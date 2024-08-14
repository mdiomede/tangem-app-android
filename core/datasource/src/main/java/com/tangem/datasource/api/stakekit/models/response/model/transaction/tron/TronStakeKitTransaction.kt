package com.tangem.datasource.api.stakekit.models.response.model.transaction.tron

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TronStakeKitTransaction(
    // @Json(name = "txID")
    // val txId: String,
    // @Json(name = "raw_data")
    // val rawData: RawData,
    @Json(name = "raw_data_hex")
    val rawDataHex: String,
    // @Json(name = "visible")
    // val visible: Boolean
)

// @JsonClass(generateAdapter = true)
// data class RawData(
//     @Json(name = "contract")
//     val contract: List<Contract>,
//     @Json(name = "ref_block_bytes")
//     val refBlockBytes: String,
//     @Json(name = "ref_block_hash")
//     val refBlockHash: String,
//     @Json(name = "expiration")
//     val expiration: Long,
//     @Json(name = "timestamp")
//     val timestamp: Long
// )
//
// @JsonClass(generateAdapter = true)
// data class Contract(
//     @Json(name = "parameter")
//     val parameter: Parameter,
//     @Json(name = "type")
//     val type: String
// )
//
// @JsonClass(generateAdapter = true)
// data class Parameter(
//     @Json(name = "value")
//     val value: Value,
//     @Json(name = "type_url")
//     val typeUrl: String
// )
//
// @JsonClass(generateAdapter = true)
// data class Value(
//     @Json(name = "resource")
//     val resource: String,
//     @Json(name = "frozen_balance")
//     val frozenBalance: Long,
//     @Json(name = "owner_address")
//     val ownerAddress: String
// )