package com.tangem.datasource.utils

import android.util.Log
import com.ihsanbal.logging.Logger
import java.text.SimpleDateFormat
import java.util.Date

class BlockchainSdkLogCollector(
    private val levels: List<Int> = listOf(Log.VERBOSE),
) : Logger {

    private val dateFormatter = SimpleDateFormat("HH:mm:ss.SSS")
    private val logs = mutableListOf<String>()
    private val mutex = Object()

    override fun log(level: Int, tag: String?, msg: String?) {
        if (!levels.contains(level)) return

        synchronized(mutex) {
            msg?.let { message ->
                val logMessage = "${dateFormatter.format(Date())}: $message\n"
                logs.add(logMessage)
            }
        }
    }

    fun getLogs(): List<String> = synchronized(mutex) { logs.toList() }

    fun clearLogs() = synchronized(mutex) { logs.clear() }
}
