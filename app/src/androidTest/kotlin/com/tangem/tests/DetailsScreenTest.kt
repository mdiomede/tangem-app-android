package com.tangem.tests

import com.tangem.common.BaseTestCase
import com.tangem.domain.models.scan.ProductType
import com.tangem.screens.*
import com.tangem.tap.domain.sdk.mocks.MockProvider
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kakaocup.compose.node.element.ComposeScreen
import org.junit.Test

@HiltAndroidTest
class DetailsScreenTest : BaseTestCase() {

    @Test
    fun walletWithoutBackupDetails() =
        setupHooks().run {
            ComposeScreen.onComposeScreen<StoriesScreen>(composeTestRule) {
                step("Click on \"Scan\" button") {
                    scanButton {
                        assertIsDisplayed()
                        performClick()
                    }
                }
            }
            DisclaimerScreen {
                step("Click on \"Accept\" button") {
                    acceptButton {
                        isVisible()
                        click()
                    }
                }
            }
            ComposeScreen.onComposeScreen<MainScreen>(composeTestRule) {
                step("Make sure wallet screen is visible") {
                    assertIsDisplayed()
                }
            }
            ComposeScreen.onComposeScreen<TopBar>(composeTestRule) {
                step("Open wallet details") {
                    moreButton {
                        assertIsDisplayed()
                        performClick()
                    }
                    device.uiDevice.pressBack() //TODO: why details screen is visible only from second time
                    moreButton {
                        assertIsDisplayed()
                        performClick()
                    }
                }
            }
            ComposeScreen.onComposeScreen<DetailsScreen>(composeTestRule) {
                step("Assert wallet connect button is visible") {
                    walletConnectButton.assertIsDisplayed()
                }
                step("Assert link more cards button is visible") {
                    linkMoreCardsButton.assertIsDisplayed()
                }
                step("Assert scan card button is visible") {
                    scanCardButton.assertIsDisplayed()
                }
                step("Assert card settings button is visible") {
                    cardSettingsButton.assertIsDisplayed()
                }
                step("Assert app settings button is visible") {
                    appSettingsButton.assertIsDisplayed()
                }
                step("Assert contact support button is visible") {
                    contactSupportButton.assertIsDisplayed()
                }
                step("Assert referral program button is visible") {
                    referralProgramButton.assertIsDisplayed()
                }
                step("Assert terms or service button is visible") {
                    toSButton.assertIsDisplayed()
                }
            }
        }

    @Test
    fun wallet2Details() =
        setupHooks().run {
            ComposeScreen.onComposeScreen<StoriesScreen>(composeTestRule) {
                step("Click on \"Scan\" button") {
                    MockProvider.setMocks(ProductType.Wallet2)
                    scanButton {
                        assertIsDisplayed()
                        performClick()
                    }
                }
            }
            DisclaimerScreen {
                step("Click on \"Accept\" button") {
                    acceptButton {
                        isVisible()
                        click()
                    }
                }
            }
            ComposeScreen.onComposeScreen<MainScreen>(composeTestRule) {
                step("Make sure wallet screen is visible") {
                    assertIsDisplayed()
                }
            }
            ComposeScreen.onComposeScreen<TopBar>(composeTestRule) {
                step("Open wallet details") {
                    moreButton {
                        assertIsDisplayed()
                        performClick()
                    }
                    device.uiDevice.pressBack() //TODO: why details screen is visible only from second time
                    moreButton {
                        assertIsDisplayed()
                        performClick()
                    }
                }
            }
            ComposeScreen.onComposeScreen<DetailsScreen>(composeTestRule) {
                step("Assert wallet connect button is visible") {
                    walletConnectButton.assertIsDisplayed()
                }
                step("Assert link more cards button is not visible") {
                    linkMoreCardsButton.assertIsNotDisplayed()
                }
                step("Assert scan card button is visible") {
                    scanCardButton.assertIsDisplayed()
                }
                step("Assert card settings button is visible") {
                    cardSettingsButton.assertIsDisplayed()
                }
                step("Assert app settings button is visible") {
                    appSettingsButton.assertIsDisplayed()
                }
                step("Assert contact support button is visible") {
                    contactSupportButton.assertIsDisplayed()
                }
                step("Assert referral program button is visible") {
                    referralProgramButton.assertIsDisplayed()
                }
                step("Assert terms or service button is visible") {
                    toSButton.assertIsDisplayed()
                }
            }
        }


    @Test
    fun noteDetailsTest() =
        setupHooks().run {
            ComposeScreen.onComposeScreen<StoriesScreen>(composeTestRule) {
                step("Click on \"Scan\" button") {
                    MockProvider.setMocks(ProductType.Note)
                    scanButton {
                        assertIsDisplayed()
                        performClick()
                    }
                }
            }
            DisclaimerScreen {
                step("Click on \"Accept\" button") {
                    acceptButton {
                        isVisible()
                        click()
                    }
                }
            }
            ComposeScreen.onComposeScreen<MainScreen>(composeTestRule) {
                step("Make sure wallet screen is visible") {
                    assertIsDisplayed()
                }
            }
            ComposeScreen.onComposeScreen<TopBar>(composeTestRule) {
                step("Open wallet details") {
                    moreButton {
                        assertIsDisplayed()
                        performClick()
                    }
                    device.uiDevice.pressBack() //TODO: why details screen is visible only from second time
                    moreButton {
                        assertIsDisplayed()
                        performClick()
                    }
                }
            }
            ComposeScreen.onComposeScreen<DetailsScreen>(composeTestRule) {
                step("Assert wallet connect button is visible") {
                    walletConnectButton.assertIsNotDisplayed()
                }
                step("Assert link more cards button is not visible") {
                    linkMoreCardsButton.assertIsNotDisplayed()
                }
                step("Assert scan card button is visible") {
                    scanCardButton.assertIsDisplayed()
                }
                step("Assert card settings button is visible") {
                    cardSettingsButton.assertIsDisplayed()
                }
                step("Assert app settings button is visible") {
                    appSettingsButton.assertIsDisplayed()
                }
                step("Assert contact support button is visible") {
                    contactSupportButton.assertIsDisplayed()
                }
                step("Assert referral program button is visible") {
                    referralProgramButton.assertIsNotDisplayed()
                }
                step("Assert terms or service button is visible") {
                    toSButton.assertIsDisplayed()
                }
            }
        }


}
