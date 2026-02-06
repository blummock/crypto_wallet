package com.blummock.cryptowallet.presentation.navigation

import LoginScreen
import SendTransactionScreen
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blummock.cryptowallet.presentation.screen.details.ui.WalletDetailsScreen
import com.blummock.cryptowallet.presentation.screen.splash.ui.SplashScreen


@Composable
internal fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Splash.route,
    ) {
        composable(AppRoute.Splash.route) {
            SplashScreen(
                viewModel = hiltViewModel(),
                navigateToDetails = {
                    navController.navigate(AppRoute.WalletDetails.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                navigateToLogin = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.Login.route) {
            LoginScreen(
                viewModel = hiltViewModel(),
                navigateToDetails = {
                    navController.navigate(AppRoute.WalletDetails.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AppRoute.WalletDetails.route) {
            WalletDetailsScreen(
                viewModel = hiltViewModel(),
                navigateToLogin = {
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                navigateSendTransaction = { walletAddress ->
                    navController.navigate(AppRoute.SendTransaction.create(walletAddress))
                }
            )
        }

        composable(AppRoute.SendTransaction.route) {
            SendTransactionScreen(
                viewModel = hiltViewModel(),
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}