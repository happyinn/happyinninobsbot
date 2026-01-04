plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.compose)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem"
    testOptions.unitTests.isIncludeAndroidResources = true

}

dependencies {



//    lintPublish(projects.lint)

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.material3.adaptive)
    api(libs.androidx.compose.material3.navigationSuite)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)

    implementation(libs.coil.kt.compose)



    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.compose.material.iconsExtended)


    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.testManifest)



    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
//    testImplementation(projects.core.screenshotTesting)
}