package com.ext.smart_calender_picker

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ext.smartcalendarpicker.CalendarEvent

class CalendarAdapter(
    private val style: CalendarStyle,
    private val onDayClick: (CalendarDay) -> Unit,
    private val onDayLongClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.DayViewHolder>(DayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view, style, onDayClick, onDayLongClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DayViewHolder(
        itemView: View,
        private val style: CalendarStyle,
        private val onDayClick: (CalendarDay) -> Unit,
        private val onDayLongClick: (CalendarDay) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
        private val layoutEventsContainer: LinearLayout = itemView.findViewById(R.id.layoutEventDots)
        private val cardDay: CardView = itemView.findViewById(R.id.cardDay)

        fun bind(day: CalendarDay) {
            tvDay.text = day.dayOfMonth

            // Reset default style
            tvDay.setTextColor(parseColor(style.dayTextColor))
            tvDay.textSize = style.dayTextSize
            style.dayFont?.let { tvDay.typeface = it } ?: tvDay.setTypeface(null, Typeface.NORMAL)
            cardDay.setCardBackgroundColor(Color.TRANSPARENT)

            // Slightly taller boxes
            val minHeightInDp = 60
            val density = itemView.context.resources.displayMetrics.density
            cardDay.minimumHeight = (minHeightInDp * density).toInt()
            cardDay.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT

            layoutEventsContainer.removeAllViews()
            layoutEventsContainer.visibility = View.GONE

            // HEADER
            if (day.isHeader) {
                tvDay.setTextColor(parseColor(style.headerTextColor))
                tvDay.textSize = style.headerTextSize
                style.headerFont?.let { tvDay.typeface = it } ?: tvDay.setTypeface(null, Typeface.BOLD)
                itemView.setOnClickListener(null)
                itemView.setOnLongClickListener(null)
                return
            }

            // EMPTY / OUTSIDE MONTH
            if (day.date.isEmpty() || !day.isCurrentMonth) {
                tvDay.setTextColor(parseColor(style.outsideMonthTextColor))
                itemView.setOnClickListener(null)
                itemView.setOnLongClickListener(null)
                return
            }

            // TODAY
            if (day.isToday) {
                cardDay.setCardBackgroundColor(parseColor(style.todayBackground))
                tvDay.setTextColor(parseColor(style.todayTextColor))
                style.todayFont?.let { tvDay.typeface = it } ?: tvDay.setTypeface(null, Typeface.BOLD)
            }

            // SELECTION & RANGE
            when {
                day.isRangeStart || day.isRangeEnd -> {
                    cardDay.setCardBackgroundColor(parseColor(style.rangeBackground))
                    tvDay.setTextColor(parseColor(style.rangeTextColor))
                    style.rangeFont?.let { tvDay.typeface = it } ?: tvDay.setTypeface(null, Typeface.BOLD)
                }
                day.isSelected -> {
                    cardDay.setCardBackgroundColor(parseColor(style.selectedBackground))
                    tvDay.setTextColor(parseColor(style.selectedTextColor))
                    style.selectedFont?.let { tvDay.typeface = it } ?: tvDay.setTypeface(null, Typeface.BOLD)
                }
            }

            // HOLIDAYS
            day.holiday?.let { holidayName: String ->
                tvDay.setTextColor(parseColor(style.holidayTextColor))

                val holidayLine = LinearLayout(itemView.context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(0, 2, 0, 2)
                }

                val dot = View(itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(style.dotSize, style.dotSize).apply {
                        setMargins(0, 0, 4, 0)
                    }
                    setBackgroundColor(parseColor(style.holidayDotColor))
                }

                val tvHoliday = TextView(itemView.context).apply {
                    text = holidayName
                    setTextColor(parseColor(style.holidayTextColor))
                    textSize = style.holidayTextSize
                    maxLines = Integer.MAX_VALUE // allow multiple lines
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    style.holidayFont?.let { typeface = it }
                }

                holidayLine.addView(dot)
                holidayLine.addView(tvHoliday)
                layoutEventsContainer.addView(holidayLine)
                layoutEventsContainer.visibility = View.VISIBLE
            }

            // EVENTS
            if (day.events.isNotEmpty()) {
                layoutEventsContainer.visibility = View.VISIBLE
                day.events.forEach { event: CalendarEvent ->
                    val eventLine = LinearLayout(itemView.context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.START or Gravity.CENTER_VERTICAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        setPadding(0, 2, 0, 2)
                    }

                    val dot = View(itemView.context).apply {
                        layoutParams = LinearLayout.LayoutParams(style.dotSize, style.dotSize).apply {
                            setMargins(0, 0, 4, 0)
                        }
                        try {
                            setBackgroundColor(parseColor(event.color))
                        } catch (_: Exception) {
                            setBackgroundColor(parseColor(style.eventDefaultDotColor))
                        }
                    }

                    val tvEvent = TextView(itemView.context).apply {
                        text = event.title
                        setTextColor(parseColor(style.eventTextColor))
                        textSize = style.eventTextSize
                        maxLines = Int.MAX_VALUE
                        layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                        )
                        style.eventFont?.let { typeface = it }
                    }

                    eventLine.addView(dot)
                    eventLine.addView(tvEvent)
                    layoutEventsContainer.addView(eventLine)
                }
            }

            // CLICK
            itemView.setOnClickListener { onDayClick(day) }
            itemView.setOnLongClickListener {
                onDayLongClick(day)
                true
            }
        }

        private fun parseColor(colorString: String): Int {
            return try {
                Color.parseColor(colorString)
            } catch (_: Exception) {
                Color.BLACK
            }
        }
    }

    private class DayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean =
            oldItem == newItem
    }
}


//package com.ext.smartcalendarpicker
//
//import android.graphics.Color
//import android.graphics.Typeface
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import android.widget.TextView
//import androidx.cardview.widget.CardView
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.ext.smart_calender_picker.R
//
//class CalendarAdapter(
//    private val onDayClick: (CalendarDay) -> Unit,
//    private val onDayLongClick: (CalendarDay) -> Unit
//) : ListAdapter<CalendarDay, CalendarAdapter.DayViewHolder>(DayDiffCallback()) {
//
//    // Function to update the list instantly
//    fun updateDays(newDays: List<CalendarDay>) {
//        submitList(newDays.toList())
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_calendar_day, parent, false)
//        return DayViewHolder(view, onDayClick, onDayLongClick)
//    }
//
//    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    class DayViewHolder(
//        itemView: View,
//        private val onDayClick: (CalendarDay) -> Unit,
//        private val onDayLongClick: (CalendarDay) -> Unit
//    ) : RecyclerView.ViewHolder(itemView) {
//
//        private val tvDay: TextView = itemView.findViewById(R.id.tvDay)
//        private val layoutEventsContainer: LinearLayout = itemView.findViewById(R.id.layoutEventDots)
//        private val cardDay: CardView = itemView.findViewById(R.id.cardDay)
//
//        fun bind(day: CalendarDay) {
//            tvDay.text = day.dayOfMonth
//
//            // ---------- RESET ----------
//            tvDay.setTextColor(Color.BLACK)
//            tvDay.setTypeface(null, Typeface.NORMAL)
//            cardDay.setCardBackgroundColor(Color.TRANSPARENT)
//            layoutEventsContainer.removeAllViews()
//            layoutEventsContainer.orientation = LinearLayout.VERTICAL
//            layoutEventsContainer.visibility = View.GONE
//
//            // ---------- HEADER ----------
//            if (day.isHeader) {
//                tvDay.setTextColor(Color.parseColor("#666666"))
//                tvDay.setTypeface(null, Typeface.BOLD)
//                itemView.setOnClickListener(null)
//                itemView.setOnLongClickListener(null)
//                return
//            }
//
//            // ---------- EMPTY / OUTSIDE MONTH ----------
//            if (day.date.isEmpty() || !day.isCurrentMonth) {
//                tvDay.setTextColor(Color.parseColor("#CCCCCC"))
//                itemView.setOnClickListener(null)
//                itemView.setOnLongClickListener(null)
//                return
//            }
//
//            // ---------- TODAY ----------
//            if (day.isToday) {
//                cardDay.setCardBackgroundColor(Color.parseColor("#E3F2FD"))
//                tvDay.setTypeface(null, Typeface.BOLD)
//            }
//
//            // ---------- SELECTION ----------
//            when {
//                day.isRangeStart || day.isRangeEnd -> {
//                    cardDay.setCardBackgroundColor(Color.parseColor("#2196F3"))
//                    tvDay.setTextColor(Color.WHITE)
//                    tvDay.setTypeface(null, Typeface.BOLD)
//                }
//                day.isSelected -> {
//                    cardDay.setCardBackgroundColor(Color.parseColor("#BBDEFB"))
//                    tvDay.setTextColor(Color.parseColor("#1976D2"))
//                }
//            }
//
//            // ---------- HOLIDAY ----------
//            day.holiday?.let { holidayName ->
//                tvDay.setTextColor(Color.parseColor("#D32F2F"))
//
//                val holidayLine = LinearLayout(itemView.context).apply {
//                    orientation = LinearLayout.HORIZONTAL
//                    gravity = Gravity.CENTER_VERTICAL
//                    layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    setPadding(0, 2, 0, 2)
//                }
//
//                // Dot for holiday
//                val dot = View(itemView.context).apply {
//                    val size = 12
//                    val params = LinearLayout.LayoutParams(size, size)
//                    params.setMargins(0, 0, 8, 0)
//                    layoutParams = params
//                    setBackgroundColor(Color.RED) // fixed red color
//                }
//
//                // Holiday name text
//                val tvHoliday = TextView(itemView.context).apply {
//                    text = holidayName // display only the name
//                    setTextColor(Color.RED)
//                    textSize = 10f // smaller text
//                }
//
//                holidayLine.addView(dot)
//                holidayLine.addView(tvHoliday)
//                layoutEventsContainer.addView(holidayLine)
//                layoutEventsContainer.visibility = View.VISIBLE
//            }
//
//            // ---------- CUSTOM EVENTS ----------
//            if (day.events.isNotEmpty()) {
//                layoutEventsContainer.visibility = View.VISIBLE
//
//                day.events.forEach { event ->
//                    val eventLine = LinearLayout(itemView.context).apply {
//                        orientation = LinearLayout.HORIZONTAL
//                        gravity = Gravity.CENTER_VERTICAL
//                        layoutParams = LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.WRAP_CONTENT
//                        )
//                        setPadding(0, 2, 0, 2)
//                    }
//
//                    val dot = View(itemView.context).apply {
//                        val size = 12
//                        val params = LinearLayout.LayoutParams(size, size)
//                        params.setMargins(0, 0, 8, 0)
//                        layoutParams = params
//                        try {
//                            // Only set color, never show hex string
//                            setBackgroundColor(Color.parseColor(event.color))
//                        } catch (_: Exception) {
//                            setBackgroundColor(Color.parseColor("#4CAF50"))
//                        }
//                    }
//
//                    val tvEvent = TextView(itemView.context).apply {
//                        text = event.title // only show event title
//                        setTextColor(Color.BLACK)
//                        textSize = 12f
//                    }
//
//                    eventLine.addView(dot)
//                    eventLine.addView(tvEvent)
//                    layoutEventsContainer.addView(eventLine)
//                }
//            }
//
//            // ---------- CLICK ----------
//            itemView.setOnClickListener { onDayClick(day) }
//
//            // ---------- LONG CLICK ----------
//            itemView.setOnLongClickListener {
//                onDayLongClick(day)
//                true
//            }
//        }
//    }
//
//    private class DayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
//        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay) =
//            oldItem.date == newItem.date
//
//        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay) =
//            oldItem == newItem
//    }
//}
