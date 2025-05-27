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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.padi.lovemovie.item.MovieGridItem
import com.padi.lovemovie.viewmodel.ComingSoonViewModel
import com.padi.lovemovie.viewmodel.HotViewModel
import com.padi.lovemovie.viewmodel.RankViewModel
import org.json.JSONObject


@Composable
fun ComingSoonPage() {

    val viewModel: ComingSoonViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        if (state.list?.size == 0) {
            viewModel.getComingSoonList()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.list?.isEmpty() == true) {
            CircularProgressIndicator()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp),
                state = listState,
                content = {
                    val list = state.list
                    items(list?.size ?: 0) {
                        val item = list?.get(it)
                        MovieGridItem(item as Any)
                    }
                }
            )
        }
    }
}