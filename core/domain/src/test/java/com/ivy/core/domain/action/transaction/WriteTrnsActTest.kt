package com.ivy.core.domain.action.transaction

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.ivy.account
import com.ivy.attachment
import com.ivy.core.domain.algorithm.accountcache.InvalidateAccCacheAct
import com.ivy.data.transaction.TransactionType
import com.ivy.tag
import com.ivy.transaction
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class WriteTrnsActTest {

    private lateinit var writeTrnsAct: WriteTrnsAct
    private lateinit var transactionDaoFake: TransactionDaoFake
    private lateinit var timeProviderFake: TimeProviderFake
    private lateinit var accountCacheDaoFake: AccountCacheDaoFake

    @BeforeEach
    fun setUp() {
        transactionDaoFake = TransactionDaoFake()
        timeProviderFake = TimeProviderFake()
        accountCacheDaoFake = AccountCacheDaoFake()
        writeTrnsAct = WriteTrnsAct(
            transactionDao = transactionDaoFake,
            timeProvider = timeProviderFake,
            accountCacheDao = accountCacheDaoFake,
            trnsSignal = TrnsSignal(),
            invalidateAccCacheAct = InvalidateAccCacheAct(
                accountCacheDao = accountCacheDaoFake,
                timeProvider = timeProviderFake
            )
        )
    }

    @Test
    fun `Test create new transaction with expense`() = runBlocking<Unit> {
        val account = account()
        val transactionId = UUID.randomUUID()
        val tag = tag()
        val attachment = attachment(associatedId = transactionId.toString())
        val transaction = transaction(
            account = account
        ).copy(
            id = transactionId,
            attachments = listOf(attachment),
            tags = listOf(tag)
        )

        writeTrnsAct(WriteTrnsAct.Input.CreateNew(transaction))

        val cachedTransaction = transactionDaoFake.transactions.find {
            it.id == transactionId.toString()
        }
        val cachedTag = transactionDaoFake.tags.find { it.tagId == tag.id }
        val cachedAttachement = transactionDaoFake.attachments.find { it.id == attachment.id }

        assertThat(cachedTransaction).isNotNull()
        assertThat(cachedTransaction?.type).isEqualTo(TransactionType.Expense)

        assertThat(cachedTag).isNotNull()
        assertThat(cachedAttachement).isNotNull()
    }
}