package com.tangem.tap.domain.sdk.mocks.wallet

import com.tangem.common.card.CardWallet
import com.tangem.common.card.EllipticCurve
import com.tangem.common.card.EncryptionMode
import com.tangem.common.card.FirmwareVersion
import com.tangem.domain.models.scan.CardDTO
import com.tangem.domain.models.scan.ProductType
import com.tangem.domain.models.scan.ScanResponse
import com.tangem.operations.attestation.Attestation
import com.tangem.tap.domain.sdk.mocks.Mocks
import java.util.Date

object WalletMocks : Mocks() {

    private val cardDto = CardDTO(
        cardId = "AC05000000086747",
        batchId = "AC05",
        cardPublicKey = byteArrayOf(
            2, -120, -3, -32, -122, -127, -120, -104, 59, 72, 76, 114, 94, 75, -37, -55, 55,
            99, 66, 123, 85, -87, 80, 106, 105, -116, 87, -83, -12, 70, 108, -68, -39
        ),
        firmwareVersion = CardDTO.FirmwareVersion(
            major = 4,
            minor = 52,
            patch = 0,
            type = FirmwareVersion.FirmwareType.Release,
        ),
        manufacturer = CardDTO.Manufacturer(
            name = "TANGEM",
            manufactureDate = Date(),
            signature = byteArrayOf(
                -20, 98, -101, 94, -23, 73, 122, -21, 74, 76, 79, -55, -102, -62, -30, 44, -38,
                118, 75, 121, -36, 118, -62, 60, -38, -63, 33, -14, -98, -69, 112, 22, 48, -43, 47, 65, -61, -56,
                38, -94, -45, 44, 95, -22, 6, -31, -40, -25, 42, 38, 94, -99, 71, 98, -33, 27, -102, 30, 52, -7,
                51, -27, 84, 66
            ),
        ),
        issuer = CardDTO.Issuer(
            name = "TANGEM",
            publicKey = byteArrayOf(
                3, 86, -25, -61, 55, 99, 41, -33, -82, 115, -120, -33, 22, -107, 103, 3, -122,
                16, 60, -110, 72, 106, -121, 100, 79, -87, -27, 18, -55, -49, 78, -110, -2
            ),
        ),
        settings = CardDTO.Settings(
            securityDelay = 15000,
            maxWalletsCount = 20,
            isSettingAccessCodeAllowed = false,
            isSettingPasscodeAllowed = false,
            isResettingUserCodesAllowed = true,
            isLinkedTerminalEnabled = true,
            isBackupAllowed = true,
            supportedEncryptionModes = listOf(EncryptionMode.Strong, EncryptionMode.Fast, EncryptionMode.None),
            isFilesAllowed = true,
            isHDWalletAllowed = true,
            isKeysImportAllowed = false
        ),
        userSettings = CardDTO.UserSettings(isUserCodeRecoveryAllowed = true),
        linkedTerminalStatus = CardDTO.LinkedTerminalStatus.None,
        isAccessCodeSet = false,
        isPasscodeSet = false,
        supportedCurves = listOf(
            EllipticCurve.Secp256k1,
            EllipticCurve.Ed25519,
            EllipticCurve.Secp256r1,
            EllipticCurve.Bls12381G2,
            EllipticCurve.Bls12381G2Aug,
            EllipticCurve.Bls12381G2Pop,
            EllipticCurve.Bip0340
        ),
        wallets = listOf(
            CardDTO.Wallet(
                publicKey = byteArrayOf(
                    2, -109, 28, -27, -124, -58, -97, -61, 43, -84, 90, -9, -5, 4, 90, 17, 112, -125,
                    -108, 44, 19, -79, -60, -23, 34, -20, -20, 61, 84, 113, 120, -90, -5
                ),
                chainCode = byteArrayOf(
                    80, 13, -8, -108, -35, 116, -92, 125, -65, -28, 85, 72, -113, 59, 83, 13, 5, -83,
                    -102, 123, 124, -22, 94, 108, -71, 95, 65, -2, 38, 38, -108, 14
                ),
                curve = EllipticCurve.Secp256k1,
                settings = CardWallet.Settings(isPermanent = false),
                totalSignedHashes = 0,
                remainingSignatures = null,
                index = 0,
                hasBackup = false,
                derivedKeys = emptyMap(), //TODO
                extendedPublicKey = null, // TODO
                isImported = false
            ),
            CardDTO.Wallet(
                publicKey = byteArrayOf(
                    -65, -53, -62, -12, -57, -32, -38, -9, -128, -52, -83, -61, 73, 39, 41, 15,
                    -74, -97, 38, 52, -101, 63, 74, -56, -20, 15, 57, -127, 114, -93, -17, -109
                ),
                chainCode = byteArrayOf(
                    -81, 15, 125, 28, -115, -22, 87, 81, 87, -123, -25, -74, 86, 2, 1, 110, -115, 65, -110,
                    63, -64, 83, -93, -97, -104, 123, 12, -26, 94, 27, 84, -6
                ),
                curve = EllipticCurve.Ed25519,
                settings = CardWallet.Settings(isPermanent = false),
                totalSignedHashes = 0,
                remainingSignatures = null,
                index = 1,
                hasBackup = false,
                derivedKeys = emptyMap(),
                extendedPublicKey = null, // TODO
                isImported = false,
            ),
        ),
        attestation = Attestation(
            cardKeyAttestation = Attestation.Status.Verified,
            walletKeysAttestation = Attestation.Status.Skipped,
            firmwareAttestation = Attestation.Status.Skipped,
            cardUniquenessAttestation = Attestation.Status.Skipped,
        ),
        backupStatus = CardDTO.BackupStatus.NoBackup,
    )

    override val scanResponse = ScanResponse(
        card = cardDto,
        productType = ProductType.Wallet,
        walletData=null,
        secondTwinPublicKey=null,
        derivedKeys = emptyMap(), // TODO
        primaryCard = null,
    )

}