package com.tangem.tasks

import com.tangem.CardEnvironment
import com.tangem.CardManagerDelegate
import com.tangem.CardReader
import com.tangem.Log
import com.tangem.commands.CommandResponse
import com.tangem.commands.CommandSerializer
import com.tangem.common.CompletionResult
import com.tangem.common.apdu.StatusWord

abstract class Task<T> {

    var delegate: CardManagerDelegate? = null
    var reader: CardReader? = null

    fun run(cardEnvironment: CardEnvironment,
            callback: (result: TaskEvent<T>) -> Unit) {
        delegate?.onTaskStarted()
        reader?.setStartSession()
        Log.i(this::class.simpleName!!, "Nfc task is started")
        onRun(cardEnvironment, callback)
    }

    protected fun onTaskCompleted(withError: Boolean = false, taskError: TaskError? = null) {
        reader?.closeSession()
        if (withError) {
            delegate?.onTaskError(taskError)
        } else {
            delegate?.onTaskCompleted()
        }
    }

    protected abstract fun onRun(cardEnvironment: CardEnvironment,
                                 callback: (result: TaskEvent<T>) -> Unit)

    protected fun <T : CommandResponse> sendCommand(
            commandSerializer: CommandSerializer<T>,
            cardEnvironment: CardEnvironment,
            callback: (result: CompletionResult<T>) -> Unit) {

        Log.i(this::class.simpleName!!, "Nfc command ${commandSerializer::class.simpleName!!} is initiated")


        reader?.transceiveApdu(
                commandSerializer.serialize(cardEnvironment)) { result ->

            when (result) {
                is CompletionResult.Success -> {
                    val responseApdu = result.data
                    when (responseApdu.statusWord) {
                        StatusWord.ProcessCompleted, StatusWord.Pin1Changed, StatusWord.Pin2Changed, StatusWord.PinsChanged
                        -> {
                            try {
                                val responseData = commandSerializer.deserialize(cardEnvironment, responseApdu)
                                Log.i(this::class.simpleName!!, "Nfc command ${commandSerializer::class.simpleName!!} is completed")
                                callback(CompletionResult.Success(responseData as T))
                            } catch (error: TaskError) {
                                callback(CompletionResult.Failure(error))
                            }
                        }
                        StatusWord.InvalidParams -> callback(CompletionResult.Failure(TaskError.InvalidParams()))
                        StatusWord.ErrorProcessingCommand -> callback(CompletionResult.Failure(TaskError.ErrorProcessingCommand()))
                        StatusWord.InvalidState -> callback(CompletionResult.Failure(TaskError.InvalidState()))

                        StatusWord.InsNotSupported -> callback(CompletionResult.Failure(TaskError.InsNotSupported()))
                        StatusWord.NeedEncryption -> callback(CompletionResult.Failure(TaskError.NeedEncryption()))
                        StatusWord.NeedPause -> {
                            val remainingTime = commandSerializer.deserializeSecurityDelay(responseApdu, cardEnvironment)
                            if (remainingTime != null) delegate?.showSecurityDelay(remainingTime)
                            Log.i(this::class.simpleName!!, "Nfc command ${commandSerializer::class.simpleName!!} triggered security delay of $remainingTime milliseconds")
                            sendCommand(commandSerializer, cardEnvironment, callback)
                        }
                    }
                }
                is CompletionResult.Failure ->
                    if (result.error is TaskError.TagLost) {
                        delegate?.hideSecurityDelay()
                    } else if (result.error is TaskError.UserCancelledError) {
                        callback(CompletionResult.Failure(TaskError.UserCancelledError()))
                        reader?.readingActive = false
                    }

            }
        }
    }
}

/**
 * Returns in callback from tasks
 */
sealed class TaskEvent<T> {
    class Event<T>(val data: T) : TaskEvent<T>()
    class Completion<T>(val error: TaskError? = null) : TaskEvent<T>()
}



