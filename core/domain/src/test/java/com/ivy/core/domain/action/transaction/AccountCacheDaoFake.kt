package com.ivy.core.domain.action.transaction

import com.ivy.core.persistence.algorithm.accountcache.AccountCacheDao
import com.ivy.core.persistence.algorithm.accountcache.AccountCacheEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.Instant

class AccountCacheDaoFake : AccountCacheDao {
    override fun findAccountCache(accountId: String): Flow<AccountCacheEntity?> {
        return flow { }
    }

    override suspend fun findTimestampById(accountId: String): Instant? {
        return null
    }

    override suspend fun save(cache: AccountCacheEntity) {

    }

    override suspend fun delete(accountId: String) {

    }

    override suspend fun deleteAll() {

    }
}