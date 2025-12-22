plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.search.impl"
}

dependencies {
//    implementation(projects.core.domain)
    implementation(projects.feature.interests.api)
    implementation(projects.feature.search.api)
    implementation(projects.feature.topic.api)

//    testImplementation(projects.core.testing)

//    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
//    androidTestImplementation(projects.core.testing)
}