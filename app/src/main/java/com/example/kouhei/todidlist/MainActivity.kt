package com.example.kouhei.todidlist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CalendarView

class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var myCalendar = findViewById<CalendarView>(R.id.calendar)
    }
}
