package com.tangem.feature.learn2earn.domain.models

import com.tangem.datasource.api.promotion.models.PromotionInfoResponse

/**
 * @author Anton Zhilenkov on 16.06.2023.
 */
internal fun Promotion.PromotionInfo.getData(promoCode: String?): PromotionInfoResponse.Data {
    return if (promoCode == null) {
        newCard
    } else {
        oldCard
    }
}