package com.example.appdirectory.feature_directory.data.remote.dto

import android.util.Log
import com.example.appdirectory.feature_directory.data.local.entity.WordInfoEntity

data class WordInfoDto(
    val meanings: List<MeaningDto>,
    val origin: String,
    val phonetic: String?, // Allow phonetic to be nullable
    val phonetics: List<PhoneticDto>,
    val word: String
) {
    fun toWordInfoEntity(): WordInfoEntity {
        Log.d("WordInfoDto", "word: $word, phonetic: $phonetic, origin: $origin")
        return WordInfoEntity(
            meanings = meanings.map { it.toMeaning() },
            origin = origin,
            phonetic = phonetic ?: "", // Provide a default value if phonetic is null
            word = word
        )
    }
}