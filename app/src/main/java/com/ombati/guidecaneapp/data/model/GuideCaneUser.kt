package com.ombati.guidecaneapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName


data class GuideCaneUser(
    @DocumentId val id: String = "",
    @PropertyName("battery_level") val batteryLevel: String = "",
    @PropertyName("caregiver_number") val caregiverNumber: String = "",
    @PropertyName("emergency_status") val emergencyStatus: String = "",
    @PropertyName("geo_fencing") val geoFencing: String = "",
    @PropertyName("latitude") val latitude: Double = 0.0,
    @PropertyName("longitude") val longitude: Double = 0.0,
    @PropertyName("geo_fen_lat") val geoFencingLatitude: Double = 0.0,
    @PropertyName("geo_fen_lon") val geoFencingLongitude: Double = 0.0,
    @PropertyName("security_key") val securityKey: String = "",
    @PropertyName("smart_cane_id") val smartCaneId: String = ""
)


