import com.obsbot.happyinn.apps.happyinninobsbot.HioBuildType


plugins {

    alias(libs.plugins.android.application)
    alias(libs.plugins.happyinninobsbot.android.application)
    alias(libs.plugins.happyinninobsbot.android.application.compose)
    alias(libs.plugins.happyinninobsbot.android.application.flavors)
//    alias(libs.plugins.happyinninobsbot.android.application.jacoco)
//    alias(libs.plugins.happyinninobsbot.android.application.firebase)
    alias(libs.plugins.happyinninobsbot.hilt)

//    alias(libs.plugins.google.osslicenses)
    alias(libs.plugins.baselineprofile)
//    alias(libs.plugins.roborazzi)
//    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot"

    defaultConfig {
        applicationId = "com.obsbot.happyinn.apps.happyinninobsbot"
        versionCode = 8
        versionName = "0.1.2" // X.Y.Z; X = Major, Y = minor, Z = Patch level

        testInstrumentationRunner = "com.obsbot.happyinn.apps.happyinninobsbot.core.testing.HioTestRunner"
    }

    buildTypes {
        debug {
            applicationIdSuffix = HioBuildType.DEBUG.applicationIdSuffix
        }
        release {
            /**isMinifyEnabled: 启用代码混淆和压缩
            providers.gradleProperty("minifyWithR8"): 从 gradle.properties 文件读取 minifyWithR8 属性值
            map(String::toBooleanStrict): 将字符串转换为布尔值
            getOrElse(true): 如果属性不存在则默认为 true*/
            isMinifyEnabled = providers.gradleProperty("minifyWithR8")
                .map(String::toBooleanStrict).getOrElse(true)

            /**为 Release 版本添加应用ID后缀，用于区分不同构建类型的应用*/
            applicationIdSuffix = HioBuildType.RELEASE.applicationIdSuffix

           /** getDefaultProguardFile(): 获取 Android SDK 提供的默认优化规则
            "proguard-rules.pro": 项目自定义的混淆规则文件*/
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            /**使用 debug 签名配置进行签名（便于克隆项目后直接运行）*/
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
            /**在构建过程中自动生成基线配置文件，用于性能优化*/
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

/*
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.testManifest)
}*/


dependencies {
    implementation(projects.feature.interests.api)
    implementation(projects.feature.interests.impl)
    implementation(projects.feature.foryou.api)
    implementation(projects.feature.foryou.impl)
    implementation(projects.feature.bookmarks.api)
    implementation(projects.feature.bookmarks.impl)
    implementation(projects.feature.topic.api)
    implementation(projects.feature.topic.impl)
    implementation(projects.feature.search.api)
    implementation(projects.feature.search.impl)
    implementation(projects.feature.settings.impl)

//    implementation(projects.core.ui)
//    implementation(projects.core.analytics)
//    implementation(projects.sync.work)
    implementation(projects.core.designsystem)
    implementation(projects.core.network)
    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.data)


    implementation(projects.core.designsystem)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.adaptive.navigation3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModel.navigation3)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)
    implementation(libs.kotlinx.serialization.json)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)
//    debugImplementation(projects.uiTestHiltManifest)

    kspTest(libs.hilt.compiler)

//    testImplementation(projects.core.dataTest)
//    testImplementation(projects.core.datastoreTest)
    testImplementation(libs.hilt.android.testing)
//    testImplementation(projects.sync.syncTest)
    testImplementation(libs.kotlin.test)

    /*testDemoImplementation(libs.androidx.navigation.testing)
    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
    testDemoImplementation(projects.core.screenshotTesting)
    testDemoImplementation(projects.core.testing)

    androidTestImplementation(projects.core.testing)
    androidTestImplementation(projects.core.dataTest)
    androidTestImplementation(projects.core.datastoreTest)*/
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlin.test)

//    baselineProfile(projects.benchmarks)
}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false

    // Make use of Dex Layout Optimizations via Startup Profiles
    dexLayoutOptimization = true
}