package com.ombati.guidecaneapp.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getCurrentTimeAsString(): String {
    val currentTime = Date()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return dateFormat.format(currentTime)
}

fun convertDateFormat(date: Date): String {
    // Set the desired format and time zone (EAT)
    val newDateFormat = SimpleDateFormat("EEEE MMMM d, yyyy, h:mma", Locale.getDefault())
    newDateFormat.timeZone = TimeZone.getTimeZone("Africa/Nairobi") // Set to EAT (UTC+3)
    return newDateFormat.format(date)
}

