package com.blummock.cryptowallet.presentation.screen.splash.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.blummock.cryptowallet.R
import com.blummock.cryptowallet.presentation.screen.splash.vm.SplashEffect
import com.blummock.cryptowallet.presentation.screen.splash.vm.SplashViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

@Composable
internal fun SplashScreen(
    viewModel: SplashViewModel,
    navigateToDetails: () -> Unit,
    navigateToLogin: () -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowOn(Dispatchers.Main.immediate)
            .collect {
                when (it) {
                    is SplashEffect.IsAuth -> {
                        if (it.boolean) {
                            navigateToDetails()
                        } else {
                            navigateToLogin()
                        }
                    }

                    is SplashEffect.Snack -> snackbarHostState.showSnackbar(it.message)
                }
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading),
            )
        }
    }
}