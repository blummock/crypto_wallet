package com.blummock.cryptowallet.presentation.screen.send_transaction.vm

internal sealed interface SendTransactionEffect {

    data class Snack(val msg: String) : SendTransactionEffect
    data object GoBack : SendTransactionEffect
}