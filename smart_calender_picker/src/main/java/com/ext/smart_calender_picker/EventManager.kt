package com.ext.smart_calender_picker

import com.ext.smartcalendarpicker.CalendarEvent

class EventManager {

    private val eventsMap = mutableMapOf<String, MutableList<CalendarEvent>>()

    fun addEvent(date: String, event: CalendarEvent) {
        val events = eventsMap[date] ?: mutableListOf()
        events.add(event)
        eventsMap[date] = events
    }

    fun getEvents(date: String): List<CalendarEvent> {
        return eventsMap[date] ?: emptyList()
    }

    fun hasEvents(date: String): Boolean {
        return eventsMap.containsKey(date)
    }

    fun removeEvent(date: String, eventId: String) {
        eventsMap[date]?.removeIf { it.id == eventId }
        if (eventsMap[date].isNullOrEmpty()) {
            eventsMap.remove(date)
        }
    }

    fun clearAll() {
        eventsMap.clear()
    }
}
