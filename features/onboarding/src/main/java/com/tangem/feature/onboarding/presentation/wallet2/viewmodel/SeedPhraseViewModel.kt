package com.tangem.feature.onboarding.presentation.wallet2.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tangem.feature.onboarding.domain.SeedPhraseError
import com.tangem.feature.onboarding.domain.SeedPhraseInteractor
import com.tangem.feature.onboarding.presentation.wallet2.model.AboutUiAction
import com.tangem.feature.onboarding.presentation.wallet2.model.CheckSeedPhraseUiAction
import com.tangem.feature.onboarding.presentation.wallet2.model.ImportSeedPhraseUiAction
import com.tangem.feature.onboarding.presentation.wallet2.model.IntroUiAction
import com.tangem.feature.onboarding.presentation.wallet2.model.OnboardingSeedPhraseState
import com.tangem.feature.onboarding.presentation.wallet2.model.OnboardingSeedPhraseStep
import com.tangem.feature.onboarding.presentation.wallet2.model.SeedPhraseField
import com.tangem.feature.onboarding.presentation.wallet2.model.TextFieldState
import com.tangem.feature.onboarding.presentation.wallet2.model.TextFieldUiAction
import com.tangem.feature.onboarding.presentation.wallet2.model.UiActions
import com.tangem.feature.onboarding.presentation.wallet2.model.YourSeedPhraseUiAction
import com.tangem.feature.onboarding.presentation.wallet2.ui.StateBuilder
import com.tangem.utils.coroutines.CoroutineDispatcherProvider
import com.tangem.utils.coroutines.Debouncer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

/**
 * Created by Anton Zhilenkov on 12.03.2023.
 */
@HiltViewModel
class SeedPhraseViewModel @Inject constructor(
    private val interactor: SeedPhraseInteractor,
    private val dispatchers: CoroutineDispatcherProvider,
) : ViewModel() {

    var aboutSeedPhraseUriProvider: AboutSeedPhraseOpener? = null
    var chatLauncher: ChatSupportOpener? = null

    private var uiBuilder = StateBuilder(createUiActions())

    var uiState: OnboardingSeedPhraseState by mutableStateOf(uiBuilder.init())
        private set

    val currentStep: OnboardingSeedPhraseStep
        get() = uiState.step

    private val textFieldsDebouncers = mutableMapOf<String, Debouncer>()
    private var suggestionWordInserted: AtomicBoolean = AtomicBoolean(false)

    //FIXME: delete
    init {
        // prepareCheckYourSeedPhraseTest()
        // prepareImportSeedPhraseTest()
    }

    private fun createUiActions(): UiActions = UiActions(
        introActions = IntroUiAction(
            buttonCreateWalletClick = ::buttonCreateWalletClick,
            buttonOtherOptionsClick = ::buttonOtherOptionsClick,
        ),
        aboutActions = AboutUiAction(
            buttonReadMoreAboutSeedPhraseClick = ::buttonReadMoreAboutSeedPhraseClick,
            buttonGenerateSeedPhraseClick = ::buttonGenerateSeedPhraseClick,
            buttonImportSeedPhraseClick = ::buttonImportSeedPhraseClick,
        ),
        yourSeedPhraseActions = YourSeedPhraseUiAction(
            buttonContinueClick = ::buttonContinueClick,
        ),
        checkSeedPhraseActions = CheckSeedPhraseUiAction(
            buttonCreateWalletClick = ::buttonCreateWalletWithSeedPhraseClick,
            secondTextFieldAction = TextFieldUiAction(
                onTextFieldChanged = { value -> onTextFieldChanged(SeedPhraseField.Second, value) },
                onFocusChanged = { isFocused -> onFocusChanged(SeedPhraseField.Second, isFocused) },
            ),
            seventhTextFieldAction = TextFieldUiAction(
                onTextFieldChanged = { value -> onTextFieldChanged(SeedPhraseField.Seventh, value) },
                onFocusChanged = { isFocused -> onFocusChanged(SeedPhraseField.Seventh, isFocused) },
            ),
            eleventhTextFieldAction = TextFieldUiAction(
                onTextFieldChanged = { value -> onTextFieldChanged(SeedPhraseField.Eleventh, value) },
                onFocusChanged = { isFocused -> onFocusChanged(SeedPhraseField.Eleventh, isFocused) },
            ),
        ),
        importSeedPhraseActions = ImportSeedPhraseUiAction(
            phraseTextFieldAction = TextFieldUiAction(
                onTextFieldChanged = { value -> onSeedPhraseTextFieldChanged(value) },
            ),
            suggestedPhraseClick = ::buttonSuggestedPhraseClick,
            buttonCreateWalletClick = ::buttonCreateWalletWithSeedPhraseClick,
        ),
        menuChatClick = ::menuChatClick,
    )

    override fun onCleared() {
        textFieldsDebouncers.forEach { entry -> entry.value.release() }
        textFieldsDebouncers.clear()
        super.onCleared()
    }

    // region CheckSeedPhrase
    private fun onFocusChanged(field: SeedPhraseField, isFocused: Boolean) {
        when (field) {
            SeedPhraseField.Second -> {}
            SeedPhraseField.Seventh -> {}
            SeedPhraseField.Eleventh -> {}
        }
    }

    private fun onTextFieldChanged(field: SeedPhraseField, textFieldValue: TextFieldValue) {
        viewModelScope.launchSingle {
            updateUi { uiBuilder.checkSeedPhrase.updateTextField(uiState, field, textFieldValue) }

            val fieldState = field.getState(uiState)
            if (fieldState.textFieldValue.text.isEmpty()) {
                updateUi { uiBuilder.checkSeedPhrase.updateTextFieldError(uiState, field, hasError = false) }
                return@launchSingle
            }

            createOrGetDebouncer(field.name).debounce(viewModelScope, context = dispatchers.io) {
                val hasError = !interactor.wordIsMatch(textFieldValue.text)
                if (fieldState.isError != hasError) {
                    updateUi { uiBuilder.checkSeedPhrase.updateTextFieldError(uiState, field, hasError) }
                }

                val allFieldsWithoutError = SeedPhraseField.values()
                    .map { field -> field.getState(uiState) }
                    .all { fieldState -> !fieldState.isError }

                if (uiState.checkSeedPhraseState.buttonCreateWallet.enabled != allFieldsWithoutError) {
                    updateUi { uiBuilder.checkSeedPhrase.updateCreateWalletButton(uiState, allFieldsWithoutError) }
                }
            }
        }
    }
    // endregion CheckSeedPhrase

    // region ImportSeedPhrase
    private fun onSeedPhraseTextFieldChanged(textFieldValue: TextFieldValue) {
        log("<--")
        viewModelScope.launchSingle {
            val oldTextFieldValue = uiState.importSeedPhraseState.tvSeedPhrase.textFieldValue
            val isSameText = textFieldValue.text == oldTextFieldValue.text
            val isCursorMoved = textFieldValue.selection != oldTextFieldValue.selection

            updateUi {
                val mediateState = uiBuilder.importSeedPhrase.updateTextField(uiState, textFieldValue)
                uiBuilder.importSeedPhrase.updateCreateWalletButton(mediateState, enabled = false)
            }

            val fieldState = uiState.importSeedPhraseState.tvSeedPhrase
            val inputMnemonic = fieldState.textFieldValue.text

            when {
                suggestionWordInserted.getAndSet(false) -> {
                    validateMnemonic(inputMnemonic)
                    log("return: text changed through pasting suggestion")
                    return@launchSingle
                }
                isSameText && !isCursorMoved -> {
                    log("return: text is MATCH and cursor don't moved")
                    return@launchSingle
                }
                isSameText && isCursorMoved -> {
                    log("return: text is MATCH but cursor was moved")
                    updateSuggestions(fieldState)
                    return@launchSingle
                }
            }

            val isPasteFromClipboard = textFieldValue.text.length - oldTextFieldValue.text.length > 1
            if (isPasteFromClipboard) {
                log("PASTE")
                updateSuggestions(fieldState)
                validateMnemonic(inputMnemonic)
            } else {
                log("USER INPUT")
                updateSuggestions(fieldState)
                val debouncer = createOrGetDebouncer(MNEMONIC_DEBOUNCER)
                debouncer.debounce(viewModelScope, MNEMONIC_DEBOUNCE_DELAY, dispatchers.io) {
                    validateMnemonic(inputMnemonic)
                }
            }
        }
    }

    private suspend fun validateMnemonic(inputMnemonic: String) {
        log("<--")
        if (inputMnemonic.isEmpty() && uiState.importSeedPhraseState.error != null) {
            log("inputMnemonic is empty. Validation don't needed. Remove error")
            updateUi { uiBuilder.importSeedPhrase.updateError(uiState, null) }
            return
        }

        interactor.validateMnemonicString(inputMnemonic)
            .onSuccess {
                log("SUCCESS validation")
                updateUi { uiBuilder.importSeedPhrase.updateError(uiState, null) }
            }
            .onFailure {
                val error = it as? SeedPhraseError ?: return
                log("FAILURE: [${error::class.java.simpleName}]")

                val mediateState = when (error) {
                    is SeedPhraseError.InvalidWords -> {
                        uiBuilder.importSeedPhrase.updateInvalidWords(uiState, error.words)
                    }
                    else -> uiState
                }
                updateUi { uiBuilder.importSeedPhrase.updateError(mediateState, error) }
            }
    }

    private suspend fun updateSuggestions(fieldState: TextFieldState) {
        val suggestions = interactor.getSuggestions(
            text = fieldState.textFieldValue.text,
            hasSelection = !fieldState.textFieldValue.selection.collapsed,
            cursorPosition = fieldState.textFieldValue.selection.end,
        )
        updateUi { uiBuilder.importSeedPhrase.updateSuggestions(uiState, suggestions) }
    }
    // endregion ImportSeedPhrase

    // region ButtonClickHandlers
    private fun buttonCreateWalletClick() {
    }

    private fun buttonCreateWalletWithSeedPhraseClick() {
        buttonGenerateSeedPhraseClick()
    }

    private fun buttonOtherOptionsClick() {
        viewModelScope.launchSingle {
            updateUi { uiBuilder.changeStep(uiState, OnboardingSeedPhraseStep.AboutSeedPhrase) }
        }
    }

    private fun buttonReadMoreAboutSeedPhraseClick() {
        aboutSeedPhraseUriProvider?.open()
    }

    private fun buttonGenerateSeedPhraseClick() {
        viewModelScope.launchSingle {
            updateUi { uiBuilder.generateMnemonicComponents(uiState) }
            interactor.generateMnemonic()
                .onSuccess { mnemonic ->
                    updateUi { uiBuilder.mnemonicGenerated(uiState, mnemonic.mnemonicComponents) }
                }
                .onFailure {
                    // show error
                }
        }
    }

    private fun buttonImportSeedPhraseClick() {
        viewModelScope.launchSingle {
            updateUi { uiBuilder.changeStep(uiState, OnboardingSeedPhraseStep.ImportSeedPhrase) }
        }
    }

    private fun buttonContinueClick() {
        viewModelScope.launchSingle {
            updateUi { uiBuilder.changeStep(uiState, OnboardingSeedPhraseStep.CheckSeedPhrase) }
        }
    }

    private fun buttonSuggestedPhraseClick(suggestionIndex: Int) {
        viewModelScope.launchSingle {
            suggestionWordInserted.set(true)
            val textFieldValue = uiState.importSeedPhraseState.tvSeedPhrase.textFieldValue
            val word = uiState.importSeedPhraseState.suggestionsList[suggestionIndex]
            val cursorPosition = textFieldValue.selection.end

            val insertResult = interactor.insertSuggestionWord(
                text = textFieldValue.text,
                suggestion = word,
                cursorPosition = cursorPosition,
            )
            updateUi {
                val mediateState = uiBuilder.importSeedPhrase.insertSuggestionWord(uiState, insertResult)
                uiBuilder.importSeedPhrase.updateSuggestions(mediateState, emptyList())
            }
        }
    }

    private fun menuChatClick() {
        chatLauncher?.open()
    }
    // endregion ButtonClickHandlers

    // region Utils
    /**
     * Updating the UI with a contract where all copying of objects are called in the IO context and updating the
     * UiState in the main context.
     */
    private suspend fun updateUi(updateBlock: suspend () -> OnboardingSeedPhraseState) {
        withSingleContext {
            val newState = updateBlock.invoke()
            withMainContext { uiState = newState }
        }
    }

    private fun createOrGetDebouncer(name: String): Debouncer {
        return textFieldsDebouncers[name] ?: Debouncer(name).apply { textFieldsDebouncers[name] = this }
    }

    private fun SeedPhraseField.getState(uiState: OnboardingSeedPhraseState): TextFieldState = when (this) {
        SeedPhraseField.Second -> uiState.checkSeedPhraseState.tvSecondPhrase
        SeedPhraseField.Seventh -> uiState.checkSeedPhraseState.tvSeventhPhrase
        SeedPhraseField.Eleventh -> uiState.checkSeedPhraseState.tvEleventhPhrase
    }

    private fun CoroutineScope.launchSingle(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(dispatchers.single, block = block)
    }

    private suspend fun <T> withMainContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(dispatchers.main, block)
    }

    private suspend fun <T> withSingleContext(block: suspend CoroutineScope.() -> T): T {
        return withContext(dispatchers.single, block)
    }
    // endregion Utils

    // region Tests
// FIXME: delete
    private fun prepareCheckYourSeedPhraseTest() {
        viewModelScope.launchSingle {
            updateUi { uiState.copy(step = OnboardingSeedPhraseStep.Intro) }
            delay(1000)
            updateUi { uiState.copy(step = OnboardingSeedPhraseStep.AboutSeedPhrase) }
            delay(1000)
            updateUi { uiState.copy(step = OnboardingSeedPhraseStep.YourSeedPhrase) }
            delay(1000)
            buttonGenerateSeedPhraseClick()
        }
    }

    // FIXME: delete
    private fun prepareImportSeedPhraseTest() {
        viewModelScope.launchSingle {
            delay(1000)
            updateUi { uiBuilder.changeStep(uiState, OnboardingSeedPhraseStep.AboutSeedPhrase) }
            delay(1000)
            updateUi { uiBuilder.changeStep(uiState, OnboardingSeedPhraseStep.ImportSeedPhrase) }
        }
    }
// endregion Tests

    companion object {
        private const val MNEMONIC_DEBOUNCER = "MnemonicDebouncer"
        private const val MNEMONIC_DEBOUNCE_DELAY = 700L
    }
}

fun log(message: String) {
    val methodName = Thread.currentThread().stackTrace[3].methodName
    Timber.d("$methodName: $message")
}

interface AboutSeedPhraseOpener {
    fun open() {}
}

interface ChatSupportOpener {
    fun open() {}
}
