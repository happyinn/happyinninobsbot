plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.notifications)
    implementation(projects.feature.videos.api)
    implementation(projects.feature.topic.api)
    implementation(libs.androidx.activity.compose)
    implementation(libs.accompanist.permissions)


    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
//    testImplementation(projects.core.testing)
//    testDemoImplementation(projects.core.screenshotTesting)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
//    androidTestImplementation(projects.core.testing)
}