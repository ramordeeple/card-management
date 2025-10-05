package com.pm.bankcards.service.api;

import com.pm.bankcards.dto.transfer.TransferRequest;
import com.pm.bankcards.entity.Transfer;

public interface TransferService {
    Transfer doOwnTransfer(TransferRequest req, Long currentUserId);
}
