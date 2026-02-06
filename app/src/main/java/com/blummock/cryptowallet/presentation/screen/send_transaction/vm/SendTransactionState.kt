package com.blummock.cryptowallet.presentation.screen.send_transaction.vm

import androidx.compose.runtime.Immutable

@Immutable
internal data class SendTransactionState(
    val senderAddress: String = "",
    val recipientAddress: RecipientAddress = RecipientAddress(),
    val amount: Amount = Amount(),
    val transactionHash: String = "",
    val isLoading: Boolean = false,
    val error: String = "",
) {
    val isCompleted
        get() = transactionHash.isNotBlank()
}

@Immutable
internal data class RecipientAddress(
    val address: String = "",
    val isValid: Boolean = false,
)

@Immutable
internal data class Amount(
    val amount: String = "",
    val isValid: Boolean = false,
)