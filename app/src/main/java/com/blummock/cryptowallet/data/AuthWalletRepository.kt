package com.blummock.cryptowallet.data

import com.dynamic.sdk.android.DynamicSDK
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.cancellation.CancellationException

internal class AuthWalletRepository @Inject constructor(
    private val sdk: DynamicSDK,
    @param:Named("IO")
    private val dispatcher: CoroutineDispatcher
) {

    suspend fun sendOtp(email: String): Result<Unit> = withContext(dispatcher) {
        runSafe { sdk.auth.email.sendOTP(email) }
    }

    suspend fun reSendOtp(): Result<Unit> = withContext(dispatcher) {
        runSafe { sdk.auth.email.resendOTP() }
    }

    suspend fun verifyOtp(otp: String): Result<Unit> = withContext(dispatcher) {
        runSafe { sdk.auth.email.verifyOTP(otp) }
    }

    suspend fun isAuthenticated() = withContext(dispatcher) {
        runSafe {
            sdk.sdk.readyChanges.first { it }
            sdk.auth.token != null
        }
    }

    suspend fun logOut() = withContext(dispatcher) {
        runSafe { sdk.auth.logout() }
    }

    fun observeIsAuthenticated() = sdk.auth.authenticatedUserChanges.map {
        it != null
    }.flowOn(dispatcher)
}

private inline fun <R> runSafe(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) {
            throw e
        } else {
            Result.failure(e)
        }
    }
}