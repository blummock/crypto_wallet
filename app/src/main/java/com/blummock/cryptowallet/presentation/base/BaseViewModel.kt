package com.blummock.cryptowallet.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

internal abstract class BaseViewModel<State, Effect, Action>(state: State) : ViewModel() {

    private val _state = MutableStateFlow(state)
    val state = _state.asStateFlow()

    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    protected fun updateState(function: (State) -> State) {
        _state.update { state ->
            function(state)
        }
    }

    protected suspend fun postEffect(effect: Effect) {
        _effect.send(effect)
    }

    abstract fun onAction(action: Action)
}