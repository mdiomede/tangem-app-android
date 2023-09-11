package com.tangem.datasource.local.appcurrency.implementation

import com.tangem.datasource.local.appcurrency.HiddenBalanceStore
import com.tangem.datasource.local.datastore.core.KeylessDataStoreDecorator
import com.tangem.datasource.local.datastore.core.StringKeyDataStore

internal class HiddenBalanceStateStore(
    dataStore: StringKeyDataStore<Boolean>,
) : HiddenBalanceStore, KeylessDataStoreDecorator<Boolean>(dataStore) {

    override suspend fun getSyncOrFalse(): Boolean {
        return getSyncOrNull() ?: false
    }

}
