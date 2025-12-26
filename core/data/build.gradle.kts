
plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
    alias(libs.plugins.happyinninobsbot.hilt)
    id("kotlinx-serialization")
}

android {
    namespace = "com.google.samples.apps.happyinninobsbot.core.data"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(projects.core.analytics)
    implementation(projects.core.notifications)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.serialization.json)
//    testImplementation(projects.core.datastoreTest)
//    testImplementation(projects.core.testing)
}