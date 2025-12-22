plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
}
android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.interests.impl"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
//    implementation(projects.core.domain)
//    implementation(projects.feature.topic.api)
    implementation(projects.feature.interests.api)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)

//    testImplementation(projects.core.testing)
//    testImplementation(projects.core.dataTest)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.hilt.android.testing)
//    testImplementation(projects.uiTestHiltManifest)
//    testImplementation(projects.feature.topic.impl)
    testImplementation(libs.androidx.navigation.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
//    androidTestImplementation(projects.core.testing)
}
