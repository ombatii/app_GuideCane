package com.ombati.guidecaneapp.data.model

import com.google.firebase.firestore.DocumentId


data class AppUser(
    @DocumentId val appUserId: String = "",
    val guideCaneUsers: List<String> = emptyList()
)
