package com.blummock.cryptowallet.presentation.screen.details.vm

import androidx.lifecycle.viewModelScope
import com.blummock.cryptowallet.data.AuthWalletRepository
import com.blummock.cryptowallet.data.WalletRepository
import com.blummock.cryptowallet.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
internal class WalletDetailsViewModel @Inject constructor(
    private val repository: WalletRepository,
    private val authWalletRepository: AuthWalletRepository,
) : BaseViewModel<WalletDetailsState, WalletDetailsEffect, WalletDetailsAction>(WalletDetailsState()) {

    init {
        observeAuth()
        loadData()
    }

    override fun onAction(action: WalletDetailsAction) {
        when (action) {
            WalletDetailsAction.LogOut -> logOut()
            WalletDetailsAction.SendTransaction -> goToSendTransactions()
            WalletDetailsAction.Refresh -> loadData()
        }
    }


    private fun goToSendTransactions() {
        viewModelScope.launch {
            postEffect(WalletDetailsEffect.NavigateToSendTransaction(state.value.address))
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            authWalletRepository.logOut()
                .onFailure { postEffect(WalletDetailsEffect.Snack(it.message.orEmpty())) }
        }
    }

    private fun observeAuth() {
        viewModelScope.launch {
            authWalletRepository.observeIsAuthenticated()
                .collect { isAuthenticated ->
                    if (!isAuthenticated) {
                        postEffect(WalletDetailsEffect.NavigateToLogin)
                    }
                }
        }
    }

    private fun loadData() {
        updateState { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            repository.getWallet()
                .onSuccess { (wallet, balance) ->
                    val hasMoney = runCatching { balance.toBigDecimal() > BigDecimal.ZERO }
                        .getOrDefault(false)
                    updateState {
                        it.copy(
                            address = wallet.address,
                            balance = Balance(balance, hasMoney),
                            isRefreshing = false,
                        )
                    }
                }
                .onFailure {
                    postEffect(WalletDetailsEffect.Snack(it.message.orEmpty()))
                    updateState { it.copy(isRefreshing = false) }
                }
        }
    }
}