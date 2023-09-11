package com.tangem.domain.settings.repositories

import kotlinx.coroutines.flow.Flow

abstract class DeviceFlipDetector {

    abstract fun deviceFlipEvents(): Flow<Unit>
}
