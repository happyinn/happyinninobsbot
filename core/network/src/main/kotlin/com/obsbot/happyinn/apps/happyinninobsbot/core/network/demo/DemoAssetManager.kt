
package com.obsbot.happyinn.apps.happyinninobsbot.core.network.demo

import java.io.InputStream

fun interface DemoAssetManager {
    fun open(fileName: String): InputStream
}
