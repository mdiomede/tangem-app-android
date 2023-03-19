package com.tangem.datasource.asset

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import javax.inject.Inject

/**
 * Implementation of asset file reader
 *
 * @property context application context
 */
internal class AndroidAssetReader @Inject constructor(
    @ApplicationContext private val context: Context,
) : AssetReader {

    override fun readJson(fileName: String): String {
        return context.assets
            .open("$fileName.json")
            .bufferedReader()
            .use(BufferedReader::readText)
    }

    override fun writeJson(content: String, fileName: String) {
        context
            .openFileOutput(fileName, Context.MODE_PRIVATE)
            .bufferedWriter()
            .use { it.write(content) }
    }
}
