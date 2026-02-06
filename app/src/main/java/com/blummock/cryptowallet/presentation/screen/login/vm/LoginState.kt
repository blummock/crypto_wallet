package com.blummock.cryptowallet.presentation.screen.login.vm

import androidx.compose.runtime.Immutable

@Immutable
internal data class LoginState(
    val email: Email = Email(),
    val isLoading: Boolean = false,
    val otpState: OtpState = OtpState.Hidden,
)

@Immutable
internal data class Email(
    val email: String = "",
    val isValid: Boolean = false,
)

@Immutable
internal sealed interface OtpState {
    @Immutable
    data object Hidden : OtpState

    @Immutable
    data class Expanded(
        val otp: Otp = Otp(),
        val isResentOtp: Boolean = false,
    ) : OtpState
}

@Immutable
internal data class Otp(
    val otp: String = "",
    val isValid: Boolean = false,
)

internal inline fun OtpState.ifExpanded(block: OtpState.Expanded.() -> OtpState) =
    if (this is OtpState.Expanded) block() else this
