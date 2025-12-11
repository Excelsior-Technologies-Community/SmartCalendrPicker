package com.ext.smartcalendarpicker

import java.util.UUID

//data class CalendarEvent(
//    val title: String,
//    val color: String = "#4CAF50"
//)
data class CalendarEvent(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val color: String = "#2196F3",
    val startTime: String? = null,   // "10:00"
    val endTime: String? = null      // "11:00"
)
