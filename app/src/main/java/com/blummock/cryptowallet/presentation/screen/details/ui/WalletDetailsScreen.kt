package com.blummock.cryptowallet.presentation.screen.details.ui

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blummock.cryptowallet.R
import com.blummock.cryptowallet.presentation.screen.details.vm.Balance
import com.blummock.cryptowallet.presentation.screen.details.vm.WalletDetailsAction
import com.blummock.cryptowallet.presentation.screen.details.vm.WalletDetailsEffect
import com.blummock.cryptowallet.presentation.screen.details.vm.WalletDetailsState
import com.blummock.cryptowallet.presentation.screen.details.vm.WalletDetailsViewModel
import com.blummock.cryptowallet.presentation.theme.CryptoWalletTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@Composable
internal fun WalletDetailsScreen(
    viewModel: WalletDetailsViewModel,
    navigateToLogin: () -> Unit,
    navigateSendTransaction: (walletAddress: String) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effect
            .flowOn(Dispatchers.Main.immediate)
            .collect { effect ->
                when (effect) {
                    is WalletDetailsEffect.Snack -> {
                        snackbarHostState.showSnackbar(
                            message = effect.msg,
                            withDismissAction = true
                        )
                    }

                    is WalletDetailsEffect.NavigateToSendTransaction -> navigateSendTransaction(effect.walletAddress)
                    WalletDetailsEffect.NavigateToLogin -> navigateToLogin()
                }
            }
    }
    WalletDetailsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onAction = viewModel::onAction,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun WalletDetailsScreenContent(
    state: WalletDetailsState,
    snackbarHostState: SnackbarHostState,
    onAction: (WalletDetailsAction) -> Unit,
) {
    val refreshState = rememberPullToRefreshState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = stringResource(R.string.wallet_details),
                        fontWeight = FontWeight.Medium,
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { innerPadding ->

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = state.isRefreshing,
            state = refreshState,
            onRefresh = { onAction(WalletDetailsAction.Refresh) },
        ) {

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CryptoWalletTheme.colors.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Surface(
                            color = CryptoWalletTheme.colors.tertiary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = state.chain,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = CryptoWalletTheme.colors.accent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        DetailItem(label = stringResource(R.string.address), value = state.address, isAddress = true)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                        DetailItem(label = stringResource(R.string.current_network), value = state.network)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 0.5.dp)

                        Text(
                            stringResource(R.string.balance),
                            color = CryptoWalletTheme.colors.textSecondary,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "${state.balance.balance} ${state.currency}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = CryptoWalletTheme.colors.accent,
                        )
                    }
                }

                val clipboard = LocalClipboard.current
                val scope = rememberCoroutineScope()
                val context = LocalContext.current


                ActionButton(
                    text = stringResource(R.string.copy_address),
                    icon = Icons.Filled.ContentCopy,
                    contentColor = CryptoWalletTheme.colors.textPrimary,
                    onCLick = {
                        if (state.address.isBlank()) return@ActionButton
                        scope.launch {
                            val clipData = ClipData
                                .newPlainText(context.getString(R.string.wallet_address), state.address)
                            val clipEntry = ClipEntry(clipData)
                            clipboard.setClipEntry(clipEntry)
                            snackbarHostState
                                .showSnackbar(context.getString(R.string.address_copied_to_clipboard))
                        }
                    }
                )

                Button(
                    onClick = { onAction(WalletDetailsAction.SendTransaction) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = state.isEnabledTransaction,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CryptoWalletTheme.colors.accent)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.send_transaction), fontSize = 16.sp)
                }

                ActionButton(
                    text = stringResource(R.string.logout),
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    contentColor = CryptoWalletTheme.colors.warning,
                    onCLick = { onAction(WalletDetailsAction.LogOut) }
                )
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String, isAddress: Boolean = false) {
    Column {
        Text(text = label, color = CryptoWalletTheme.colors.textSecondary, fontSize = 14.sp)
        Text(
            text = value,
            fontSize = if (isAddress) 13.sp else 16.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            color = CryptoWalletTheme.colors.textPrimary,
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: ImageVector,
    contentColor: Color,
    onCLick: () -> Unit
) {
    OutlinedCard(
        onClick = onCLick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = CryptoWalletTheme.colors.surface,
        ),
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Icon(
                    icon,
                    contentDescription = text,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = text,
                    color = contentColor,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = CryptoWalletTheme.colors.textPlaceholder,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun WalletDetailsScreenPreview() {
    CryptoWalletTheme {
        WalletDetailsScreenContent(
            state = WalletDetailsState(
                address = "dni23n4ijni3ndnw",
                balance = Balance(
                    balance = "100.00",
                    hasMoney = true
                ),
                isRefreshing = false,
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {},
        )
    }
}