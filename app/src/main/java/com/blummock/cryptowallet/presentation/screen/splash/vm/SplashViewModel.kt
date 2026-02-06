package com.blummock.cryptowallet.presentation.screen.splash.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blummock.cryptowallet.data.AuthWalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    val authWalletRepository: AuthWalletRepository
) : ViewModel() {

    private val _effect = Channel<SplashEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            authWalletRepository.isAuthenticated()
                .onSuccess { _effect.send(SplashEffect.IsAuth(it)) }
                .onFailure { _effect.send(SplashEffect.Snack(it.message ?: "Unknown error")) }
        }
    }
}