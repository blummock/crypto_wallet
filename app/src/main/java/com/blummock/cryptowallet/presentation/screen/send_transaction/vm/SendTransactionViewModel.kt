package com.blummock.cryptowallet.presentation.screen.send_transaction.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.blummock.cryptowallet.data.WalletRepository
import com.blummock.cryptowallet.presentation.base.BaseViewModel
import com.blummock.cryptowallet.presentation.navigation.AppRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
internal class SendTransactionViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<SendTransactionState, SendTransactionEffect, SendTransactionAction>(SendTransactionState()) {

    private val senderAddress: String = checkNotNull(savedStateHandle[AppRoute.SendTransaction.WALLET_ADDRESS])

    init {
        updateState { it.copy(senderAddress = senderAddress) }
    }

    override fun onAction(action: SendTransactionAction) {
        when (action) {
            is SendTransactionAction.AmountChanged -> amountChanged(action.text)
            SendTransactionAction.GoBack -> goBack()
            SendTransactionAction.PerformTransaction -> performTransaction()
            is SendTransactionAction.RecipientAddressChanged -> recipientChanged(action.text)
        }
    }

    private fun filterAmount(input: String): String {
        val regex = Regex("^\\d*(?:[.]\\d{0,8})?$")
        return if (regex.matches(input)) input else input.dropLast(1)
    }

    private fun validateAmount(input: String) =
        input.isNotBlank() &&
                runCatching { input.toBigDecimal() > BigDecimal.ZERO }.getOrDefault(false)

    private fun amountChanged(text: String) {
        val amount = filterAmount(text)
        updateState { it.copy(amount = it.amount.copy(amount = amount, isValid = validateAmount(amount))) }
    }

    private fun isValidAddress(address: String) = address.matches(Regex("^0x[a-fA-F0-9]{40}$"))

    private fun recipientChanged(text: String) {
        updateState {
            it.copy(
                recipientAddress = it.recipientAddress.copy(
                    address = text,
                    isValid = isValidAddress(text)
                )
            )
        }
    }

    private fun goBack() {
        viewModelScope.launch {
            postEffect(SendTransactionEffect.GoBack)
        }
    }

    private fun performTransaction() {
        updateState { it.copy(isLoading = true, error = "") }
        val state = state.value
        viewModelScope.launch {
            walletRepository.performTransaction(
                from = state.senderAddress,
                to = state.recipientAddress.address,
                amount = state.amount.amount,
            ).onSuccess { transactionHash ->
                updateState { it.copy(transactionHash = transactionHash, isLoading = false) }
            }.onFailure { exception ->
                updateState { it.copy(isLoading = false, error = exception.message + exception.cause) }
            }
        }
    }
}