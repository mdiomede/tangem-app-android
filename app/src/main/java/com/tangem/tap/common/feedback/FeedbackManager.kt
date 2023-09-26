package com.tangem.tap.common.feedback

import android.content.Context
import com.tangem.datasource.config.models.ChatConfig
import com.tangem.datasource.utils.BlockchainSdkLogCollector
import com.tangem.domain.common.TapWorkarounds
import com.tangem.tap.common.chat.ChatManager
import com.tangem.tap.common.extensions.sendEmail
import com.tangem.tap.common.log.TangemLogCollector
import com.tangem.tap.foregroundActivityObserver
import com.tangem.tap.withForegroundActivity
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.StringWriter

/**
 * Created by Anton Zhilenkov on 25/02/2021.
 */
class FeedbackManager(
    val infoHolder: AdditionalFeedbackInfo,
    private val logCollector: TangemLogCollector,
    private val blockchainSdkLogCollector: BlockchainSdkLogCollector,
    private val chatManager: ChatManager,
) {

    private var sessionFeedbackFile: File? = null
    private var sessionLogsFile: File? = null
    private var sessionBlockchainSdkLogsFile: File? = null

    fun sendEmail(feedbackData: FeedbackData, onFail: ((Exception) -> Unit)? = null) {
        feedbackData.prepare(infoHolder)
        foregroundActivityObserver.withForegroundActivity { activity ->
            activity.sendEmail(
                email = getSupportEmail(),
                subject = activity.getString(feedbackData.subjectResId),
                message = feedbackData.joinTogether(activity, infoHolder),
                files = getLogFiles(activity),
                onFail = onFail,
            )
        }
    }

    fun openChat(config: ChatConfig, feedbackData: FeedbackData) {
        chatManager.open(
            config = config,
            createLogsFile = ::getScanLogFile,
            createFeedbackFile = { context -> getFeedbackFile(context, feedbackData) },
        )
    }

    private fun getFeedbackFile(context: Context, feedbackData: FeedbackData): File? {
        return try {
            if (sessionFeedbackFile != null) {
                return sessionFeedbackFile
            }
            val file = File(context.filesDir, FEEDBACK_FILE)
            file.delete()
            file.createNewFile()

            val feedback = feedbackData.run {
                prepare(infoHolder)
                joinTogether(context, infoHolder)
            }
            val fileWriter = FileWriter(file)
            fileWriter.write(feedback)
            fileWriter.close()

            if (file.exists()) {
                sessionFeedbackFile = file
                sessionFeedbackFile
            } else {
                null
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Can't create the logs file")
            null
        }
    }

    private fun getLogFiles(context: Context): List<File?> {
        val logFile = if (sessionLogsFile != null) {
            sessionLogsFile
        } else {
            sessionLogsFile = getLogFile(
                context = context,
                fileName = LOGS_FILE,
                logs = logCollector::getLogs,
                clearLogs = logCollector::clearLogs,
            )
            sessionLogsFile
        }

        val blockchainLogFile = if (sessionBlockchainSdkLogsFile != null) {
            sessionBlockchainSdkLogsFile
        } else {
            sessionBlockchainSdkLogsFile = getLogFile(
                context = context,
                fileName = BLOCKCHAIN_LOGS_FILE,
                logs = blockchainSdkLogCollector::getLogs,
                clearLogs = blockchainSdkLogCollector::clearLogs,
            )
            sessionBlockchainSdkLogsFile
        }

        return listOf(
            logFile,
            blockchainLogFile,
        )
    }

    // TODO: Implement sending of multiple logs files in chat https://tangem.atlassian.net/browse/AND-4718
    private fun getScanLogFile(context: Context): File? {
        return if (sessionLogsFile != null) {
            sessionLogsFile
        } else {
            sessionLogsFile = getLogFile(
                context = context,
                fileName = LOGS_FILE,
                logs = logCollector::getLogs,
                clearLogs = logCollector::clearLogs,
            )
            sessionLogsFile
        }
    }

    private fun getLogFile(
        context: Context,
        fileName: String,
        logs: () -> List<String>,
        clearLogs: () -> Unit,
    ): File? {
        return try {
            val file = File(context.filesDir, fileName)
            file.delete()
            file.createNewFile()

            val stringWriter = StringWriter()
            logs().forEach { stringWriter.append(it) }
            val fileWriter = FileWriter(file)
            fileWriter.write(stringWriter.toString())
            fileWriter.close()
            clearLogs()
            if (file.exists()) {
                file
            } else {
                null
            }
        } catch (ex: Exception) {
            Timber.e(ex, "Can't create the logs file")
            null
        }
    }

    private fun getSupportEmail(): String {
        return if (TapWorkarounds.isStart2CoinIssuer(infoHolder.cardIssuer)) {
            S2C_SUPPORT_EMAIL
        } else {
            DEFAULT_SUPPORT_EMAIL
        }
    }

    companion object {
        const val DEFAULT_SUPPORT_EMAIL = "support@tangem.com"
        const val S2C_SUPPORT_EMAIL = "cardsupport@start2coin.com"
        const val FEEDBACK_FILE = "feedback.txt"
        const val LOGS_FILE = "logs.txt"
        const val BLOCKCHAIN_LOGS_FILE = "blockchain_logs.txt"
    }
}
