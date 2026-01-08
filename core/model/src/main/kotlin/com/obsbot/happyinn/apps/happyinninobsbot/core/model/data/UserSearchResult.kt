package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data


data class UserSearchResult(
    val topics: List<FollowableTopic> = emptyList(),
    val newsResources: List<UserNewsResource> = emptyList(),
)
