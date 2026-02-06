package com.blummock.cryptowallet.presentation.screen.login.vm

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.blummock.cryptowallet.data.AuthWalletRepository
import com.blummock.cryptowallet.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val repository: AuthWalletRepository,
) : BaseViewModel<LoginState, LoginEffect, LoginScreenAction>(LoginState()) {

    init {
        observeLogin()
    }

    override fun onAction(action: LoginScreenAction) {
        when (action) {
            is LoginScreenAction.EmailChanged -> changeEmail(action.text)
            LoginScreenAction.VerifyOtp -> verifyOtp()
            LoginScreenAction.SendOtp -> sendOtp()
            is LoginScreenAction.OtpChanged -> otpChanged(action.text)
            LoginScreenAction.DismissOtp -> dismissOtp()
            LoginScreenAction.ReSendOtp -> resendOtp()
        }
    }

    private fun observeLogin() {
        viewModelScope.launch {
            repository.observeIsAuthenticated().collectLatest { isAuthenticated ->
                if (isAuthenticated) {
                    postEffect(LoginEffect.GoToDetails)
                }
            }
        }
    }

    private fun dismissOtp() {
        updateState { state -> state.copy(otpState = OtpState.Hidden) }
    }

    private fun otpChanged(text: String) {
        val isValid = text.isNotBlank() && text.length == OTP_LENGTH
        updateState { state -> state.copy(otpState = OtpState.Expanded(otp = Otp(text, isValid))) }
    }

    private fun changeEmail(text: String) {
        val isValid = text.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(text).matches()
        updateState { state -> state.copy(email = Email(text, isValid)) }
    }

    private fun sendOtp() {
        viewModelScope.launch {
            updateState { state -> state.copy(isLoading = true) }
            repository.sendOtp(state.value.email.email)
                .onSuccess {
                    updateState { state ->
                        state.copy(
                            isLoading = false,
                            otpState = OtpState.Expanded(),
                        )
                    }
                }
                .onFailure {
                    updateState { state -> state.copy(isLoading = false) }
                    postEffect(LoginEffect.Snack(it.message.orEmpty()))
                }
        }
    }

    private fun resendOtp() {
        updateState { state -> state.copy(otpState = state.otpState.ifExpanded { copy(isResentOtp = true) }) }
        viewModelScope.launch {
            repository.reSendOtp()
                .onSuccess {
                    delay(3.seconds)
                    updateState { state ->
                        state.copy(otpState = state.otpState.ifExpanded { copy(isResentOtp = false) })
                    }
                }
                .onFailure {
                    postEffect(LoginEffect.Snack(it.message.orEmpty()))
                    updateState { state ->
                        state.copy(otpState = state.otpState.ifExpanded { copy(isResentOtp = false) })
                    }
                }
        }
    }

    private fun verifyOtp() {
        viewModelScope.launch {
            updateState { state -> state.copy(isLoading = true) }
            repository.verifyOtp((state.value.otpState as? OtpState.Expanded)?.otp?.otp ?: return@launch)

                .onFailure {
                    postEffect(LoginEffect.Snack(it.message.orEmpty()))
                    updateState { state -> state.copy(isLoading = false) }
                }
        }
    }

    companion object {
        internal const val OTP_LENGTH = 6
    }
}