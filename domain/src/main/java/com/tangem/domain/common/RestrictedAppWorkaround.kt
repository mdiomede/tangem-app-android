package com.tangem.domain.common

import com.tangem.common.card.CardWallet
import com.tangem.common.card.EllipticCurve
import java.util.*

/**
 * Created by Anton Zhilenkov on 28.12.2022.
 */
object RestrictedAppWorkaround {
    fun getSupportedCardIds(): List<String> = listOf(
        "AC01000000000858", // test card
        "AC01000000000130", // test card
        "AC03000000117742",
        "AC03000000117759",
        "AC03000000117767",
    )

    fun appIsExpired(): Boolean {
        val expiredCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2023)
            set(Calendar.DAY_OF_YEAR, 13)
        }
        return Calendar.getInstance().timeInMillis >= expiredCalendar.timeInMillis
    }
}

fun List<CardWallet>.selectWalletForRestrictedApp(curve: EllipticCurve? = EllipticCurve.Secp256k1): CardWallet? {
    return when (this.size) {
        0 -> null
        1 -> this[0]
        else -> {
            val walletsByCurve = this.filter { it.curve == curve }
            when (walletsByCurve.size) {
                1 -> walletsByCurve[0]
                2 -> walletsByCurve[1]
                else -> this[0]
            }
        }
    }
}
