package com.ext.smart_calender_picker

import com.ext.smartcalendarpicker.CalendarEvent

data class CalendarDay(
    val date: String = "",
    val dayOfMonth: String = "",
    val isCurrentMonth: Boolean = false,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val isRangeStart: Boolean = false,
    val isRangeEnd: Boolean = false,
    val isHeader: Boolean = false,
    val holiday: String? = null,
    val events: List<CalendarEvent> = emptyList()
)


//data class CalendarDay(
//    val date: String = "",
//    val dayOfMonth: String = "",
//    val isCurrentMonth: Boolean = true,
//    val isToday: Boolean = false,
//    val isSelected: Boolean = false,
//    val isRangeStart: Boolean = false,
//    val isRangeEnd: Boolean = false,
//    val isHeader: Boolean = false,
//    val holiday: String? = null,
//    val events: List<CalendarEvent> = emptyList()
//)


//data class CalendarDay(
//    val date: String,
//    val dayOfMonth: String,
//    val isCurrentMonth: Boolean = true,
//    val isToday: Boolean = false,
//    val isSelected: Boolean = false,
//    val isRangeStart: Boolean = false,
//    val isRangeEnd: Boolean = false,
//    val isHeader: Boolean = false,
//    val holiday: String? = null,
//    val events: List<CalendarEvent> = emptyList() // <-- changed from single event
//)



