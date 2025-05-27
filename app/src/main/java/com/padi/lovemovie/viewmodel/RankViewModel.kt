package com.padi.lovemovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.lovemovie.service.GeneralRetrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

data class RankData(
    val title: String?,
    val imgUrl: String?
)

class RankViewModel : ViewModel() {
    data class State(val list: List<RankData>? = mutableListOf<RankData>())

    val state = MutableStateFlow(State())

    fun getRankList() = viewModelScope.launch {
        runCatching {
            val call =
                GeneralRetrofit.instance.request("https://movie.douban.com/top250?start=0&filter=")
                    .await()
            val str = call.string()
            val doc: Document = Jsoup.parse(str)
            val gridView: Element? = doc.selectFirst(".grid_view")
            val items = gridView?.select("li")
            val list = mutableListOf<RankData>()
            items?.forEach { item ->
                val imgElement = item.selectFirst("img")
                val title = imgElement?.attr("alt")
                val imgUrl = imgElement?.attr("src")
                list.add(RankData(title, imgUrl))
            }
            state.update {
                it.copy(
                    list = list
                )
            }

        }.onFailure {
            it.printStackTrace()
        }
    }
}