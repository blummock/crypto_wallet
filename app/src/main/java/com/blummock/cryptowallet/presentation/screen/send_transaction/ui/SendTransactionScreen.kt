import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blummock.cryptowallet.R
import com.blummock.cryptowallet.presentation.screen.send_transaction.vm.Amount
import com.blummock.cryptowallet.presentation.screen.send_transaction.vm.RecipientAddress
import com.blummock.cryptowallet.presentation.screen.send_transaction.vm.SendTransactionAction
import com.blummock.cryptowallet.presentation.screen.send_transaction.vm.SendTransactionEffect
import com.blummock.cryptowallet.presentation.screen.send_transaction.vm.SendTransactionState
import com.blummock.cryptowallet.presentation.screen.send_transaction.vm.SendTransactionViewModel
import com.blummock.cryptowallet.presentation.theme.CryptoWalletTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn


@Composable
internal fun SendTransactionScreen(
    viewModel: SendTransactionViewModel,
    navigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect
            .flowOn(Dispatchers.Main.immediate)
            .collect { effect ->
                when (effect) {
                    SendTransactionEffect.GoBack -> navigateBack()
                    is SendTransactionEffect.Snack -> snackbarHostState.showSnackbar(
                        message = effect.msg,
                        withDismissAction = true
                    )
                }
            }
    }
    SendTransactionScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SendTransactionScreenContent(
    state: SendTransactionState,
    snackbarHostState: SnackbarHostState,
    onAction: (SendTransactionAction) -> Unit,
) {
    var enabledBack by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.send_transaction), fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(
                        enabled = enabledBack,
                        onClick = {
                            enabledBack = false
                            onAction(SendTransactionAction.GoBack)
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            TransactionInputField(
                label = stringResource(R.string.recipient_address),
                enabled = !state.isLoading && !state.isCompleted,
                value = state.recipientAddress.address,
                onValueChange = { onAction(SendTransactionAction.RecipientAddressChanged(it)) }
            )

            TransactionInputField(
                label = stringResource(R.string.amount_eth),
                enabled = !state.isLoading && !state.isCompleted,
                value = state.amount.amount,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                ),
                onValueChange = { onAction(SendTransactionAction.AmountChanged(it)) }
            )

            Button(
                onClick = { onAction(SendTransactionAction.PerformTransaction) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !state.isCompleted && state.amount.isValid &&
                        state.recipientAddress.isValid && !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CryptoWalletTheme.colors.accent)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.send_transaction), fontSize = 18.sp)
                }
            }
            if (state.isCompleted) {
                TransactionSuccessCard(
                    transactionHash = state.transactionHash,
                )
            }
            if (state.error.isNotBlank()) {
                ErrorNotificationCard(message = state.error)
            }
        }
    }
}

@Composable
fun TransactionInputField(
    label: String,
    value: String,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, color = CryptoWalletTheme.colors.textSecondary, fontSize = 16.sp)
        OutlinedTextField(
            value = value,
            enabled = enabled,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions,
        )
    }
}

@Composable
private fun ErrorNotificationCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CryptoWalletTheme.colors.errorSurface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = CryptoWalletTheme.colors.warning,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                color = CryptoWalletTheme.colors.warning,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun TransactionSuccessCard(transactionHash: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CryptoWalletTheme.colors.successSurface),
        border = BorderStroke(1.dp, CryptoWalletTheme.colors.success)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(CryptoWalletTheme.colors.success, CircleShape),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Default.Check,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))

                Text(
                    stringResource(R.string.transaction_success),
                    color = CryptoWalletTheme.colors.success,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = CryptoWalletTheme.colors.textPlaceholder
            )

            Text(
                stringResource(R.string.transaction_hash),
                color = CryptoWalletTheme.colors.textSecondary,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = CryptoWalletTheme.colors.tertiary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        text = transactionHash,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        fontSize = 16.sp,
                        color = CryptoWalletTheme.colors.textSecondary,
                    )
                }
                IconButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = { /* Copy hash */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = CryptoWalletTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            TextButton(
                onClick = { },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    stringResource(R.string.view_on_etherscan),
                    color = CryptoWalletTheme.colors.accent,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}


@Preview
@Composable
private fun SendTransactionScreenPreview() {
    CryptoWalletTheme {
        SendTransactionScreenContent(
            state = SendTransactionState(
                recipientAddress = RecipientAddress(
                    address = "0x400ee04b31cb1ac06094bba7ab48a913979f5f37",
                ),
                amount = Amount(
                    amount = "1000",
                    isValid = true,
                ),
                error = "Some unknown error"
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {},
        )
    }
}
