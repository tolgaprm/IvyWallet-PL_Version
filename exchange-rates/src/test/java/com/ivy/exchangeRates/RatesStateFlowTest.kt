package com.ivy.exchangeRates

import MainCoroutineExtension
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.exchangeRates.data.RateUi
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
class RatesStateFlowTest {

    private lateinit var ratesStateFlow: RatesStateFlow
    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var ratesDao: RatesDaoFake

    @BeforeEach
    fun setUp() {
        baseCurrencyFlow = mockk()
        every { baseCurrencyFlow.invoke() } returns flowOf("", "TRY")
        ratesDao = RatesDaoFake()
        ratesStateFlow = RatesStateFlow(baseCurrencyFlow, ratesDao)
    }

    @Test
    fun `Test RatesStateFlow return RateUi`() = runTest {
        val initialRatesState = RatesState(
            baseCurrency = "",
            manual = emptyList(),
            automatic = emptyList()
        )
        ratesStateFlow().test {
            val initialEmission = awaitItem() // Initial emission
            assertThat(initialEmission).isEqualTo(initialRatesState)

            val emission1 = awaitItem()
            val overriddenRate = RateUi(to = "USD", from = "TRY", rate = 10.0)
            assertThat(emission1.baseCurrency).isEqualTo("TRY")
            assertThat(emission1.automatic).doesNotContain(overriddenRate)
            assertThat(emission1.manual).contains(overriddenRate)

            ratesDao.rates.value += Rate(rate = 5.0, currency = "CAD")
            val emission2 = awaitItem()
            val rate = RateUi(to = "CAD", from = "TRY", rate = 5.0)
            assertThat(emission2.manual).doesNotContain(rate)
            assertThat(emission2.automatic).contains(rate)
        }
    }
}