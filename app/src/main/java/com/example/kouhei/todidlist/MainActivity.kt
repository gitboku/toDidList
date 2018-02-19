package com.example.kouhei.todidlist

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CalendarView
import android.widget.FrameLayout

class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var tabLayout = findViewById<TabLayout>(R.id.tabs)
//        var frameLayout = findViewById<FrameLayout>(R.id.tabContent)

//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_calender))
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_stack))

        var calender = findViewById<CalendarView>(R.id.calender)

        // setOnTabSelectedListener was deprecated in API level 24.1.0.
        // Use addOnTabSelectedListener(OnTabSelectedListener) and removeOnTabSelectedListener(OnTabSelectedListener).
        // TabLayout.OnTabSelectedListener ← タブの選択状態が変更されたときに呼び出されるcallback interface
        // "object:" ← 無名クラスを作るときに使う
//        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
//            // "onTabSelected","onTabUnselected","onTabReselected"の3つを実装しないとエラー
//
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                Log.i("myTag", "onTabSelebted")
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                Log.i("myTag", "onTabUnselected")
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                Log.i("myTag", "onTabReselected")
//            }
//        })
    }
}
