package com.blummock.cryptowallet.presentation.screen.login.vm

internal sealed interface LoginScreenAction {

    data class EmailChanged(val text: String) : LoginScreenAction
    data class OtpChanged(val text: String) : LoginScreenAction
    data object DismissOtp : LoginScreenAction
    data object SendOtp : LoginScreenAction
    data object ReSendOtp : LoginScreenAction
    data object VerifyOtp : LoginScreenAction
}