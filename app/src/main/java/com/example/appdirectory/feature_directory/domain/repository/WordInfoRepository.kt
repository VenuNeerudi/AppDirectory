package com.example.appdirectory.feature_directory.domain.repository

import com.example.appdirectory.core.util.Resource
import com.example.appdirectory.feature_directory.domain.model.WordInfo
import kotlinx.coroutines.flow.Flow

interface WordInfoRepository {

    fun getWordInfo(word: String): Flow<Resource<List<WordInfo>>>
}