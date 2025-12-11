package com.ext.smartcalendarpicker

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ext.smart_calender_picker.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var rvCalendar: RecyclerView
    private lateinit var tvMonthYear: TextView
    private lateinit var btnPrevMonth: Button
    private lateinit var btnNextMonth: Button
    private lateinit var btnSingleDate: Button
    private lateinit var btnMultiDate: Button
    private lateinit var btnDateRange: Button
    private lateinit var btnShowSelected: Button
    private lateinit var btnClearSelection: Button

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var holidayManager: HolidayManager

    private val calendar = Calendar.getInstance()
    private var selectionMode = SelectionMode.SINGLE
    private var selectedDates = mutableSetOf<String>()
    private var rangeStartDate: String? = null
    private var rangeEndDate: String? = null

    private val customEvents = mutableMapOf<String, MutableList<CalendarEvent>>()

    // Style instance for customization
    private var calendarStyle = CalendarStyle()

    enum class SelectionMode {
        SINGLE, MULTI, RANGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupHolidays()
        setupCustomEvents()
        setupCalendar()
        setupClickListeners()
        setupSwipeGesture()
        setupWindowInsetsAndStatusBar()
        updateCalendar()

    }

    private fun initViews() {
        rvCalendar = findViewById(R.id.rvCalendar)
        tvMonthYear = findViewById(R.id.tvMonthYear)
        btnPrevMonth = findViewById(R.id.btnPrevMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        btnSingleDate = findViewById(R.id.btnSingleDate)
        btnMultiDate = findViewById(R.id.btnMultiDate)
        btnDateRange = findViewById(R.id.btnDateRange)
        btnShowSelected = findViewById(R.id.btnShowSelected)
        btnClearSelection = findViewById(R.id.btnClearSelection)

        holidayManager = HolidayManager()
    }

    /** Apply a custom style instantly */
    fun applyCustomCalendarStyle(customStyle: CalendarStyle?) {
        if (customStyle != null) {
            calendarStyle = customStyle
        } else {
            calendarStyle = CalendarStyle() // default
        }

        calendarAdapter = CalendarAdapter(
            style = calendarStyle,
            onDayClick = ::handleDayClick,
            onDayLongClick = ::handleDayLongClick
        )
        rvCalendar.adapter = calendarAdapter
        updateCalendar() // refresh calendar to apply new style
    }

    private fun setupHolidays() {
        holidayManager.addHoliday("2025-01-01", "New Year's Day")
        holidayManager.addHoliday("2025-01-26", "Republic Day")
        holidayManager.addHoliday("2025-03-14", "Holi")
        holidayManager.addHoliday("2025-08-15", "Independence Day")
        holidayManager.addHoliday("2025-10-02", "Gandhi Jayanti")
        holidayManager.addHoliday("2025-12-25", "Christmas")
    }

    private fun setupCustomEvents() {
        customEvents["2025-01-01"] = mutableListOf(CalendarEvent("New Year Celebration", "#FF9800"))
        customEvents["2025-01-15"] = mutableListOf(CalendarEvent("Project Deadline", "#F44336"))
        customEvents["2025-02-14"] = mutableListOf(CalendarEvent("Valentine's Day", "#E91E63"))
    }

    private fun setupWindowInsetsAndStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.parseColor("#FFFFFF")
        }
        val decorView = window.decorView
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
        WindowInsetsControllerCompat(window, decorView).isAppearanceLightStatusBars = true
    }

    private fun setupCalendar() {
        rvCalendar.layoutManager = GridLayoutManager(this, 7)
        calendarAdapter = CalendarAdapter(calendarStyle, ::handleDayClick, ::handleDayLongClick)
        rvCalendar.adapter = calendarAdapter
    }

    private fun handleDayLongClick(day: CalendarDay) {
        if (!day.isCurrentMonth || day.date.isEmpty()) return
        val events = customEvents[day.date] ?: mutableListOf()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 30, 40, 30)
            setBackgroundColor(Color.parseColor(calendarStyle.dialogBackgroundColor))
        }

        val tvTitle = TextView(this).apply {
            text = "Events on ${formatDate(day.date)}"
            textSize = calendarStyle.dialogTitleSize
            setTextColor(Color.parseColor(calendarStyle.dialogTitleColor))
            calendarStyle.dialogTitleFont?.let { typeface = it }
        }
        layout.addView(tvTitle)

        val input = EditText(this).apply { hint = "Add new event" }
        layout.addView(input)

        fun refreshEventList() {
            while (layout.childCount > 2) layout.removeViewAt(1)

            events.forEachIndexed { index, event ->
                val eventLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 10, 0, 10)
                }

                val tvEvent = TextView(this).apply {
                    text = event.title
                    textSize = calendarStyle.eventTextSize
                    setTextColor(Color.parseColor(calendarStyle.eventTextColor))
                    calendarStyle.eventFont?.let { typeface = it }
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val btnEdit = Button(this).apply {
                    text = "Edit"
                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(Color.BLUE)
                }

                val btnDelete = Button(this).apply {
                    text = "Delete"
                    setBackgroundColor(Color.TRANSPARENT)
                    setTextColor(Color.RED)
                }

                btnEdit.setOnClickListener {
                    val editInput = EditText(this).apply { setText(event.title) }
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Edit Event")
                        .setView(editInput)
                        .setPositiveButton("Update") { _, _ ->
                            val newTitle = editInput.text.toString().trim()
                            if (newTitle.isNotEmpty()) {
                                events[index] = event.copy(title = newTitle)
                                customEvents[day.date] = events
                                updateCalendar()
                                refreshEventList()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                btnDelete.setOnClickListener {
                    events.removeAt(index)
                    if (events.isEmpty()) customEvents.remove(day.date)
                    else customEvents[day.date] = events
                    updateCalendar()
                    refreshEventList()
                }

                eventLayout.addView(tvEvent)
                eventLayout.addView(btnEdit)
                eventLayout.addView(btnDelete)
                layout.addView(eventLayout, layout.childCount - 1)
            }
        }

        val builder = AlertDialog.Builder(this)
            .setView(layout)
            .setPositiveButton("Add") { _, _ ->
                val title = input.text.toString().trim()
                if (title.isNotEmpty()) {
                    val updatedEvents = customEvents[day.date] ?: mutableListOf()
                    updatedEvents.add(CalendarEvent(title = title, color = "#FF5722"))
                    customEvents[day.date] = updatedEvents
                    updateCalendar()
                    handleDayLongClick(day)
                }
            }
            .setNegativeButton("Close", null)

        val dialog = builder.create()
        dialog.show()
        refreshEventList()
    }

    private fun setupClickListeners() {
        btnPrevMonth.setOnClickListener { calendar.add(Calendar.MONTH, -1); updateCalendar() }
        btnNextMonth.setOnClickListener { calendar.add(Calendar.MONTH, 1); updateCalendar() }

        btnSingleDate.setOnClickListener {
            selectionMode = SelectionMode.SINGLE
            clearAllSelections()
            updateModeButtons()
//            Toast.makeText(this, "Single Date Mode", Toast.LENGTH_SHORT).show()
        }

        btnMultiDate.setOnClickListener {
            selectionMode = SelectionMode.MULTI
            clearAllSelections()
            updateModeButtons()
//            Toast.makeText(this, "Multi Date Mode", Toast.LENGTH_SHORT).show()
        }

        btnDateRange.setOnClickListener {
            selectionMode = SelectionMode.RANGE
            clearAllSelections()
            updateModeButtons()
//            Toast.makeText(this, "Date Range Mode", Toast.LENGTH_SHORT).show()
        }

        btnShowSelected.setOnClickListener { showSelectedDates() }
        btnClearSelection.setOnClickListener {
            clearAllSelections()
            updateCalendar()
//            Toast.makeText(this, "Selection cleared", Toast.LENGTH_SHORT).show()
        }

        updateModeButtons()
    }

    private fun setupSwipeGesture() {
        gestureDetector = GestureDetectorCompat(this, object : android.view.GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY)) {
                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) calendar.add(Calendar.MONTH, -1)
                        else calendar.add(Calendar.MONTH, 1)
                        updateCalendar()
                        return true
                    }
                } else {
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) calendar.add(Calendar.YEAR, -1)
                        else calendar.add(Calendar.YEAR, 1)
                        updateCalendar()
                        return true
                    }
                }
                return false
            }
        })

        rvCalendar.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event); false }
    }

    private fun updateCalendar() {
        val days = generateCalendarDays()
        calendarAdapter.submitList(days)
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = monthFormat.format(calendar.time)
    }

    private fun generateCalendarDays(): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        dayHeaders.forEach { days.add(CalendarDay(date = "", dayOfMonth = it, isHeader = true)) }

        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        repeat(firstDayOfWeek) { days.add(CalendarDay(date = "", dayOfMonth = "", isCurrentMonth = false)) }

        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (day in 1..daysInMonth) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, day)
            val dateString = dateFormat.format(tempCalendar.time)

            val isToday = tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    tempCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)

            val isSelected = when (selectionMode) {
                SelectionMode.SINGLE, SelectionMode.MULTI -> selectedDates.contains(dateString)
                SelectionMode.RANGE -> isDateInRange(dateString)
            }

            val isRangeStart = dateString == rangeStartDate
            val isRangeEnd = dateString == rangeEndDate
            val holiday = holidayManager.getHoliday(dateString)
            val dayEvents = customEvents[dateString] ?: mutableListOf()

            days.add(CalendarDay(
                date = dateString,
                dayOfMonth = day.toString(),
                isCurrentMonth = true,
                isToday = isToday,
                isSelected = isSelected,
                isRangeStart = isRangeStart,
                isRangeEnd = isRangeEnd,
                holiday = holiday,
                events = dayEvents
            ))
        }
        return days
    }

    private fun handleDayClick(day: CalendarDay) {
        if (!day.isCurrentMonth || day.date.isEmpty()) return
        when (selectionMode) {
            SelectionMode.SINGLE -> { selectedDates.clear(); selectedDates.add(day.date) }
            SelectionMode.MULTI -> { if (!selectedDates.remove(day.date)) selectedDates.add(day.date) }
            SelectionMode.RANGE -> handleRangeSelection(day.date)
        }
        updateCalendar()
    }

    private fun handleRangeSelection(date: String) {
        when {
            rangeStartDate == null -> { rangeStartDate = date; rangeEndDate = null }
            rangeEndDate == null -> {
                val startDate = parseDate(rangeStartDate!!)
                val endDate = parseDate(date)
                if (endDate.before(startDate)) { rangeStartDate = date; rangeEndDate = null }
                else rangeEndDate = date
            }
            else -> { rangeStartDate = date; rangeEndDate = null }
        }
    }

    private fun isDateInRange(date: String): Boolean {
        if (rangeStartDate == null) return false
        if (rangeEndDate == null) return date == rangeStartDate
        val current = parseDate(date)
        val start = parseDate(rangeStartDate!!)
        val end = parseDate(rangeEndDate!!)
        return !current.before(start) && !current.after(end)
    }

    private fun parseDate(dateString: String): Date {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.parse(dateString) ?: Date()
    }

    private fun showSelectedDates() {
        val message = when (selectionMode) {
            SelectionMode.SINGLE -> if (selectedDates.isEmpty()) "No date selected" else "Selected: ${formatDate(selectedDates.first())}"
            SelectionMode.MULTI -> if (selectedDates.isEmpty()) "No dates selected" else "Selected ${selectedDates.size} dates:\n" +
                    selectedDates.sorted().joinToString("\n") { formatDate(it) }
            SelectionMode.RANGE -> when {
                rangeStartDate == null -> "No range selected"
                rangeEndDate == null -> "Start: ${formatDate(rangeStartDate!!)}\nSelect end date"
                else -> "Range: ${formatDate(rangeStartDate!!)} to ${formatDate(rangeEndDate!!)}"
            }
        }
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date ?: Date())
    }

    private fun clearAllSelections() {
        selectedDates.clear()
        rangeStartDate = null
        rangeEndDate = null
    }

    private fun updateModeButtons() {
        btnSingleDate.isSelected = selectionMode == SelectionMode.SINGLE
        btnMultiDate.isSelected = selectionMode == SelectionMode.MULTI
        btnDateRange.isSelected = selectionMode == SelectionMode.RANGE
    }
}





//package com.ext.smartcalendarpicker
//
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import android.view.MotionEvent
//import android.widget.Button
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GestureDetectorCompat
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.WindowInsetsControllerCompat
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.ext.smart_calender_picker.R
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.math.abs
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var rvCalendar: RecyclerView
//    private lateinit var tvMonthYear: TextView
//    private lateinit var btnPrevMonth: Button
//    private lateinit var btnNextMonth: Button
//    private lateinit var btnSingleDate: Button
//    private lateinit var btnMultiDate: Button
//    private lateinit var btnDateRange: Button
//    private lateinit var btnShowSelected: Button
//    private lateinit var btnClearSelection: Button
//
//    private lateinit var calendarAdapter: CalendarAdapter
//    private lateinit var gestureDetector: GestureDetectorCompat
//    private lateinit var holidayManager: HolidayManager
//
//    private val calendar = Calendar.getInstance()
//    private var selectionMode = SelectionMode.SINGLE
//    private var selectedDates = mutableSetOf<String>()
//    private var rangeStartDate: String? = null
//    private var rangeEndDate: String? = null
//
//    private val customEvents = mutableMapOf<String, MutableList<CalendarEvent>>() // <-- support multiple events
//
//    enum class SelectionMode {
//        SINGLE, MULTI, RANGE
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        initViews()
//        setupHolidays()
//        setupCustomEvents()
//        setupCalendar()
//        setupClickListeners()
//        setupSwipeGesture()
//        setupWindowInsetsAndStatusBar()
//
//        updateCalendar()
//    }
//
//    private fun initViews() {
//        rvCalendar = findViewById(R.id.rvCalendar)
//        tvMonthYear = findViewById(R.id.tvMonthYear)
//        btnPrevMonth = findViewById(R.id.btnPrevMonth)
//        btnNextMonth = findViewById(R.id.btnNextMonth)
//        btnSingleDate = findViewById(R.id.btnSingleDate)
//        btnMultiDate = findViewById(R.id.btnMultiDate)
//        btnDateRange = findViewById(R.id.btnDateRange)
//        btnShowSelected = findViewById(R.id.btnShowSelected)
//        btnClearSelection = findViewById(R.id.btnClearSelection)
//
//        holidayManager = HolidayManager()
//    }
//
//    private fun setupHolidays() {
//        // Sample holidays
//        holidayManager.addHoliday("2025-01-01", "New Year's Day")
//        holidayManager.addHoliday("2025-01-26", "Republic Day")
//        holidayManager.addHoliday("2025-03-14", "Holi")
//        holidayManager.addHoliday("2025-08-15", "Independence Day")
//        holidayManager.addHoliday("2025-10-02", "Gandhi Jayanti")
//        holidayManager.addHoliday("2025-12-25", "Christmas")
//    }
//
//    private fun setupCustomEvents() {
//        // Sample events
//        customEvents["2025-01-01"] = mutableListOf(CalendarEvent("New Year Celebration", "#FF9800"))
//        customEvents["2025-01-15"] = mutableListOf(CalendarEvent("Project Deadline", "#F44336"))
//        customEvents["2025-02-14"] = mutableListOf(CalendarEvent("Valentine's Day", "#E91E63"))
//    }
//
//    private fun setupWindowInsetsAndStatusBar() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.statusBarColor = Color.parseColor("#FFFFFF")
//        }
//
//        val decorView = window.decorView
//        ViewCompat.setOnApplyWindowInsetsListener(decorView) { view, insets ->
//            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
//            view.setPadding(0, statusBarInsets.top, 0, 0)
//            insets
//        }
//
//        WindowInsetsControllerCompat(window, decorView).isAppearanceLightStatusBars = true
//    }
//
//    private fun setupCalendar() {
//        rvCalendar.layoutManager = GridLayoutManager(this, 7)
//        calendarAdapter = CalendarAdapter(
//            onDayClick = { day -> handleDayClick(day) },
//            onDayLongClick = { day -> handleDayLongClick(day) } // add event
//        )
//        rvCalendar.adapter = calendarAdapter
//    }
//
//    private fun handleDayLongClick(day: CalendarDay) {
//        if (!day.isCurrentMonth || day.date.isEmpty()) return
//
//        // Get existing events for that day
//        val events = customEvents[day.date] ?: mutableListOf()
//
//        // Create vertical layout for dialog content
//        val layout = LinearLayout(this).apply {
//            orientation = LinearLayout.VERTICAL
//            setPadding(40, 30, 40, 30)
//        }
//
//        // Title above events
//        val tvTitle = TextView(this).apply {
//            text = "Events on ${formatDate(day.date)}"
//            textSize = 18f
//            setTextColor(Color.BLACK)
//        }
//        layout.addView(tvTitle)
//
//        // Input field to add new event
//        val input = android.widget.EditText(this).apply {
//            hint = "Add new event"
//        }
//        layout.addView(input)
//
//        // Function to refresh events in dialog
//        fun refreshEventList() {
//            // Remove all event views (keep title and input)
//            while (layout.childCount > 2) {
//                layout.removeViewAt(1)
//            }
//
//            events.forEachIndexed { index, event ->
//                val eventLayout = LinearLayout(this).apply {
//                    orientation = LinearLayout.HORIZONTAL
//                    setPadding(0, 10, 0, 10)
//                }
//
//                val tvEvent = TextView(this).apply {
//                    text = event.title
//                    textSize = 16f
//                    setTextColor(Color.BLACK)
//                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
//                }
//
//                val btnEdit = Button(this).apply {
//                    text = "Edit"
//                    setBackgroundColor(Color.TRANSPARENT)
//                    setTextColor(Color.BLUE)
//                }
//
//                val btnDelete = Button(this).apply {
//                    text = "Delete"
//                    setBackgroundColor(Color.TRANSPARENT)
//                    setTextColor(Color.RED)
//                }
//
//                // Edit event
//                btnEdit.setOnClickListener {
//                    val editInput = android.widget.EditText(this).apply { setText(event.title) }
//                    AlertDialog.Builder(this@MainActivity)
//                        .setTitle("Edit Event")
//                        .setView(editInput)
//                        .setPositiveButton("Update") { _, _ ->
//                            val newTitle = editInput.text.toString().trim()
//                            if (newTitle.isNotEmpty()) {
//                                events[index] = event.copy(title = newTitle)
//                                customEvents[day.date] = events
//                                updateCalendar()      // Refresh main calendar
//                                refreshEventList()    // Refresh dialog list
//                            }
//                        }
//                        .setNegativeButton("Cancel", null)
//                        .show()
//                }
//
//                // Delete event
//                btnDelete.setOnClickListener {
//                    events.removeAt(index)
//                    if (events.isEmpty()) customEvents.remove(day.date)
//                    else customEvents[day.date] = events
//                    updateCalendar()          // Refresh main calendar
//                    refreshEventList()        // Refresh dialog list
//                }
//
//                eventLayout.addView(tvEvent)
//                eventLayout.addView(btnEdit)
//                eventLayout.addView(btnDelete)
//                layout.addView(eventLayout, layout.childCount - 1) // before input field
//            }
//        }
//
//        val builder = AlertDialog.Builder(this)
//            .setView(layout)
//            .setPositiveButton("Add") { _, _ ->
//                val title = input.text.toString().trim()
//                if (title.isNotEmpty()) {
//                    val updatedEvents = customEvents[day.date] ?: mutableListOf()
//                    updatedEvents.add(CalendarEvent(title = title, color = "#FF5722"))
//                    customEvents[day.date] = updatedEvents
//                    updateCalendar()      // Refresh main calendar
//                    handleDayLongClick(day) // Reopen dialog to show new event
//                }
//            }
//            .setNegativeButton("Close", null)
//
//        val dialog = builder.create()
//        dialog.show()
//
//        // Initially populate events
//        refreshEventList()
//    }
//
//    private fun setupClickListeners() {
//        btnPrevMonth.setOnClickListener { calendar.add(Calendar.MONTH, -1); updateCalendar() }
//        btnNextMonth.setOnClickListener { calendar.add(Calendar.MONTH, 1); updateCalendar() }
//
//        btnSingleDate.setOnClickListener {
//            selectionMode = SelectionMode.SINGLE
//            clearAllSelections()
//            updateModeButtons()
//            Toast.makeText(this, "Single Date Mode", Toast.LENGTH_SHORT).show()
//        }
//
//        btnMultiDate.setOnClickListener {
//            selectionMode = SelectionMode.MULTI
//            clearAllSelections()
//            updateModeButtons()
//            Toast.makeText(this, "Multi Date Mode", Toast.LENGTH_SHORT).show()
//        }
//
//        btnDateRange.setOnClickListener {
//            selectionMode = SelectionMode.RANGE
//            clearAllSelections()
//            updateModeButtons()
//            Toast.makeText(this, "Date Range Mode", Toast.LENGTH_SHORT).show()
//        }
//
//        btnShowSelected.setOnClickListener { showSelectedDates() }
//        btnClearSelection.setOnClickListener {
//            clearAllSelections()
//            updateCalendar()
//            Toast.makeText(this, "Selection cleared", Toast.LENGTH_SHORT).show()
//        }
//
//        updateModeButtons()
//    }
//
//    private fun setupSwipeGesture() {
//        gestureDetector = GestureDetectorCompat(this, object : android.view.GestureDetector.SimpleOnGestureListener() {
//            private val SWIPE_THRESHOLD = 100
//            private val SWIPE_VELOCITY_THRESHOLD = 100
//
//            override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
//                if (e1 == null) return false
//                val diffX = e2.x - e1.x
//                val diffY = e2.y - e1.y
//
//                if (abs(diffX) > abs(diffY)) {
//                    if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
//                        if (diffX > 0) calendar.add(Calendar.MONTH, -1)
//                        else calendar.add(Calendar.MONTH, 1)
//                        updateCalendar()
//                        return true
//                    }
//                } else {
//                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//                        if (diffY > 0) calendar.add(Calendar.YEAR, -1)
//                        else calendar.add(Calendar.YEAR, 1)
//                        updateCalendar()
//                        return true
//                    }
//                }
//                return false
//            }
//        })
//
//        rvCalendar.setOnTouchListener { _, event ->
//            gestureDetector.onTouchEvent(event)
//            false
//        }
//    }
//
//    private fun updateCalendar() {
//        val days = generateCalendarDays()
//        calendarAdapter.submitList(days)
//
//        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
//        tvMonthYear.text = monthFormat.format(calendar.time)
//    }
//
//    private fun generateCalendarDays(): List<CalendarDay> {
//        val days = mutableListOf<CalendarDay>()
//
//        // Day headers
//        val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
//        dayHeaders.forEach { header ->
//            days.add(CalendarDay(date = "", dayOfMonth = header, isHeader = true))
//        }
//
//        val tempCalendar = calendar.clone() as Calendar
//        tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
//
//        val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1
//        val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
//
//        // Empty cells before first day
//        repeat(firstDayOfWeek) { days.add(CalendarDay(date = "", dayOfMonth = "", isCurrentMonth = false)) }
//
//        val today = Calendar.getInstance()
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//
//        for (day in 1..daysInMonth) {
//            tempCalendar.set(Calendar.DAY_OF_MONTH, day)
//            val dateString = dateFormat.format(tempCalendar.time)
//
//            val isToday = tempCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
//                    tempCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
//                    tempCalendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
//
//            val isSelected = when (selectionMode) {
//                SelectionMode.SINGLE, SelectionMode.MULTI -> selectedDates.contains(dateString)
//                SelectionMode.RANGE -> isDateInRange(dateString)
//            }
//
//            val isRangeStart = dateString == rangeStartDate
//            val isRangeEnd = dateString == rangeEndDate
//            val holiday = holidayManager.getHoliday(dateString)
//            val dayEvents = customEvents[dateString] ?: mutableListOf()
//
//            days.add(CalendarDay(
//                date = dateString,
//                dayOfMonth = day.toString(),
//                isCurrentMonth = true,
//                isToday = isToday,
//                isSelected = isSelected,
//                isRangeStart = isRangeStart,
//                isRangeEnd = isRangeEnd,
//                holiday = holiday,
//                events = dayEvents
//            ))
//        }
//
//        return days
//    }
//
//    private fun handleDayClick(day: CalendarDay) {
//        if (!day.isCurrentMonth || day.date.isEmpty()) return
//
//        when (selectionMode) {
//            SelectionMode.SINGLE -> { selectedDates.clear(); selectedDates.add(day.date) }
//            SelectionMode.MULTI -> {
//                if (selectedDates.contains(day.date)) selectedDates.remove(day.date)
//                else selectedDates.add(day.date)
//            }
//            SelectionMode.RANGE -> handleRangeSelection(day.date)
//        }
//
//        updateCalendar()
//    }
//
//    private fun handleRangeSelection(date: String) {
//        when {
//            rangeStartDate == null -> { rangeStartDate = date; rangeEndDate = null }
//            rangeEndDate == null -> {
//                val startDate = parseDate(rangeStartDate!!)
//                val endDate = parseDate(date)
//                if (endDate.before(startDate)) { rangeStartDate = date; rangeEndDate = null }
//                else rangeEndDate = date
//            }
//            else -> { rangeStartDate = date; rangeEndDate = null }
//        }
//    }
//
//    private fun isDateInRange(date: String): Boolean {
//        if (rangeStartDate == null) return false
//        if (rangeEndDate == null) return date == rangeStartDate
//        val current = parseDate(date)
//        val start = parseDate(rangeStartDate!!)
//        val end = parseDate(rangeEndDate!!)
//        return !current.before(start) && !current.after(end)
//    }
//
//    private fun parseDate(dateString: String): Date {
//        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        return format.parse(dateString) ?: Date()
//    }
//
//    private fun showSelectedDates() {
//        val message = when (selectionMode) {
//            SelectionMode.SINGLE -> if (selectedDates.isEmpty()) "No date selected" else "Selected: ${formatDate(selectedDates.first())}"
//            SelectionMode.MULTI -> if (selectedDates.isEmpty()) "No dates selected" else "Selected ${selectedDates.size} dates:\n" +
//                    selectedDates.sorted().joinToString("\n") { formatDate(it) }
//            SelectionMode.RANGE -> when {
//                rangeStartDate == null -> "No range selected"
//                rangeEndDate == null -> "Start: ${formatDate(rangeStartDate!!)}\nSelect end date"
//                else -> "Range: ${formatDate(rangeStartDate!!)} to ${formatDate(rangeEndDate!!)}"
//            }
//        }
//
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
//    }
//
//    private fun formatDate(dateString: String): String {
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//        val date = inputFormat.parse(dateString)
//        return outputFormat.format(date ?: Date())
//    }
//
//    private fun clearAllSelections() {
//        selectedDates.clear()
//        rangeStartDate = null
//        rangeEndDate = null
//    }
//
//    private fun updateModeButtons() {
//        btnSingleDate.isSelected = selectionMode == SelectionMode.SINGLE
//        btnMultiDate.isSelected = selectionMode == SelectionMode.MULTI
//        btnDateRange.isSelected = selectionMode == SelectionMode.RANGE
//    }
//}
