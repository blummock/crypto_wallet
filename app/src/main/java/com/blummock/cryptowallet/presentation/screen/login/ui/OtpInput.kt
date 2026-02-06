package com.blummock.cryptowallet.presentation.screen.login.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blummock.cryptowallet.presentation.theme.CryptoWalletTheme

@Composable
internal fun OtpInput(
    modifier: Modifier = Modifier,
    otpLength: Int = 6,
    value: String,
    onValueChange: (String) -> Unit,
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= otpLength && newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                repeat(otpLength) { index ->
                    OtpCell(
                        char = value.getOrNull(index),
                        isFocused = value.length == index
                    )
                }
            }
        },
    )
}

@Composable
private fun OtpCell(
    char: Char?,
    isFocused: Boolean
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .border(
                width = 1.dp,
                color = if (isFocused) CryptoWalletTheme.colors.accent else CryptoWalletTheme.colors.unfocused,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char?.toString() ?: "_",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (char == null) CryptoWalletTheme.colors.textPlaceholder else MaterialTheme.colorScheme.onBackground,
        )
    }
}

