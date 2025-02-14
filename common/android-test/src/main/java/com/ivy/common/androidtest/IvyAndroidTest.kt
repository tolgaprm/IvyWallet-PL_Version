package com.ivy.common.androidtest

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.datastore.dataStore
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
abstract class IvyAndroidTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: IvyWalletCoreDb

    @Inject
    lateinit var timeProvider: TimeProvider

    protected lateinit var context: Context

    @Before
    open fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()
        db.clearAllTables()
        clearDataStore()
    }

    @After
    open fun tearDown() {
        db.close()
    }

    private fun clearDataStore() = runTest {
        context.dataStore.edit {
            it.clear()
        }
    }

    protected fun setDate(date: LocalDate) {
        (timeProvider as TimeProviderFake).apply {
            timeNow = date.atTime(12, 0)
            dateNow = date
        }
    }
}