package com.ombati.guidecaneapp.presentation.home

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates the distance between two geographical points on the Earth's surface using the Haversine formula.
 *
 * @param lat1 Latitude of the first point in degrees.
 * @param lon1 Longitude of the first point in degrees.
 * @param lat2 Latitude of the geofencing point in degrees.
 * @param lon2 Longitude of the geofencing point in degrees.
 * @return The distance in meters.
 */
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
    val earthRadius = 6371000.0

    val lat1Rad = Math.toRadians(lat1)
    val lon1Rad = Math.toRadians(lon1)
    val lat2Rad = Math.toRadians(lat2)
    val lon2Rad = Math.toRadians(lon2)


    val dLat = lat2Rad - lat1Rad
    val dLon = lon2Rad - lon1Rad
    val a = sin(dLat / 2).pow(2) +
            cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return (earthRadius * c).toFloat()
}
