package com.tangem.domain.redux.global

import com.tangem.domain.DomainStateDialog
import com.tangem.domain.common.ScanResponse
import org.rekotlin.Action

/**
 * Created by Anton Zhilenkov on 07/04/2022.
 */
//TODO: refactoring: is alias for the GlobalAction
sealed class DomainGlobalAction : Action {
    data class SetScanResponse(val scanResponse: ScanResponse?) : DomainGlobalAction()
    data class ShowDialog(val stateDialog: DomainStateDialog?) : DomainGlobalAction()
}