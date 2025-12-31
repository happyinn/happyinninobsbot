plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.api)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api"
}

dependencies {
    api(projects.core.navigation)
}
