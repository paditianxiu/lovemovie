package com.padi.lovemovie.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.padi.lovemovie.LocalMainNavController
import com.padi.lovemovie.viewmodel.ApiViewModel
import com.padi.lovemovie.viewmodel.SearchViewModel
import org.json.JSONObject
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSheet(showSheet: Boolean,clickTitle: String,sheetState: SheetState,autoSearch: Boolean = false,onSheetStateChange:(Boolean) -> Unit) {
    if (showSheet) {
        val apiViewModel: ApiViewModel = viewModel()
        val apiState by apiViewModel.state.collectAsState()
        val viewModel: SearchViewModel = viewModel()
        val state by viewModel.state.collectAsState()
        val list = apiState.list
        val json = state.json
        var selectedTabIndex by remember { mutableIntStateOf(0) }
        val scope = rememberCoroutineScope()
        var keyword by rememberSaveable { mutableStateOf(clickTitle) }
        val listState = rememberLazyGridState()
        var isSearch by remember { mutableStateOf(false) }
        var currentShape by remember { mutableStateOf<Shape>(RoundedCornerShape(16.dp)) }
        val mainNavController = LocalMainNavController.current
        LaunchedEffect(sheetState.currentValue) {
            currentShape = if (sheetState.currentValue == SheetValue.Expanded) {
                RectangleShape
            } else {
                RoundedCornerShape(16.dp)
            }
        }
        LaunchedEffect(Unit) {
            if (list?.length() == 0) {
                apiViewModel.getApiList()
            }

        }
        ModalBottomSheet(
            onDismissRequest = {
                onSheetStateChange(false)
            },
            sheetState = sheetState,
            shape = currentShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "发现精彩生活",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                TextField(
                    label = { Text("搜索") },
                    value = keyword,
                    onValueChange = { keyword = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                )
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    for (index in 0 until (list?.length() ?: 0)) {
                        val item = list?.get(index) as JSONObject
                        Tab(selected = selectedTabIndex == index, onClick = {
                            selectedTabIndex = index
                        }, text = { Text(item.optString("name")) })
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val item = list?.get(selectedTabIndex) as JSONObject
                        val api = item.optString("url")
                        val url = "${api}?ac=detail&wd=${URLEncoder.encode(keyword, "UTF-8")}"
                        isSearch = true
                        viewModel.clearResultList()
                        viewModel.getResultList(url)
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Text("搜索")
                }
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isSearch && state.json?.length() == 0) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        LazyVerticalGrid(
                            modifier = Modifier.fillMaxSize(),
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            contentPadding = PaddingValues(4.dp),
                            state = listState,
                            content = {
                                val list = state.json?.optJSONArray("list")
                                items(list?.length() ?: 0) {
                                    val item = list?.get(it) as JSONObject?
                                    val name = item?.optString("vod_name") ?: "未知标题"
                                    val pic = item?.optString("vod_pic") ?: ""
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        onClick = {
                                            mainNavController.navigate(
                                                "detail/${
                                                    URLEncoder.encode(
                                                        item.toString(), "UTF-8"
                                                    )
                                                }"
                                            )
                                        },
                                        color = MaterialTheme.colorScheme.surface,
                                        border = CardDefaults.outlinedCardBorder(
                                            enabled = false
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .httpHeaders(
                                                        NetworkHeaders.Builder().set(
                                                            "User-Agent", "PostmanRuntime/7.37.0"
                                                        ).build()
                                                    ).data(pic).crossfade(true).build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                            )
                                            Text(
                                                name,
                                                style = MaterialTheme.typography.labelSmall,
                                                modifier = Modifier.padding(8.dp),
                                                maxLines = 1,
                                            )
                                        }
                                    }
                                }


                            })
                    }
                }
            }

        }
    }

}