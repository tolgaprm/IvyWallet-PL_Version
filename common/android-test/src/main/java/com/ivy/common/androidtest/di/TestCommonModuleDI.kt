package com.ivy.common.androidtest.di

import com.ivy.common.androidtest.TimeProviderFake
import com.ivy.common.di.CommonModuleDI
import com.ivy.common.time.provider.TimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [CommonModuleDI::class]
)
abstract class TestCommonModuleDI {
    @Singleton
    @Binds
    abstract fun timeProvider(provider: TimeProviderFake): TimeProvider
}