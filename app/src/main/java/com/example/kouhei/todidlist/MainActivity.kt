package com.example.kouhei.todidlist

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout

class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tabLayout = findViewById<TabLayout>(R.id.tabs)
        var frameLayout = findViewById<FrameLayout>(R.id.tabContent)

        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_calender))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_stack))

//        tabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener() {
//            fun onTabSelected(tab:TabLayout.Tab) {
//                val fragment: Fragment = null
//
//                when (tab.position) {
//                    0 -> fragment = CalenderFragment()
//                    1 -> fragment = CalenderFragment()
//                }
//                val fm = getSupportFragmentManager()
//                val ft = fm.beginTransaction()
//                ft.replace(R.id.tabContent, fragment)
//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                ft.commit()
//            }
//        })
    }
}
