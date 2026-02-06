import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blummock.cryptowallet.R
import com.blummock.cryptowallet.presentation.screen.login.ui.OtpVerificationBSScreen
import com.blummock.cryptowallet.presentation.screen.login.vm.Email
import com.blummock.cryptowallet.presentation.screen.login.vm.LoginEffect
import com.blummock.cryptowallet.presentation.screen.login.vm.LoginScreenAction
import com.blummock.cryptowallet.presentation.screen.login.vm.LoginState
import com.blummock.cryptowallet.presentation.screen.login.vm.LoginViewModel
import com.blummock.cryptowallet.presentation.screen.login.vm.OtpState
import com.blummock.cryptowallet.presentation.theme.CryptoWalletTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

@Composable
internal fun LoginScreen(viewModel: LoginViewModel, navigateToDetails: () -> Unit) {

    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect
            .flowOn(Dispatchers.Main.immediate)
            .collect { effect ->
                when (effect) {
                    LoginEffect.GoToDetails -> navigateToDetails()
                    is LoginEffect.Snack -> snackbarHostState.showSnackbar(
                        message = effect.msg,
                        withDismissAction = true
                    )
                }
            }
    }

    LoginScreenContent(state, snackbarHostState, viewModel::onAction)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreenContent(
    state: LoginState,
    snackbarHostState: SnackbarHostState,
    onAction: (LoginScreenAction) -> Unit,
) {
    val otpSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Default.AccountBalanceWallet,
                contentDescription = stringResource(R.string.logo),
                tint = CryptoWalletTheme.colors.accent,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.crypto_wallet),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.please_sign_in_to_continue),
                fontSize = 18.sp,
                color = CryptoWalletTheme.colors.textSecondary,
            )

            Spacer(modifier = Modifier.height(48.dp))

            val focusRequester = remember { FocusRequester() }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = state.email.email,
                onValueChange = { onAction(LoginScreenAction.EmailChanged(it)) },
                label = { Text(stringResource(R.string.email)) },
                placeholder = {
                    Text(
                        stringResource(R.string.you_example_com),
                        color = CryptoWalletTheme.colors.textPlaceholder,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CryptoWalletTheme.colors.accent,
                    unfocusedBorderColor = CryptoWalletTheme.colors.unfocused,
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            val focusManager = LocalFocusManager.current

            Button(
                onClick = {
                    focusManager.clearFocus()
                    onAction(LoginScreenAction.SendOtp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = state.email.isValid && !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptoWalletTheme.colors.accent
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(
                        text = stringResource(R.string.send_email_otp),
                        fontSize = 18.sp,
                        color = CryptoWalletTheme.colors.textButton,
                    )
                }
            }
        }

        when (state.otpState) {
            is OtpState.Expanded -> {
                ModalBottomSheet(
                    sheetState = otpSheetState,
                    onDismissRequest = {
                        onAction(LoginScreenAction.DismissOtp)
                    }
                ) {
                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(hostState = snackbarHostState)
                        }
                    ) { paddingValues ->
                        OtpVerificationBSScreen(
                            modifier = Modifier
                                .padding(paddingValues)
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            email = state.email.email,
                            otpState = state.otpState,
                            isLoading = state.isLoading,
                            onOtpChanged = { onAction(LoginScreenAction.OtpChanged(it)) },
                            onVerify = { onAction(LoginScreenAction.VerifyOtp) },
                            onResend = { onAction(LoginScreenAction.ReSendOtp) },
                        )
                    }
                }
            }

            is OtpState.Hidden -> Unit
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginScreenContentPreview() {
    CryptoWalletTheme {
        LoginScreenContent(
            LoginState(
                email = Email(
                    isValid = true,
                ),
                otpState = OtpState.Hidden,
                isLoading = false,
            ),
            onAction = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}