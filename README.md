# Smart Calendar Picker

A simple and **customizable Android calendar library** with support for **single, multiple, and range date selection**, holidays, events, and custom styles.

Users can select dates, view holidays, add events, and apply **custom styles** such as text color, background color, font, and dot size.

---

## ✨ Features

- Single, Multi, and Range **date selection modes**  
- **Highlight today**, holidays, and selected dates  
- **Custom events** with colored dots  
- **Fully customizable UI**: fonts, text sizes, colors, background  
- **Swipe gestures** for month and year navigation  
- Easy to **reset selections** and show selected dates  

---

## ⚡ Preview
---
<p align="center">
  <img src="https://github.com/user-attachments/assets/1af99684-c7ef-48e2-a8cb-4ca83e2092f5"
       alt="Demo GIF"
       width="200">


</p>



## ⚡ Dependency

```
    implementation("androidx.recyclerview:recyclerview:1.3.2")

```

## ⚡ Installation

```
Step 1: Add JitPack repository to your root build.gradle:

allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

```kotlin
Step 2: Add dependency to your app module's build.gradle:

dependencies {
	        implementation("com.github.Excelsior-Technologies-Community:SpeechToTextEditor:1.0.1")
}
```

---

## 📄 Usage

### 1. Add RecyclerView to your layout

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/rvCalendar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```

### 2. Initialize the Calendar in MainActivity.kt
```
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val rvCalendar = findViewById<RecyclerView>(R.id.rvCalendar)
    rvCalendar.layoutManager = GridLayoutManager(this, 7)

    // Create default style
    val defaultStyle = CalendarStyle()

    // Initialize adapter
    calendarAdapter = CalendarAdapter(
        style = defaultStyle,
        onDayClick = { day -> handleDayClick(day) },
        onDayLongClick = { day -> handleDayLongClick(day) }
    )
    rvCalendar.adapter = calendarAdapter

    updateCalendar()
}


```

 ##   3.Apply custom style

 You can define a custom CalendarStyle and apply it instantly:
  ```

    val myStyle = CalendarStyle(
    dayTextColor = "#FF5722",
    selectedBackground = "#3F51B5",
    rangeBackground = "#2196F3",
    todayBackground = "#FFEB3B",
    holidayTextColor = "#D50000",
    dayTextSize = 16f,
    dotSize = 16
)

// Apply to adapter
calendarAdapter = CalendarAdapter(
    style = myStyle,
    onDayClick = { day -> handleDayClick(day) },
    onDayLongClick = { day -> handleDayLongClick(day) }
)
rvCalendar.adapter = calendarAdapter
updateCalendar()


```

### 4. Add custom events
```
val customEvents = mutableMapOf<String, MutableList<CalendarEvent>>()
customEvents["2025-01-01"] = mutableListOf(CalendarEvent("New Year Celebration", "#FF9800"))
customEvents["2025-02-14"] = mutableListOf(CalendarEvent("Valentine's Day", "#E91E63"))

```

### 🖌 Customization Options

```
The CalendarStyle class allows you to customize every UI element:

Day text: dayTextColor, dayTextSize, dayFont

Header: headerTextColor, headerTextSize, headerFont

Today: todayBackground, todayTextColor, todayFont

Selected day: selectedBackground, selectedTextColor, selectedFont

Range: rangeBackground, rangeTextColor, rangeFont

Holidays: holidayTextColor, holidayDotColor, holidayFont

Events: eventTextColor, eventTextSize, eventDefaultDotColor, eventFont, dotSize

Month header: monthTitleColor, monthTitleSize, monthTitleFont, monthBackgroundColor

Navigation arrows: arrowColor, arrowSize

Buttons: clear/apply/mode button colors, fonts, corner radius

Dialog box: dialogBackgroundColor, dialogTitleColor, dialogTitleSize, dialogButtonColor, etc.

Overall background: calendarBackgroundColor

```

### Example:

```
val customStyle = CalendarStyle(
    dayTextColor = "#000000",
    todayBackground = "#FFEB3B",
    selectedBackground = "#2196F3",
    holidayTextColor = "#D32F2F",
    dotSize = 14
)

```

### 📄 License

MIT License
 ```
Copyright (c) 2025 Excelsior Technologies

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
```

