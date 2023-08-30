package com.ivy.core.domain.algorithm.calc

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.data.transaction.TransactionType
import org.junit.jupiter.api.Test
import java.time.Instant

internal class RawStatsTest {

    @Test
    fun `rawStats() should return RawStats with correct values`() {
        val listOfCalcTransaction = mutableListOf(
            CalcTrn(100.0, "USD", TransactionType.Income, Instant.now()),
            CalcTrn(300.0, "TRY", TransactionType.Income, Instant.now()),
            CalcTrn(300.0, "TRY", TransactionType.Expense, Instant.now()),
            CalcTrn(200.0, "USD", TransactionType.Income, Instant.now()),
            CalcTrn(500.0, "TRY", TransactionType.Income, Instant.now())
        )
        val lastTransactionTime = Instant.now()
        listOfCalcTransaction.add(
            CalcTrn(100.0, "USD", TransactionType.Expense, lastTransactionTime)
        )
        val stats = rawStats(listOfCalcTransaction)
        assertThat(stats.incomesCount).isEqualTo(4)
        assertThat(stats.expensesCount).isEqualTo(2)
        assertThat(stats.incomes).isEqualTo(mapOf("USD" to 300.0, "TRY" to 800.0))
        assertThat(stats.expenses).isEqualTo(mapOf("TRY" to 300.0, "USD" to 100.0))
        assertThat(stats.newestTrnTime).isEqualTo(lastTransactionTime)
    }
}