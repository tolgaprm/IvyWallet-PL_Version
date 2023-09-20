package com.ivy.exchangeRates

import com.ivy.core.persistence.algorithm.calc.Rate


fun rate(
    rate: Double,
    currency: String
): Rate {
    return Rate(
        rate = rate,
        currency = currency
    )
}