package com.tangem.tap.persistence

import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Created by Anton Zhilenkov on 25.10.2022.
 */
class DisclaimerPrefStorage(
    private val preferences: SharedPreferences,
) {

    fun accept(disclaimerKey: String) {
        preferences.edit { putBoolean(disclaimerKey, true) }
    }

    fun isAccepted(disclaimerKey: String): Boolean {
        return preferences.getBoolean(disclaimerKey, false)
    }
}
