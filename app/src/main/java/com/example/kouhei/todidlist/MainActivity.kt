package com.example.kouhei.todidlist

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentActivity
import android.util.AttributeSet
import android.view.View

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_calender))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_stack))
    }
}
