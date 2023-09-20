package com.ivy.core.domain.action.exchange

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import com.ivy.MainCoroutineExtension
import com.ivy.TestDispatcher
import com.ivy.core.domain.action.settings.basecurrency.BaseCurrencyFlow
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainCoroutineExtension::class)
class ExchangeRatesFlowTest {
    private lateinit var exchangeRatedFlow: ExchangeRatesFlow
    private lateinit var baseCurrencyFlow: BaseCurrencyFlow
    private lateinit var exchangeRateDao: ExchangeRateDaoFake
    private lateinit var exchangeRateOverrideDao: ExchangeRateOverrideDaoFake

    companion object {
        @JvmField
        @RegisterExtension
        val mainCoroutineExtension = MainCoroutineExtension()
    }

    @BeforeEach
    fun setUp() {
        baseCurrencyFlow = mockk()
        every { baseCurrencyFlow.invoke() } returns flowOf("", "EUR")

        exchangeRateDao = ExchangeRateDaoFake()
        exchangeRateOverrideDao = ExchangeRateOverrideDaoFake()

        val testDispatcher = TestDispatcher(mainCoroutineExtension.testDispatcher)

        exchangeRatedFlow = ExchangeRatesFlow(
            baseCurrencyFlow = baseCurrencyFlow,
            exchangeRateDao = exchangeRateDao,
            exchangeRateOverrideDao = exchangeRateOverrideDao,
            dispatchers = testDispatcher
        )
    }

    @Test
    fun `Test exchange rates flow emission`() = runTest {
        val exchangeRates = listOf(
            exchangeRateEntity("USD", 1.3),
            exchangeRateEntity("CAD", 1.7),
            exchangeRateEntity("AUD", 1.9),
        )
        val exchangeRateOverrides = listOf(
            exchangeRateOverrideEntity("CAD", 1.5)
        )
        exchangeRatedFlow().test {
            awaitItem() // Initial emission
            exchangeRateDao.save(exchangeRates)
            exchangeRateOverrideDao.save(exchangeRateOverrides)

            val rates1 = awaitItem()
            assertThat(rates1.rates).hasSize(3)
            assertThat(rates1.rates["USD"]).isEqualTo(1.3)
            assertThat(rates1.rates["CAD"]).isEqualTo(1.5) // Override Rate
            assertThat(rates1.rates["AUD"]).isEqualTo(1.9)

            exchangeRateOverrideDao.save(emptyList())
            val rates2 = awaitItem()
            assertThat(rates2.rates).hasSize(3)
            assertThat(rates2.rates["USD"]).isEqualTo(1.3)
            assertThat(rates2.rates["CAD"]).isEqualTo(1.7) // Real Rate
            assertThat(rates2.rates["AUD"]).isEqualTo(1.9)
        }
    }
}