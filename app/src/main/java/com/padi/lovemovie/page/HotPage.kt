package com.padi.lovemovie.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.padi.lovemovie.viewmodel.HotViewModel
import org.json.JSONObject
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.padi.lovemovie.item.MovieGridItem

@Composable
fun HotPage() {
    val viewModel: HotViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyGridState()
    LaunchedEffect(Unit) {
        if (state.list?.length() == 0) {
            viewModel.getHotList()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.list == null || state.list?.length() == 0) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp),
                state = listState,
                content = {
                    val subjects = state.list?.optJSONArray("subjects")
                    items(subjects?.length() ?: 0) {
                        val item = subjects?.get(it) as JSONObject?
                        MovieGridItem(item as Any)
                    }
                }
            )
        }
    }
}