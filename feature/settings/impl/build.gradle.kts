plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
}

android {
    namespace = "com.google.samples.apps.happyinninobsbot.feature.settings.impl"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.google.oss.licenses)
    implementation(projects.core.data)

//    testImplementation(projects.core.testing)

    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}