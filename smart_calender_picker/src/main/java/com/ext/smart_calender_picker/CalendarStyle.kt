package com.ext.smart_calender_picker

import android.graphics.Typeface

data class CalendarStyle(

    //-----------------------
    // DAY TEXT & FONT
    //-----------------------
    val dayTextColor: String = "#000000",
    val dayTextSize: Float = 14f,
    val dayFont: Typeface? = null,

    //-----------------------
    // HEADER (MON, TUE...)
    //-----------------------
    val headerTextColor: String = "#666666",
    val headerTextSize: Float = 13f,
    val headerFont: Typeface? = null,

    //-----------------------
    // OUTSIDE MONTH DAYS
    //-----------------------
    val outsideMonthTextColor: String = "#CCCCCC",

    //-----------------------
    // TODAY CELL
    //-----------------------
    val todayBackground: String = "#E3F2FD",
    val todayTextColor: String = "#000000",
    val todayFont: Typeface? = null,

    //-----------------------
    // SELECTED DAY CELL
    //-----------------------
    val selectedBackground: String = "#BBDEFB",
    val selectedTextColor: String = "#1976D2",
    val selectedFont: Typeface? = null,

    //-----------------------
    // RANGE START–END CELLS
    //-----------------------
    val rangeBackground: String = "#2196F3",
    val rangeTextColor: String = "#FFFFFF",
    val rangeFont: Typeface? = null,

    //-----------------------
    // RANGE MIDDLE CELLS
    //-----------------------
    val rangeMiddleBackground: String = "#64B5F6",
    val rangeMiddleTextColor: String = "#FFFFFF",

    //-----------------------
    // HOLIDAYS
    //-----------------------
    val holidayTextColor: String = "#D32F2F",
    val holidayDotColor: String = "#D32F2F",
    val holidayTextSize: Float = 10f,
    val holidayFont: Typeface? = null,

    //-----------------------
    // EVENTS
    //-----------------------
    val eventTextColor: String = "#000000",
    val eventTextSize: Float = 12f,
    val eventDefaultDotColor: String = "#4CAF50",
    val eventFont: Typeface? = null,
    val dotSize: Int = 12,

    //-----------------------
    // MONTH HEADER AREA
    //-----------------------
    val monthTitleColor: String = "#000000",
    val monthTitleSize: Float = 18f,
    val monthTitleFont: Typeface? = null,
    val monthBackgroundColor: String = "#FFFFFF",

    //-----------------------
    // MONTH NAVIGATION ARROWS
    //-----------------------
    val arrowColor: String = "#000000",
    val arrowSize: Int = 30,

    //-----------------------
    // MODE BUTTONS (Single/Multi/Range)
    //-----------------------
    val modeButtonBackground: String = "#EEEEEE",
    val modeButtonSelectedBackground: String = "#2196F3",
    val modeButtonTextColor: String = "#000000",
    val modeButtonSelectedTextColor: String = "#FFFFFF",
    val modeButtonFont: Typeface? = null,
    val modeButtonTextSize: Float = 14f,
    val modeButtonCornerRadius: Float = 50f,

    //-----------------------
    // CLEAR BUTTON
    //-----------------------
    val clearButtonBackground: String = "#F44336",
    val clearButtonTextColor: String = "#FFFFFF",
    val clearButtonFont: Typeface? = null,
    val clearButtonTextSize: Float = 14f,

    //-----------------------
    // CONFIRM / APPLY BUTTON
    //-----------------------
    val applyButtonBackground: String = "#2196F3",
    val applyButtonTextColor: String = "#FFFFFF",
    val applyButtonFont: Typeface? = null,
    val applyButtonTextSize: Float = 14f,

    //-----------------------
    // DIALOG BOX
    //-----------------------
    val dialogBackgroundColor: String = "#FFFFFF",
    val dialogTitleColor: String = "#000000",
    val dialogTitleSize: Float = 18f,
    val dialogTitleFont: Typeface? = null,

    //-----------------------
    // DIALOG ACTION BUTTONS
    //-----------------------
    val dialogButtonColor: String = "#2196F3",
    val dialogButtonTextColor: String = "#FFFFFF",
    val dialogButtonFont: Typeface? = null,
    val dialogButtonTextSize: Float = 14f,

    //-----------------------
    // CALENDAR OVERALL BACKGROUND
    //-----------------------
    val calendarBackgroundColor: String = "#FFFFFF"
)
