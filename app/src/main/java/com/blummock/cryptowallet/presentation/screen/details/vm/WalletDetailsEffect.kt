package com.blummock.cryptowallet.presentation.screen.details.vm

internal sealed interface WalletDetailsEffect {

    data class Snack(val msg: String) : WalletDetailsEffect
    data class NavigateToSendTransaction(val walletAddress: String) : WalletDetailsEffect
    data object NavigateToLogin : WalletDetailsEffect
}