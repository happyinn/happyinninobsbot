package com.obsbot.happyinn.apps.happyinninobsbot.core.network.di


@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {

    @Binds
    fun binds(impl: DemoNiaNetworkDataSource): NiaNetworkDataSource
}
