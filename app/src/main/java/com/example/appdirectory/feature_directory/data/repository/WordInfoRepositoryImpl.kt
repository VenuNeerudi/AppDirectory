package com.example.appdirectory.feature_directory.data.repository

import com.example.appdirectory.core.util.Resource
import com.example.appdirectory.feature_directory.data.local.WordInfoDao
import com.example.appdirectory.feature_directory.data.remote.DictionaryApi
import com.example.appdirectory.feature_directory.domain.model.WordInfo
import com.example.appdirectory.feature_directory.domain.repository.WordInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class WordInfoRepositoryImpl(
    private val api: DictionaryApi,
    private val dao: WordInfoDao
): WordInfoRepository {

    override fun getWordInfo(word: String): Flow<Resource<List<WordInfo>>> = flow {
        emit(Resource.Loading<List<WordInfo>>())

        // Explicitly specify the type of `cachedWordInfos`
        val cachedWordInfos: List<WordInfo> = dao.getWordInfos(word).map { it.toWordInfo() }
        emit(Resource.Loading(data = cachedWordInfos))

        try {
            val remoteWordInfos = api.getWordInfo(word)
            dao.deleteWordInfos(remoteWordInfos.map { it.word })
            dao.insertWordInfos(remoteWordInfos.map { it.toWordInfoEntity() })

            val updatedWordInfos: List<WordInfo> = dao.getWordInfos(word).map { it.toWordInfo() }
            emit(Resource.Success(updatedWordInfos))
        } catch(e: HttpException) {
            emit(Resource.Error(
                message = "Oops, something went wrong!",
                data = cachedWordInfos
            ))
        } catch(e: IOException) {
            emit(Resource.Error(
                message = "Couldn't reach server, check your internet connection.",
                data = cachedWordInfos
            ))
        }
    }
}
