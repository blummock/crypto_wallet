package com.blummock.cryptowallet.presentation.screen.send_transaction.vm

internal sealed interface SendTransactionAction {

    data object GoBack : SendTransactionAction
    data object PerformTransaction : SendTransactionAction
    data class AmountChanged(val text: String) : SendTransactionAction
    data class RecipientAddressChanged(val text: String) : SendTransactionAction
}