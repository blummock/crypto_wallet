package com.blummock.cryptowallet.presentation.navigation

internal sealed interface AppRoute {

    val route: String

    data object Splash : AppRoute {
        override val route: String = "splash"
    }

    data object Login : AppRoute {
        override val route: String = "login"
    }

    data object WalletDetails : AppRoute {
        override val route: String = "details"
    }

    data object SendTransaction : AppRoute {
        const val WALLET_ADDRESS = "walletAddress"
        override val route = "sendTransaction/{$WALLET_ADDRESS}"

        fun create(walletAddress: String) = "sendTransaction/$walletAddress"
    }
}
