package com.ivy.core.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class FlowViewModel<InternalState, UiState, Event> : ViewModel() {
    private val events = MutableSharedFlow<Event>(replay = 0)

    protected abstract val initialState: InternalState
    protected abstract val initialUi: UiState

    protected abstract val stateFlow: Flow<InternalState>
    protected abstract val uiFlow: Flow<UiState>

    protected abstract suspend fun handleEvent(event: Event)

    protected val state: StateFlow<InternalState> by lazy {
        stateFlow
            .flowOn(Dispatchers.Main)
            .onEach {
                Timber.d("Internal state = $it")
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = initialState,
            )
    }

    val uiState: StateFlow<UiState> by lazy {
        uiFlow.onEach {
            Timber.d("UI state = $it")
        }.flowOn(Dispatchers.Main)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                initialValue = initialUi,
            )
    }

    init {
        viewModelScope.launch {
            events.collect(::handleEvent)
        }
        viewModelScope.launch {
            // without this delay it crashes because isn't instantiated
            delay(100)
            state // init the lazy val for the internal state
        }
    }

    fun onEvent(event: Event) {
        viewModelScope.launch {
            events.emit(event)
        }
    }
}