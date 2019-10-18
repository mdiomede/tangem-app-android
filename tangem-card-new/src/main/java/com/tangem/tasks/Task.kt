package com.tangem.tasks

import com.tangem.CardEnvironment
import com.tangem.CardManagerDelegate
import com.tangem.CardReader
import com.tangem.commands.CommandEvent
import com.tangem.commands.CommandResponse
import com.tangem.commands.CommandSerializer
import com.tangem.enums.Status

abstract class Task<T>(protected val delegate: CardManagerDelegate? = null, private val reader: CardReader) {

    fun run(cardEnvironment: CardEnvironment,
            callback: (result: T, cardEnvironment: CardEnvironment) -> Unit) {

        delegate?.openNfcPopup()
        onRun(cardEnvironment, callback)

    }

    abstract fun onRun(cardEnvironment: CardEnvironment,
                       callback: (result: T, cardEnvironment: CardEnvironment) -> Unit)

    protected fun <T: CommandResponse>sendCommand(commandSerializer: CommandSerializer<T>, cardEnvironment: CardEnvironment,
                              callback: (result: CommandEvent) -> Unit) {

        reader.sendApdu(
                commandSerializer.serialize(cardEnvironment).toBytes()) { responseApdu ->

            when (responseApdu.status) {
                Status.ProcessCompleted, Status.Pin1Changed, Status.Pin2Changed, Status.PinsChanged
                -> {
                    val responseData = commandSerializer.deserialize(cardEnvironment, responseApdu)

                    if (responseData == null) {

                    } else {
                        callback(
                                CommandEvent.Success(responseData)
                        )
                    }

                }
                Status.InvalidParams -> TODO()
                Status.ErrorProcessingCommand -> TODO()
                Status.InvalidState -> TODO()

                Status.InsNotSupported -> TODO()
                Status.NeedEnctryption -> TODO()
                Status.NeedPause -> TODO()
            }

//            val parsedApdu = responseApdu.getTlvData()
//            if (parsedApdu == null) callback.invoke(TaskResult.Error(CardError()))
//            else if (!parsedApdu.statusCompleted()) {
//                callback.invoke(TaskResult.Error(CardError(parsedApdu.status.code)))
//            } else {
//                val response = commandSerializer.deserialize(parsedApdu)
//                callback.invoke(response)
//            }
        }
    }
}

sealed class TaskError : Exception() {
    class UnknownStatus(sw: Int) : TaskError()
    class MappingError : TaskError()
    class ErrorProcessingCommand : TaskError()
    class InvalidState : TaskError()
    class InsNotSupported : TaskError()
    class VefificationFailed : TaskError()
    class CardError : TaskError()
    class ReaderError() : TaskError()
}

sealed class TaskResult {
    data class Success(val resultData: Any) : TaskResult()
    data class CommandCompleted(val resultData: Any) : TaskResult()
    data class Error(val error: TaskError) : TaskResult()
}



class BasicTask<T: CommandResponse>(
        private val commandSerializer: CommandSerializer<CommandResponse>,
        delegate: CardManagerDelegate? = null,
        reader: CardReader) : Task(delegate, reader) {

    override fun onRun(cardEnvironment: CardEnvironment,
                     callback: (result: TaskResult, cardEnvironment: CardEnvironment) -> Unit) {
        sendCommand(commandSerializer, cardEnvironment) {
            callback.invoke(it, updateEnvironment())
        }
    }

    private fun updateEnvironment(): CardEnvironment {
        //TODO: set logic of changing CardEnvironment when needed
        return CardEnvironment()
    }
}



