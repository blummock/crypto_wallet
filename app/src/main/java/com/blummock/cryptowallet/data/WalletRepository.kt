package com.blummock.cryptowallet.data

import com.dynamic.sdk.android.Chains.EVM.EthereumTransaction
import com.dynamic.sdk.android.Chains.EVM.convertEthToWei
import com.dynamic.sdk.android.DynamicSDK
import com.dynamic.sdk.android.Models.Network
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Named
import kotlin.time.Duration.Companion.seconds

internal class WalletRepository @Inject constructor(
    private val sdk: DynamicSDK,
    @param:Named("IO")
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun performTransaction(from: String, to: String, amount: String) = withContext(dispatcher) {
        try {
            val wallet = sdk.wallets.userWallets.firstOrNull { it.address == from }
            checkNotNull(wallet)

            // Get chain ID from network
            val chainId = sdk.wallets.getNetwork(wallet).value.jsonPrimitive.int

            // Create EVM client
            val client = sdk.evm.createPublicClient(chainId)

            // Get gas price
            val gasPrice = client.getGasPrice()

            // Calculate max fee per gas (2x gas price for EIP-1559)
            val maxFeePerGas = gasPrice * BigInteger.valueOf(2)

            // Convert amount to wei
            val weiAmount = convertEthToWei(amount)

            // Create transaction
            val transaction = EthereumTransaction(
                from = from,
                to = to,
                value = weiAmount,
                gas = BigInteger.valueOf(21000), // Standard gas limit for ETH transfer
                maxFeePerGas = maxFeePerGas,
                maxPriorityFeePerGas = gasPrice,
            )

            // Send transaction
            val txHash = sdk.evm.sendTransaction(transaction, wallet)
            Result.success(txHash)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getWallet(chain: String = "EVM", chainId: Int = 11155111) =
        withContext(dispatcher) {
            try {
                // sometimes not ready
                val wallet = withTimeoutOrNull(5.seconds) {
                    sdk.wallets.userWalletsChanges
                        .map { it.firstOrNull { wallet -> wallet.chain.uppercase() == chain } }
                        .filterNotNull()
                        .first()
                }

                checkNotNull(wallet)

                // Switch to the correct network
                if (sdk.wallets.getNetwork(wallet).value.jsonPrimitive.int != chainId) {
                    sdk.wallets.switchNetwork(wallet, Network.evm(chainId))
                }
                val balance = sdk.wallets.getBalance(wallet)
                Result.success(wallet to balance)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}