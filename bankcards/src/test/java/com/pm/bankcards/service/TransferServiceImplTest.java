package com.pm.bankcards.service;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Card;
import com.pm.bankcards.entity.CardStatus;
import com.pm.bankcards.entity.Transfer;
import com.pm.bankcards.entity.User;
import com.pm.bankcards.exception.BusinessException;
import com.pm.bankcards.repository.CardRepository;
import com.pm.bankcards.repository.TransferRepository;
import com.pm.bankcards.service.impl.TransferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    private CardRepository cardRepo;
    private TransferRepository transferRepo;
    private TransferServiceImpl service;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        cardRepo = mock(CardRepository.class);
        transferRepo = mock(TransferRepository.class);
        service = new TransferServiceImpl(transferRepo, cardRepo, List.of()); // правила не тестируем здесь
    }

    @Test
    void shouldTransferFundsAndPersist() {
        var req = new TransferRequest(10L, 20L, new BigDecimal("30.00"), "rid-1");
        var from = cardOf(USER_ID, CardStatus.ACTIVE, "100.00");
        var to   = cardOf(USER_ID, CardStatus.ACTIVE, "10.00");

        when(transferRepo.existsByRequestId("rid-1")).thenReturn(false);
        when(cardRepo.lockByIdAndOwnerId(10L, USER_ID)).thenReturn(Optional.of(from));
        when(cardRepo.lockByIdAndOwnerId(20L, USER_ID)).thenReturn(Optional.of(to));

        var captor = ArgumentCaptor.forClass(Transfer.class);
        when(transferRepo.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        var saved = service.doOwnTransfer(req, USER_ID);

        assertThat(from.getBalance()).isEqualByComparingTo("70.00");
        assertThat(to.getBalance()).isEqualByComparingTo("40.00");
        assertThat(saved.getAmount()).isEqualByComparingTo("30.00");
        assertThat(saved.getRequestId()).isEqualTo("rid-1");
        verify(transferRepo).save(any(Transfer.class));
    }

    @Test
    void shouldReturnExistingTransferWhenIdempotent() {
        var req = new TransferRequest(10L, 20L, new BigDecimal("30.00"), "rid-2");
        var existing = new Transfer();
        existing.setAmount(new BigDecimal("30.00"));
        existing.setRequestId("rid-2");

        when(transferRepo.existsByRequestId("rid-2")).thenReturn(true);
        when(transferRepo.findByRequestId("rid-2")).thenReturn(Optional.of(existing));

        var result = service.doOwnTransfer(req, USER_ID);

        assertThat(result).isSameAs(existing);
        verifyNoInteractions(cardRepo);
        verify(transferRepo, never()).save(any());
    }

    @Test
    void shouldFailWhenSourceCardNotFoundOrNotOwned() {
        var req = new TransferRequest(10L, 20L, new BigDecimal("10.00"), "rid-3");

        when(transferRepo.existsByRequestId("rid-3")).thenReturn(false);
        when(cardRepo.lockByIdAndOwnerId(10L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.doOwnTransfer(req, USER_ID))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Карта отправителя не найдена");

        verify(transferRepo, never()).save(any());
    }

    private static Card cardOf(Long ownerId, CardStatus status, String balance) {
        var owner = new User();
        owner.setUsername("u" + ownerId);
        owner.setPasswordHash("x");
        owner.setEnabled(true);

        var c = new Card();
        c.setOwner(owner);
        c.setStatus(status);
        c.setBalance(new BigDecimal(balance));
        c.setLast4("4242");
        c.setCardNumberEnc("enc-4242");
        c.setExpiryMonth(12);
        c.setExpiryYear(2030);
        return c;
    }
}
