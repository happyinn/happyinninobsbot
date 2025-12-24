plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.api)
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.topic.api"
}

