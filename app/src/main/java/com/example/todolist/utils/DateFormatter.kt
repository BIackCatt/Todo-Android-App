package com.example.todolist.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun getFormattedDate(): String {
    val date = Date() // Current date and time
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Example: "Jan 20, 2025"
    return formatter.format(date) // Formats the date as a string
}