package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation


import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class FolderNavKey(val folderPath: String) : NavKey

fun Navigator.navigateToFolder(
    folderPath: String,
) {
    navigate(FolderNavKey(folderPath))
}
