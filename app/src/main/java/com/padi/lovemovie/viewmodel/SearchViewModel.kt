package com.padi.lovemovie.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.padi.lovemovie.service.GeneralRetrofit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class SearchViewModel(): ViewModel() {
    data class State(val json: JSONObject? = JSONObject())
    val state = MutableStateFlow(State())

    fun clearResultList(){
        state.update {
            it.copy(
                json = JSONObject()
            )
        }
    }
    fun getResultList(url: String) = viewModelScope.launch {
        runCatching {
            val call = GeneralRetrofit.instance.request(url).await()
            val json = call.string()
            val jsonArray = JSONObject(json)
            state.update {
                it.copy(
                    json = jsonArray
                )
            }

        }.onFailure {
            it.printStackTrace()
        }
    }
}