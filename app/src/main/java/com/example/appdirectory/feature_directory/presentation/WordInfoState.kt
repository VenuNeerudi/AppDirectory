package com.example.appdirectory.feature_directory.presentation

import com.example.appdirectory.feature_directory.domain.model.WordInfo

data class WordInfoState(
    val wordInfoItems: List<WordInfo> = emptyList(),
    val isLoading: Boolean = false,
    val suggestions: List<String> = emptyList()
)