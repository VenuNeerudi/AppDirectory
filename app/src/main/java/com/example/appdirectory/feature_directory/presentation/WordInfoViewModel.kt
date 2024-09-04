package com.example.appdirectory.feature_directory.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdirectory.core.util.Resource
import com.example.appdirectory.feature_directory.domain.use_case.GetWordInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class WordInfoViewModel @Inject constructor(
    private val getWordInfo: GetWordInfo
) : ViewModel() {

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _state = mutableStateOf(WordInfoState())
    val state: State<WordInfoState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var searchJob: Job? = null

    private val searchHistory = mutableListOf<String>() // Maintain search history

    fun onSearch(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            if (query.isBlank()) {
                // Handle empty query
                _state.value = state.value.copy(
                    wordInfoItems = emptyList(),
                    suggestions = emptyList(), // Clear suggestions
                    isLoading = false
                )
            } else {
                // Update search history
                updateSearchHistory(query)

                // Provide suggestions based on current query
                val suggestions = getSuggestions(query)
                _state.value = state.value.copy(
                    suggestions = suggestions,
                    isLoading = false
                )

                // Fetch word info
                getWordInfo(query)
                    .onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.value = state.value.copy(
                                    wordInfoItems = result.data ?: emptyList(),
                                    isLoading = false
                                )
                            }
                            is Resource.Error -> {
                                _state.value = state.value.copy(
                                    wordInfoItems = result.data ?: emptyList(),
                                    isLoading = false
                                )
                                _eventFlow.emit(UIEvent.ShowSnackbar(
                                    result.message ?: "Unknown error"
                                ))
                            }
                            is Resource.Loading -> {
                                _state.value = state.value.copy(
                                    wordInfoItems = result.data ?: emptyList(),
                                    isLoading = true
                                )
                            }
                        }
                    }.launchIn(this)
            }
        }
    }

    fun getSearchHistory(): List<String> = searchHistory

    private fun updateSearchHistory(query: String) {
        if (query.isNotBlank() && !searchHistory.contains(query)) {
            searchHistory.add(query)
        }
    }

    private fun getSuggestions(query: String): List<String> {
        return searchHistory.filter { it.contains(query, ignoreCase = true) }
    }

    sealed class UIEvent {
        data class ShowSnackbar(val message: String): UIEvent()
    }
}
