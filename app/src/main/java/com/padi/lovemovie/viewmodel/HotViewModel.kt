package com.padi.lovemovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.lovemovie.service.HotRetrofit
import com.padi.lovemovie.service.HotRetrofit.HotService
import com.padi.lovemovie.service.HotRetrofit.hotService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject

class HotViewModel : ViewModel() {
    data class State(val list: JSONObject? = JSONObject())

    val state = MutableStateFlow(State())

    fun getHotList() = viewModelScope.launch {
        runCatching {
            val call = hotService.getHotData().await()
            val json = call.string()
            val jsonObject = JSONObject(json)
            state.update {
                it.copy(
                    list = jsonObject
                )
            }

        }.onFailure {
            it.printStackTrace()
        }
    }

}