package com.padi.lovemovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.padi.lovemovie.service.GeneralRetrofit
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

data class RankData(
    val title: String?, val imgUrl: String?
)

class RankViewModel : ViewModel() {
    data class State(val list: List<RankData>? = mutableListOf<RankData>())
    val state = MutableStateFlow(State())

    val flow= Pager(config = PagingConfig(pageSize = 25)){
        RankPagingSource()
    }.flow
        .cachedIn(viewModelScope)
    class RankPagingSource : PagingSource<Int, RankData>() {
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RankData> {
            return try {
                val nextPageNumber = params.key ?: 1
                val response = getRankList(nextPageNumber)
                LoadResult.Page(
                    data = response,
                    prevKey = null,
                    nextKey = if (response.isNotEmpty()) nextPageNumber + 1 else null
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        private suspend fun getRankList(page: Int): List<RankData> {
            return try {
                val call = GeneralRetrofit.instance
                    .request("https://movie.douban.com/top250?start=${(page - 1) * 25}&filter=")
                    .await()

                val str = call.string()
                val doc: Document = Jsoup.parse(str)
                val gridView: Element? = doc.selectFirst(".grid_view")
                val items = gridView?.select("li")

                items?.mapNotNull { item ->
                    val imgElement = item.selectFirst("img")
                    val title = imgElement?.attr("alt")
                    val imgUrl = imgElement?.attr("src")

                    if (title != null && imgUrl != null) {
                        RankData(title, imgUrl)
                    } else {
                        null
                    }
                } ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

        override fun getRefreshKey(state: PagingState<Int, RankData>): Int? {
            return state.anchorPosition?.let { anchorPosition ->
                val anchorPage = state.closestPageToPosition(anchorPosition)
                anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
            }
        }
    }
}