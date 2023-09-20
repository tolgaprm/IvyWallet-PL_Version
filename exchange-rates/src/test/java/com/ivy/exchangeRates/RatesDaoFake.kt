package com.ivy.exchangeRates

import com.ivy.core.persistence.algorithm.calc.Rate
import com.ivy.core.persistence.algorithm.calc.RatesDao
import com.ivy.data.CurrencyCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RatesDaoFake : RatesDao {

    var rates = MutableStateFlow<List<Rate>>(
        listOf(
            rate(25.0, "USD"),
            rate(28.0, "EUR"),
        )
    )
    var overrides = MutableStateFlow<List<Rate>>(
        listOf(
            rate(10.0, "USD")
        )
    )

    override fun findAll(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return rates
    }

    override fun findAllOverrides(baseCurrency: CurrencyCode): Flow<List<Rate>> {
        return overrides
    }
}