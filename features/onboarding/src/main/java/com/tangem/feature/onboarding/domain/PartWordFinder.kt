package com.tangem.feature.onboarding.domain

import com.tangem.feature.onboarding.presentation.wallet2.viewmodel.log
import com.tangem.utils.extensions.isNotWhitespace

/**
 * Created by Anton Zhilenkov on 07.04.2023.
 */
internal class PartWordFinder {

    fun getLeadPartOfWord(text: String, cursorPosition: Int): String? {
        log("<--")
        log("text: $text, cursorPosition: $cursorPosition)")
        val charText = text.toCharArray().asList()
        if (cursorPosition > charText.size || cursorPosition < 0) {
            log("NULL: cursorPosition is out of text size")
            return null
        }
        if (cursorPosition == 0) {
            log("NULL: cursorPosition at start of text")
            return null
        }
        if (charText[cursorPosition - 1].isWhitespace()) {
            log("NULL: from left of cursorPosition is white space")
            return null
        }
        if (charText.size > cursorPosition && charText[cursorPosition].isNotWhitespace()) {
            log("NULL: cursorPosition in the middle of a word")
            return null
        }

        for (charIndex in cursorPosition - 1 downTo 0) {
            val char = charText[charIndex]
            when {
                charIndex == 0 -> {
                    val foundWord = text.substring(charIndex, cursorPosition)
                    log("found lead word: [$foundWord]")
                    return foundWord
                }
                char.isWhitespace() -> {
                    val foundWord = text.substring(charIndex + 1, cursorPosition)
                    log("found lead word: [$foundWord]")
                    return foundWord
                }
            }
        }

        log("NULL: no word found")
        return null
    }

    fun getLastPartOfWord(text: String, word: String, cursorPosition: Int): String {
        log("<--")
        val leadPart = getLeadPartOfWord(text, cursorPosition) ?: return ""
        // val lastPart = word.replace(leadPart, "")
        val lastPart = word.substring(leadPart.length, word.length)

        log("found last word part: [$lastPart]")
        return lastPart
    }
}

