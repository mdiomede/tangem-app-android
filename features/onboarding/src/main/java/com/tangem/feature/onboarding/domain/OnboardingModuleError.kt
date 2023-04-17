package com.tangem.feature.onboarding.domain

import com.tangem.common.module.ModuleError
import com.tangem.common.module.ModuleErrorCode
import com.tangem.common.module.ModuleMessage

/**
 * Created by Anton Zhilenkov on 07.04.2023.
 * For each pool of errors, use the codes lying in range 100 (200..299, 300..399, etc)
 *
 */
abstract class OnboardingModuleError(
    subCode: Int,
    override val message: String,
    override val data: Any?,
) : ModuleMessage, ModuleError() {
    override val code: Int = ModuleErrorCode.ONBOARDING + subCode

    companion object {
        internal const val ERROR_CODE_SEED_PHRASE = 100
    }
}

sealed class SeedPhraseError(
    subCode: Int = 0,
    message: String? = null,
    data: Any? = null,
) : OnboardingModuleError(
    subCode = ERROR_CODE_SEED_PHRASE + subCode,
    message = message ?: this::class.java.simpleName,
    data = data,
) {
    object InvalidWordCount : SeedPhraseError(0)
    object InvalidEntropyLength : SeedPhraseError(1)
    object InvalidWordsFile : SeedPhraseError(2)
    object InvalidChecksum : SeedPhraseError(3)
    object MnenmonicCreationFailed : SeedPhraseError(4)
    object NormalizationFailed : SeedPhraseError(5)
    object UnsupportedLanguage : SeedPhraseError(6)
    data class InvalidWords(val words: Set<String>) : SeedPhraseError(7)
}

