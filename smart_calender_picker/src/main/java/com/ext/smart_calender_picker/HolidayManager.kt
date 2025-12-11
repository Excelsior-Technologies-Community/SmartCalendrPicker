package com.ext.smartcalendarpicker

import java.text.SimpleDateFormat
import java.util.Locale

class HolidayManager {

    private val holidays = mutableMapOf<String, String>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /** Add a holiday (yyyy-MM-dd) */
    fun addHoliday(date: String, name: String) {
        if (isValidDate(date)) {
            holidays[date] = name
        }
    }

    /** Get holiday name for a date */
    fun getHoliday(date: String): String? {
        return holidays[date]
    }

    /** Check if date is a holiday */
    fun isHoliday(date: String): Boolean {
        return holidays.containsKey(date)
    }

    /** Remove a holiday */
    fun removeHoliday(date: String) {
        holidays.remove(date)
    }

    /** Get all holidays */
    fun getAllHolidays(): Map<String, String> {
        return holidays.toMap()
    }

    /** Get holidays for a specific year */
    fun getHolidaysForYear(year: Int): Map<String, String> {
        return holidays.filterKeys { it.startsWith("$year-") }
    }

    /** Clear all holidays */
    fun clearHolidays() {
        holidays.clear()
    }

    /** Validate date format */
    private fun isValidDate(date: String): Boolean {
        return try {
            dateFormat.isLenient = false
            dateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }
}
