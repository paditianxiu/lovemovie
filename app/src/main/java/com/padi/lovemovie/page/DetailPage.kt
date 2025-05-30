package com.padi.lovemovie.page

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MimeTypes
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import io.sanghun.compose.video.VideoPlayer
import io.sanghun.compose.video.controller.VideoPlayerControllerConfig
import io.sanghun.compose.video.uri.VideoPlayerMediaItem
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailPage(json: String?, onBack: () -> Unit) {
    val listState = rememberLazyGridState()
    val jsonObject = JSONObject(json ?: "")
    val playUrl = jsonObject.optString("vod_play_url") ?: ""
    val list = playUrl.split("#")
    val imgUrl = jsonObject.optString("vod_pic") ?: ""
    val title = jsonObject.optString("vod_name") ?: ""
    val detail = jsonObject.optString("vod_blurb") ?: ""
    val navController = rememberNavController()
    var videoUrl by rememberSaveable { mutableStateOf(list[0].split("$")[1]) }
    val ctx = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            val fullScreen = remember {
                mutableStateOf(false)
            }
            Scaffold(
                contentWindowInsets = WindowInsets(0),
                topBar = {
                    if (!fullScreen.value)
                        TopAppBar(title = { Text("详情页") }, navigationIcon = {
                            IconButton(onClick = {
                                onBack()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack, contentDescription = null
                                )
                            }
                        })
                }) { innerPadding ->
                Column(modifier = if (!fullScreen.value) Modifier.padding(innerPadding) else Modifier) {
                    VideoPlayer(
                        mediaItems = listOf(
                            VideoPlayerMediaItem.NetworkMediaItem(
                                url = videoUrl,
                                mediaMetadata = MediaMetadata.Builder()
                                    .setTitle("Widevine DASH cbcs: Tears").build(),
                                mimeType = MimeTypes.APPLICATION_M3U8,
                            )
                        ),
                        handleLifecycle = true,
                        usePlayerController = true,
                        autoPlay = true,
                        controllerConfig = VideoPlayerControllerConfig(
                            showSpeedAndPitchOverlay = false,
                            showSubtitleButton = false,
                            showCurrentTimeAndTotalTime = true,
                            showBufferingProgress = false,
                            showForwardIncrementButton = true,
                            showBackwardIncrementButton = true,
                            showBackTrackButton = true,
                            showNextTrackButton = true,
                            showRepeatModeButton = true,
                            controllerShowTimeMilliSeconds = 2_000,
                            controllerAutoShow = true,
                            showFullScreenButton = true,
                        ),
                        onFullScreenEnter = {
                            fullScreen.value = true
                        },
                        onFullScreenExit = {
                            fullScreen.value = false
                            (ctx as androidx.activity.ComponentActivity).enableEdgeToEdge(
                                statusBarStyle = SystemBarStyle.light(
                                    scrim = Color.TRANSPARENT,
                                    darkScrim = Color.TRANSPARENT
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (fullScreen.value) {
                                    Modifier.fillMaxSize()
                                } else {
                                    Modifier.height(250.dp)
                                }
                            )
                    )
                    if (!fullScreen.value) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).httpHeaders(
                                    NetworkHeaders.Builder()
                                        .set("User-Agent", "PostmanRuntime/7.37.0")
                                        .build()
                                ).data(
                                    imgUrl
                                ).crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height((100 * 1.5).dp)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Column {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.titleMedium + TextStyle(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1,
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    detail,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(4.dp),
                            state = listState,
                            content = {
                                items(list.size) {
                                    val item = list[it]
                                    AssistChip(leadingIcon = {
                                        Icon(
                                            Icons.Filled.FavoriteBorder,
                                            contentDescription = null,
                                            Modifier.size(AssistChipDefaults.IconSize)
                                        )
                                    }, onClick = {
                                        videoUrl = item.split("$")[1]
                                    }, label = { Text(item.split("$")[0]) })
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}