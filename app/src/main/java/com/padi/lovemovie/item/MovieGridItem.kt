package com.padi.lovemovie.item

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.padi.lovemovie.LocalMainNavController
import com.padi.lovemovie.viewmodel.ComingSoonData
import com.padi.lovemovie.viewmodel.RankData
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieGridItem(data: Any) {
    var title = ""
    var imgUrl = ""
    var clickTitle by rememberSaveable { mutableStateOf("") }
    LocalMainNavController.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    val showSheet = rememberSaveable {
        mutableStateOf(false)
    }
    when (data) {
        is JSONObject -> {
            title = data.optString("title") ?: "未知标题"
            imgUrl = data.optString("cover")
        }

        is RankData -> {
            title = data.title ?: "未知标题"
            imgUrl = data.imgUrl ?: ""
        }

        is ComingSoonData -> {
            title = data.title ?: "未知标题"
            imgUrl = data.imgUrl ?: ""
        }
    }

    Surface(
        shape = MaterialTheme.shapes.small, onClick = {
            clickTitle = title
            showSheet.value = true
        }, color = MaterialTheme.colorScheme.surface, border = CardDefaults.outlinedCardBorder(
            enabled = false
        ), modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).httpHeaders(
                    NetworkHeaders.Builder().set("User-Agent", "PostmanRuntime/7.37.0").build()
                ).data(
                    imgUrl
                ).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(8.dp),
                maxLines = 1,
            )
        }

    }
    VideoSheet(showSheet.value, clickTitle, autoSearch = true, sheetState = sheetState) {
        showSheet.value = false
    }
}



