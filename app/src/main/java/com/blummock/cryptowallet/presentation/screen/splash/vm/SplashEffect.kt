package com.blummock.cryptowallet.presentation.screen.splash.vm

internal sealed interface SplashEffect {
    data class Snack(val message: String) : SplashEffect
    data class IsAuth(val boolean: Boolean) : SplashEffect
}