plugins {
    alias(libs.plugins.happyinninobsbot.android.feature.impl)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.impl"
}

dependencies {
    implementation(projects.core.data)
    implementation(projects.feature.bookmarks.api)
    implementation(projects.feature.topic.api)

//    testImplementation(projects.core.testing)

//    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
//    androidTestImplementation(projects.core.testing)
}