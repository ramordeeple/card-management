package com.pm.bankcards.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.exception.ErrorCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransferIT extends IntegrationTestSupport {
    private static final int START_TIMEOUT_SECONDS = 10;
    private static final int COMPLETION_TIMEOUT_SECONDS = 30;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void insufficientFundsRollBackBothBalancesAndLedger() {
        var transfer = request(from, to, "101.00");

        assertThatThrownBy(() -> transfers.doOwnTransfer(transfer, owner.getId()))
                .isInstanceOfSatisfying(BusinessException.class, failure ->
                        assertThat(failure.getCode()).isEqualTo(ErrorCodes.INSUFFICIENT_FUNDS));

        assertBalances("100.00", "100.00");
        assertThat(ledger.count()).isZero();
    }

    @Test
    void transferToSameCardIsRejected() {
        var transfer = request(from, from, "10.00");

        assertThatThrownBy(() -> transfers.doOwnTransfer(transfer, owner.getId()))
                .isInstanceOfSatisfying(BusinessException.class, failure ->
                        assertThat(failure.getCode()).isEqualTo(ErrorCodes.SAME_CARD));

        assertBalances("100.00", "100.00");
        assertThat(ledger.count()).isZero();
    }

    @Test
    void concurrentWithdrawalsDoNotCreateNegativeBalance() throws Exception {
        var successfulTransfers = runConcurrently(2, () -> tryOwnTransfer("80.00"));

        assertThat(successfulTransfers).containsExactlyInAnyOrder(true, false);
        assertBalances("20.00", "180.00");
        assertThat(ledger.count()).isEqualTo(1);
    }

    @Test
    void oppositeTransfersMustCompleteWithoutDeadlock() throws Exception {
        int workersPerDirection = 4;
        int transfersPerWorker = 10;
        List<Callable<Void>> batches = new ArrayList<>();

        for (int worker = 0; worker < workersPerDirection; worker++) {
            batches.add(transferBatch(from, to, transfersPerWorker));
            batches.add(transferBatch(to, from, transfersPerWorker));
        }

        runConcurrently(batches);

        assertBalances("100.00", "100.00");
        assertThat(ledger.count()).isEqualTo(2L * workersPerDirection * transfersPerWorker);
    }

    @Test
    void simultaneousTopUpsDoNotLoseMoney() throws Exception {
        int topUpCount = 8;

        runConcurrently(topUpCount, () -> {
            transfers.topUp(from.getId(), owner.getId(), BigDecimal.TEN, UUID.randomUUID().toString());
            return null;
        });

        assertBalances("180.00", "100.00");
        assertThat(ledger.count()).isEqualTo(topUpCount);
    }

    @ParameterizedTest(name = "amount={0}")
    @NullSource
    @ValueSource(strings = {"0.00", "-1.00", "0.015"})
    void invalidAmountsCannotChangeBalancesOrCreateTransfers(String amount) throws Exception {
        var transfer = new TransferRequest(
                from.getId(),
                to.getId(),
                amount == null ? null : new BigDecimal(amount),
                UUID.randomUUID().toString());

        postOwnTransfer(transfer).andExpect(status().isBadRequest());

        assertBalances("100.00", "100.00");
        assertThat(ledger.count()).isZero();
    }

    @Test
    void transferWithFractionOfCentIsRejectedByService() {
        var transfer = request(from, to, "0.015");

        assertThatThrownBy(() -> transfers.doOwnTransfer(transfer, owner.getId()))
                .isInstanceOfSatisfying(BusinessException.class, failure ->
                        assertThat(failure.getCode()).isEqualTo(ErrorCodes.AMOUNT_INVALID));

        assertBalances("100.00", "100.00");
        assertThat(ledger.count()).isZero();
    }

    @Test
    void topUpWithFractionOfCentIsRejectedByService() {
        var amount = new BigDecimal("0.015");
        String requestId = UUID.randomUUID().toString();

        assertThatThrownBy(() -> transfers.topUp(
                from.getId(),
                owner.getId(),
                amount,
                requestId))
                .isInstanceOfSatisfying(BusinessException.class,
                        failure -> assertThat(failure.getCode())
                                .isEqualTo(ErrorCodes.AMOUNT_INVALID));

        assertBalances("100.00", "100.00");
        assertThat(ledger.count()).isZero();
    }

    private boolean tryOwnTransfer(String amount) {
        try {
            transfers.doOwnTransfer(request(from, to, amount), owner.getId());
            return true;
        } catch (BusinessException failure) {
            assertThat(failure.getCode()).isEqualTo(ErrorCodes.INSUFFICIENT_FUNDS);
            return false;
        }
    }

    private ResultActions postOwnTransfer(TransferRequest transfer) throws Exception {
        return mvc.perform(post("/transfers/own")
                .with(user(principal(owner)))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(transfer)));
    }

    private Callable<Void> transferBatch(Card source, Card target, int count) {
        return () -> {
            for (int transfer = 0; transfer < count; transfer++)
                postOwnTransfer(request(source, target, "1.00")).andExpect(status().isOk());

            return null;
        };
    }

    private <T> List<T> runConcurrently(int taskCount, Callable<T> task) throws Exception {
        return runConcurrently(Collections.nCopies(taskCount, task));
    }

    private <T> List<T> runConcurrently(List<Callable<T>> tasks) throws Exception {
        var start = new CyclicBarrier(tasks.size());
        List<Callable<T>> synchronizedTasks = new ArrayList<>(tasks.size());

        for (var task : tasks) {
            synchronizedTasks.add(() -> {
                start.await(START_TIMEOUT_SECONDS, SECONDS);
                return task.call();
            });
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var completedTasks = executor
                    .invokeAll(synchronizedTasks, COMPLETION_TIMEOUT_SECONDS, SECONDS);

            List<T> results = new ArrayList<>(completedTasks.size());

            for (var task : completedTasks)
                results.add(task.get());

            return results;
        }
    }

    private void assertBalances(String expectedSourceBalance, String expectedTargetBalance) {
        assertThat(balance(from)).as("Баланс исходной карты").isEqualByComparingTo(expectedSourceBalance);
        assertThat(balance(to)).as("Баланс второй карты").isEqualByComparingTo(expectedTargetBalance);
    }
}
