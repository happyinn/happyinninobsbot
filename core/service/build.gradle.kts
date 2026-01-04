plugins {
    alias(libs.plugins.happyinninobsbot.hilt)
    alias(libs.plugins.happyinninobsbot.android.library)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.service"
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.model)
    api(projects.core.network)

    implementation(projects.core.analytics)
    implementation(projects.core.notifications)
    implementation(projects.sync.work)
    implementation(libs.github.anilbeesetti.nextlib.mediainfo)


    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
}