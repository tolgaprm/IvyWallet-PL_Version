package com.ivy.wallet.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ivy.IvyComposeRule
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.CurrencyCode
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.main.Home
import kotlinx.coroutines.runBlocking

class HomeScreenRobot(
    private val composeRule: IvyComposeRule
) {
    fun navigateTo(navigator: Navigator): HomeScreenRobot {
        runBlocking {
            composeRule.awaitIdle()
            composeRule.runOnUiThread {
                navigator.navigate(Home.route) {
                    popUpTo(Home.route) {
                        inclusive = true
                    }
                }
            }
        }
        return this
    }

    fun openDateRangeSheet(timeProvider: TimeProvider): HomeScreenRobot {
        composeRule.onNodeWithText(
            timeProvider.dateNow().month.name, ignoreCase = true
        ).performClick()
        return this
    }

    fun selectMonth(monthName: String): HomeScreenRobot {
        composeRule.onNodeWithText(monthName)
            .assertIsDisplayed()
            .performClick()
        return this
    }

    fun assertDateIsDisplayed(day: Int, month: String): HomeScreenRobot {
        val paddedDay = day.toString().padStart(2, '0')
        composeRule.onNodeWithText("${month.take(3)}. $paddedDay").assertIsDisplayed()
        return this
    }

    fun clickDone(): HomeScreenRobot {
        composeRule.onNodeWithText("Done").performClick()
        return this
    }

    fun clickUpcoming(): HomeScreenRobot {
        composeRule.onNodeWithText("Upcoming").performClick()
        return this
    }

    fun assertTransactionDoesNotExist(transactionTitle: String): HomeScreenRobot {
        composeRule.onNodeWithText(transactionTitle).assertDoesNotExist()
        return this
    }

    fun assertTransactionIsDisplayed(transactionTitle: String): HomeScreenRobot {
        composeRule.onNodeWithText(transactionTitle).assertIsDisplayed()
        return this
    }

    fun openOverdue(): HomeScreenRobot {
        composeRule
            .onNodeWithText("Overdue")
            .performClick()
        return this
    }

    fun assertBalanceIsDisplayed(amount: Double, currency: CurrencyCode): HomeScreenRobot {
        val formattedAmount = if(amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else amount.toString()

        composeRule
            .onAllNodes(
                hasText(formattedAmount) and hasAnySibling(hasText(currency)),
                useUnmergedTree = true
            )
            .onFirst()
            .assertIsDisplayed()

        return this
    }

    fun clickGet(): HomeScreenRobot {
        composeRule.onNodeWithText("Get").performClick()
        return this
    }
}