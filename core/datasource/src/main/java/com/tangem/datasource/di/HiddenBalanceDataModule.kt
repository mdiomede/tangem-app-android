package com.tangem.datasource.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.tangem.datasource.local.appcurrency.HiddenBalanceStore
import com.tangem.datasource.local.appcurrency.implementation.HiddenBalanceStateStore
import com.tangem.datasource.local.datastore.SharedPreferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object HiddenBalanceDataModule {

    @Provides
    fun provideHiddenBalanceStateStore(
        @ApplicationContext context: Context,
        @NetworkMoshi moshi: Moshi,
    ): HiddenBalanceStore {
        return HiddenBalanceStateStore(
            dataStore = SharedPreferencesDataStore(
                preferencesName = "is_balance_hidden",
                context = context,
                adapter = moshi.adapter(Boolean::class.java),
            ),
        )
    }
}
