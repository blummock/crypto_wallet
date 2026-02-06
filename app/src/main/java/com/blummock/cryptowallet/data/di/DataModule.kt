package com.blummock.cryptowallet.data.di

import com.dynamic.sdk.android.DynamicSDK
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Named


@Module
@InstallIn(SingletonComponent::class)
internal object DataModule {

    @Provides
    fun provideDynamicSdk() = DynamicSDK.getInstance()


    @Provides
    @Named("IO")
    fun provideDispatcher() = Dispatchers.IO
}