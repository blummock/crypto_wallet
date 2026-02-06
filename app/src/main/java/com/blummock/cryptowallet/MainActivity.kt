package com.blummock.cryptowallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.blummock.cryptowallet.presentation.navigation.AppNavHost
import com.blummock.cryptowallet.presentation.theme.CryptoWalletTheme
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.UI.DynamicUI
import com.dynamic.sdk.android.core.ClientProps
import com.dynamic.sdk.android.core.LoggerLevel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initDynamicSdk()
        }
        enableEdgeToEdge()
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                CryptoWalletTheme {
                    AppNavHost()
                }
                DynamicUI()  // Required overlay for auth flows
            }
        }
    }

    private fun initDynamicSdk() {
        try {
            val props = ClientProps(
                environmentId = BuildConfig.ENVIRONMENT_ID,
                appLogoUrl = BuildConfig.APP_LOGO_URL,
                appName = BuildConfig.APP_NAME,
                redirectUrl = BuildConfig.REDIRECT_URL,
                appOrigin = BuildConfig.APP_ORIGIN,
                logLevel = if (BuildConfig.DEBUG) LoggerLevel.DEBUG else LoggerLevel.INFO
            )
            DynamicSDK.initialize(props, applicationContext, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}