package com.blummock.cryptowallet.presentation.screen.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blummock.cryptowallet.R
import com.blummock.cryptowallet.presentation.screen.login.vm.LoginViewModel.Companion.OTP_LENGTH
import com.blummock.cryptowallet.presentation.screen.login.vm.Otp
import com.blummock.cryptowallet.presentation.screen.login.vm.OtpState
import com.blummock.cryptowallet.presentation.theme.CryptoWalletTheme

@Composable
internal fun OtpVerificationBSScreen(
    modifier: Modifier = Modifier,
    email: String,
    otpState: OtpState.Expanded,
    onOtpChanged: (String) -> Unit,
    isLoading: Boolean = false,
    onVerify: () -> Unit,
    onResend: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, shape = RoundedCornerShape(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = CryptoWalletTheme.colors.accent,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.check_your_email), fontSize = 26.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.we_sent_a_verification_code_to),
            color = CryptoWalletTheme.colors.textSecondary,
            fontSize = 16.sp
        )
        Text(
            text = email,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(32.dp))

        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        OtpInput(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            otpLength = OTP_LENGTH,
            value = otpState.otp.otp,
            onValueChange = onOtpChanged,
        )


        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onVerify,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = otpState.otp.isValid && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CryptoWalletTheme.colors.accent)
        ) {
            if (!isLoading) {
                Text(stringResource(R.string.verify_code), fontSize = 18.sp)
            } else {
                CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!otpState.isResentOtp && !isLoading) {
            Text(stringResource(R.string.didn_t_receive_the_code), color = CryptoWalletTheme.colors.textSecondary)
            TextButton(onClick = onResend) {
                Text(
                    stringResource(R.string.resend_code),
                    color = CryptoWalletTheme.colors.accent,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun OtpVerificationBSScreenPreview() {
    CryptoWalletTheme {
        Box(
            Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            OtpVerificationBSScreen(
                email = "you@example.com",
                otpState = OtpState.Expanded(
                    otp = Otp(otp = "123")
                ),
                onOtpChanged = {},
                onVerify = {},
                onResend = {},
            )
        }
    }
}