package com.obsbot.happyinn.apps.happyinninobsbot.core.data.di

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.CompositeUserVideosResourceRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserVideosResourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UserVideosResourceRepositoryModule {
    @Binds
    fun bindsUserVideosResourceRepository(
        userDataRepository: CompositeUserVideosResourceRepository,
    ): UserVideosResourceRepository
}
