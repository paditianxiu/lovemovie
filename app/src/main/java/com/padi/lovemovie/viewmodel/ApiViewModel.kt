package com.padi.lovemovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.lovemovie.service.GeneralRetrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ApiViewModel: ViewModel() {
    data class State(val list: JSONArray? = JSONArray())
    val state = MutableStateFlow(State())
    fun getApiList() = viewModelScope.launch {
        runCatching {
            val call = GeneralRetrofit.instance.request("https://gitee.com/padi/padi/raw/master/lovesearch.json").await()
            val json = call.string()
            val jsonArray = JSONArray(json)
            state.update {
                it.copy(
                    list = jsonArray
                )
            }

        }.onFailure {
            it.printStackTrace()
        }
    }
}