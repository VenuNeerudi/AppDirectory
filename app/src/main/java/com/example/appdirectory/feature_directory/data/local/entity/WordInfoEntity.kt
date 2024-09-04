package com.example.appdirectory.feature_directory.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.appdirectory.feature_directory.domain.model.Meaning
import com.example.appdirectory.feature_directory.domain.model.WordInfo

@Entity
data class WordInfoEntity(
    val word: String,
    val phonetic: String?, // Allow phonetic to be nullable
    val origin: String?, // Allow origin to be nullable
    val meanings: List<Meaning>,
    @PrimaryKey val id: Int? = null
) {
    fun toWordInfo(): WordInfo {
        return WordInfo(
            meanings = meanings,
            word = word,
            origin = origin ?: "", // Provide default value if origin is null
            phonetic = phonetic ?: "" // Provide default value if phonetic is null
        )
    }
}