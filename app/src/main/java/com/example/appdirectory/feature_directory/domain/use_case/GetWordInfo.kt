package com.example.appdirectory.feature_directory.domain.use_case

import com.example.appdirectory.core.util.Resource
import com.example.appdirectory.feature_directory.domain.model.WordInfo
import com.example.appdirectory.feature_directory.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWordInfo(
    private val repository: WordInfoRepository
) {

    operator fun invoke(word: String): Flow<Resource<List<WordInfo>>> {
        if(word.isBlank()) {
            return flow {  }
        }
        return repository.getWordInfo(word)
    }
}