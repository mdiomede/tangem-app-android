package com.tangem.lib.crypto.models

/**
 * Analytics data for send events in analytics engine
 *
 * @property feeType type of fee (min,max,normal)
 * @property additionalAnalyticsParam custom params
 */
data class AnalyticsData(
    val feeType: String,
    val additionalAnalyticsParam: Map<String, String>
)
