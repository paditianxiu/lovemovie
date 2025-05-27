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

data class ComingSoonData(
    val title: String?,
    val imgUrl: String?
)

class ComingSoonViewModel : ViewModel(){
    data class State(val list: List<ComingSoonData>? = listOf<ComingSoonData>())

    val state = MutableStateFlow(State())

    fun getComingSoonList() = viewModelScope.launch {
        runCatching {
            val call =
                GeneralRetrofit.instance.request("https://movie.douban.com/cinema/later/")
                    .await()
            val str = call.string()
            val doc: Document = Jsoup.parse(str)
            val gridView: Element? = doc.selectFirst(".tab-bd")
            val items = gridView?.select(".item")
            val list = mutableListOf<ComingSoonData>()
            items?.forEach { item ->
                val imgElement = item.selectFirst("img")
                val intro =item.selectFirst(".intro")
                val title =intro?.selectFirst("a")?.text()
                val imgUrl = imgElement?.attr("src")
                list.add(ComingSoonData(title, imgUrl))
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