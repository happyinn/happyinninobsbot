package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun VideosScreen(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VideosViewModel = hiltViewModel(),
) {

}