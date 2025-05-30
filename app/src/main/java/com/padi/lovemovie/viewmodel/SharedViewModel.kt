package com.padi.lovemovie.viewmodel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SharedViewModel : ViewModel() {
    data class State(val showBottomSheet: String = "")

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun setShowBottomSheet(value: String) {
        _state.update {
            it.copy(showBottomSheet = value)
        }
    }
}