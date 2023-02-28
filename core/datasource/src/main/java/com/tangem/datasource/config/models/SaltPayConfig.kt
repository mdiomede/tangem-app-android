package com.tangem.datasource.config.models

import org.spongycastle.util.encoders.Base64.toBase64String

/**
 * Created by Anton Zhilenkov on 13.10.2022.
 */
data class SaltPayConfig(
    val sprinklr: SprinklrConfig,
    val kycProvider: KYCProvider,
    val credentials: Credentials,
    val blockscoutCredentials: Credentials,
) {
    companion object {
        fun stub(): SaltPayConfig {
            return SaltPayConfig(
                sprinklr = SprinklrConfig("", ""),
                kycProvider = KYCProvider("", "", "", ""),
                credentials = Credentials("", ""),
                blockscoutCredentials = Credentials("", ""),
            )
        }
    }
}

data class KYCProvider(
    val baseUrl: String,
    val externalIdParameterKey: String,
    val sidParameterKey: String,
    val sidValue: String,
)

data class Credentials(
    val user: String,
    val password: String,
) {
    val token: String by lazy { "Basic ${toBase64String("$user:$password".toByteArray())}" }
}
