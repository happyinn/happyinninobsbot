package com.obsbot.happyinn.apps.happyinninobsbot.core.data.di

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.CompositeUserNewsResourceRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserNewsResourceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface UserNewsResourceRepositoryModule {
    @Binds
    fun bindsUserNewsResourceRepository(
        userDataRepository: CompositeUserNewsResourceRepository,
    ): UserNewsResourceRepository
}

