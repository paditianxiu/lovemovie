package com.padi.lovemovie.page

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.padi.lovemovie.viewmodel.RankViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.padi.lovemovie.item.MovieGridItem
import org.json.JSONObject

@Composable
fun RankPage() {
    val viewModel: RankViewModel = viewModel()
    val listState = rememberLazyGridState()
    val data = viewModel.flow.collectAsLazyPagingItems()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(4.dp),
            state = listState,
            content = {
                items(data.itemCount) {
                    val item = data[it]
                    println(item)
                    MovieGridItem(item as Any)
                }
                item(span = {
                    GridItemSpan(3)
                }) {

                    if (data.loadState.append is LoadState.Loading) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                    }
                }

            })

        if (data.loadState.refresh is LoadState.Loading) {
            CircularProgressIndicator()
        }
    }
}