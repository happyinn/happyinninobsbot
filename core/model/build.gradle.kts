plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.hilt)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.model"
}

dependencies {
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.serialization.json)
}
