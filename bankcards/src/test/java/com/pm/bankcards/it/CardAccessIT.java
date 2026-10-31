package com.pm.bankcards.it;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.exception.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardAccessIT extends IntegrationTestSupport {
    @Test
    void foreignCardDetailsMustBeDeniedOnColdCache() throws Exception {
        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(stranger))))
                .andExpect(status().isNotFound());
    }

    @Test
    void foreignCardDetailsMustBeDeniedOnWarmCache() throws Exception {
        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(owner))))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/cards/" + from.getId()).with(user(principal(stranger))))
                .andExpect(status().isNotFound());
    }

    @Test
    void foreignBalanceIsDenied() throws Exception {
        mvc.perform(get("/api/v1/cards/" + from.getId() + "/balance").with(user(principal(stranger))))
                .andExpect(status().isNotFound());
    }

    @Test
    void cardListContainsOnlyOwnedCards() throws Exception {
        createCard(stranger);
        mvc.perform(get("/api/v1/cards").with(user(principal(owner))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void transferCannotDebitForeignCard() {
        assertThatThrownBy(() -> transfers.doOwnTransfer(request(from, to, "10"), stranger.getId()))
                .isInstanceOf(BusinessException.class);
        assertConserved();
        assertThat(ledger.count()).isZero();
    }

    @Test
    void foreignCardCannotBeBlockedOrUsedForLoadTest() throws Exception {
        mvc.perform(post("/api/v1/cards/" + from.getId() + "/block").with(user(principal(stranger))))
                .andExpect(status().isNotFound());
        mvc.perform(post("/api/v1/cards/load-test/" + from.getId()).with(user(principal(stranger))))
                .andExpect(status().isNotFound());
    }

    @Test
    void foreignUserCannotReplayOwnersTransfer() {
        TransferRequest request = request(from, to, "10.00");
        transfers.doOwnTransfer(request, owner.getId());
        assertThatThrownBy(() -> transfers.doOwnTransfer(request, stranger.getId()))
                .isInstanceOf(BusinessException.class);
        assertThat(ledger.count()).isEqualTo(1);
    }

    @Test
    void anonymousUserCannotReadCards() throws Exception {
        mvc.perform(get("/api/v1/cards/" + from.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void foreignRecipientAndTopUpAreRejected() {
        Card foreign = createCard(stranger);
        assertThatThrownBy(() -> transfers.doOwnTransfer(request(from, foreign, "10.00"), owner.getId()))
                .isInstanceOf(BusinessException.class);

        assertThatThrownBy(() -> transfers.topUp(
                foreign.getId(),
                owner.getId(),
                BigDecimal.TEN,
                UUID.randomUUID()
                        .toString()))
                .isInstanceOf(BusinessException.class);

        assertThat(balance(foreign)).isEqualByComparingTo("100.00");
        assertThat(ledger.count()).isZero();
        assertConserved();
    }
}
