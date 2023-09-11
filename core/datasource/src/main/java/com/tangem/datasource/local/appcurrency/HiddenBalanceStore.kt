package com.tangem.datasource.local.appcurrency

import kotlinx.coroutines.flow.Flow

interface HiddenBalanceStore {

    fun get(): Flow<Boolean>

    suspend fun getSyncOrFalse(): Boolean

    suspend fun store(item: Boolean)

}
