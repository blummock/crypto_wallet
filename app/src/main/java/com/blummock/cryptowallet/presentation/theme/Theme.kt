package com.blummock.cryptowallet.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Immutable
internal data class AppColors(
    val surface: Color,
    val accent: Color,
    val unfocused: Color,
    val tertiary: Color,
    val warning: Color,
    val success: Color,
    val successSurface: Color,
    val errorSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textButton: Color,
    val textPlaceholder: Color,
)

private val LocalCustomAppColors = staticCompositionLocalOf<AppColors> {
    error("AppColors not provided")
}

internal object CryptoWalletTheme {
    val colors: AppColors
        @Composable get() = LocalCustomAppColors.current
}

internal val lightColors = AppColors(
    surface = Color.White,
    accent = Color(0xFF4A90E2),
    unfocused = Color.LightGray,
    tertiary = Color(0xFFE3F2FD),
    warning = Color.Red,
    success = Color(0xFF4CAF50),
    successSurface = Color(0xFFF1F9F1),
    errorSurface = Color(0xFFFFEBEE),
    textPrimary = Color.Black,
    textSecondary = Color.Gray,
    textButton = Color.White,
    textPlaceholder = Color.LightGray,
)

internal val darkColors = AppColors(
    surface = Color.DarkGray,
    accent = Color(0xFF4A90E2),
    unfocused = Color.LightGray,
    tertiary = Color(0xFFE3F2FD),
    warning = Color.Red,
    success = Color(0xFF4CAF50),
    successSurface = Color(0xFFF1F9F1),
    errorSurface = Color(0xFFFFEBEE),
    textPrimary = Color.White,
    textSecondary = Color.Gray,
    textButton = Color.White,
    textPlaceholder = Color.LightGray,
)

@Composable
internal fun CryptoWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {

    val customColors = if (darkTheme) darkColors else lightColors

    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(
        LocalCustomAppColors provides customColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}