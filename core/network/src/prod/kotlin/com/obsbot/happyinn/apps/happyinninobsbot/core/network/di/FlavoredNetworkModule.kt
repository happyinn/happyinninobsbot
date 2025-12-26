package com.obsbot.happyinn.apps.happyinninobsbot.core.network.di

import com.obsbot.happyinn.apps.happyinninobsbot.core.network.HioNetworkDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.retrofit.RetrofitHioNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {

    @Binds
    fun binds(impl: RetrofitHioNetwork): HioNetworkDataSource
}
