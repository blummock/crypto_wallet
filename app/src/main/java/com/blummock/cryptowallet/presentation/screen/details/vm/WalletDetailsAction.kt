package com.blummock.cryptowallet.presentation.screen.details.vm

internal sealed interface WalletDetailsAction {

    data object LogOut : WalletDetailsAction
    data object SendTransaction : WalletDetailsAction
    data object Refresh : WalletDetailsAction
}