plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.topic.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.topic.api)

    implementation(libs.androidx.compose.material3.adaptive.navigation3)

//    testImplementation(projects.core.testing)
    testImplementation(libs.robolectric)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
//    androidTestImplementation(projects.core.testing)
}