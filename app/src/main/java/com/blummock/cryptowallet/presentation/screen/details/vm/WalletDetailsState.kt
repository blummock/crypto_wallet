package com.blummock.cryptowallet.presentation.screen.details.vm

import androidx.compose.runtime.Immutable

@Immutable
internal data class WalletDetailsState(
    val chain: String = "EVM",
    val network: String = "Sepolia - 11155111",
    val address: String = "",
    val balance: Balance = Balance(),
    val currency: String = "ETH",
    val isRefreshing: Boolean = true,
) {
    val isEnabledTransaction: Boolean
        get() = address.isNotBlank() && balance.hasMoney

}

@Immutable
internal data class Balance(
    val balance: String = "",
    val hasMoney: Boolean = false,
)