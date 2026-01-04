plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.ui"
}

dependencies {
    api(libs.androidx.metrics)
    api(projects.core.analytics)
    api(projects.core.designsystem)
    api(projects.core.model)
    implementation(libs.androidx.compose.ui.tooling.preview)

    implementation(libs.androidx.browser)
    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)

//    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
//    androidTestImplementation(projects.core.testing)
}