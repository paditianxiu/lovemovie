package com.padi.lovemovie.viewmodel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SharedViewModel : ViewModel() {
    data class State(val showBottomSheet: Boolean = false)

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun setShowBottomSheet(value: Boolean) {
        _state.update {
            it.copy(showBottomSheet = value)
        }
    }
}