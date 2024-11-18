package com.ombati.guidecaneapp.data.repositories.storage

import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.data.model.GuideCaneUser
import com.ombati.guidecaneapp.data.model.History

interface GuideCaneRepository {

    fun fetchGuideCaneUsersForCurrentUser(
        appUserId: String,
        onGuideCaneUsersUpdated: (Result<List<GuideCaneUser>>) -> Unit
    )
    suspend fun fetchGuideCaneUser(smartCaneId: String): Result<GuideCaneUser>
    suspend fun updateGuideCaneUser(smartCaneId: String, updates: Map<String, Any>): Result<Unit>
    suspend fun updateAppUser(appUserId: String, smartCaneId: String, securityKey: String?, add: Boolean): Result<Unit>

    suspend fun fetchUserHistory(smartCaneId: String): Result<List<History>>
    suspend fun removeSmartCaneIdFromAppUser(smartCaneId: String): Result<Unit>

}
