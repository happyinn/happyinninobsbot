plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
    alias(libs.plugins.happyinninobsbot.hilt)
}

android {
    defaultConfig {
        testInstrumentationRunner = "com.obsbot.happyinn.apps.happyinninobsbot.core.testing.NiaTestRunner"
    }
    namespace = "com.obsbot.happyinn.apps.happyinninobsbo.sync"
}

dependencies {
    ksp(libs.hilt.ext.compiler)

    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.work.ktx)
    implementation(libs.hilt.ext.work)
    implementation(projects.core.analytics)
    implementation(projects.core.data)
    implementation(projects.core.notifications)

    implementation(libs.github.anilbeesetti.nextlib.mediainfo)

    prodImplementation(libs.firebase.cloud.messaging)
    prodImplementation(platform(libs.firebase.bom))

    androidTestImplementation(libs.androidx.work.testing)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlinx.coroutines.guava)
//    androidTestImplementation(projects.core.testing)
}