package com.blummock.cryptowallet.presentation.screen.login.vm

internal sealed interface LoginEffect {

    data class Snack(val msg: String) : LoginEffect
    data object GoToDetails : LoginEffect
}