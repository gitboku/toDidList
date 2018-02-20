package com.example.kouhei.todidlist

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CalendarView
import android.widget.FrameLayout
import com.tyczj.extendedcalendarview.ExtendedCalendarView

class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var calender = findViewById<ExtendedCalendarView>(R.id.calendar)


    }
}
