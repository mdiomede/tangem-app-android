package com.tangem.feature.learn2earn.data.models

import com.tangem.datasource.api.promotion.models.PromotionInfoResponse

/**
 * @author Anton Zhilenkov on 27.06.2023.
 */
internal fun PromotionInfoResponse.getData(promoCode: String?): PromotionInfoResponse.Data? {
    return if (promoCode == null) {
        newCard
    } else {
        oldCard
    }
}