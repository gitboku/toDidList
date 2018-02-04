package com.example.kouhei.todidlist

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentTabHost

class MainActivity :  FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var mTabHost = findViewById<FragmentTabHost>(R.id.tabhost)
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tabContent)

        mTabHost.addTab(
                mTabHost.newTabSpec("tab_calender").setIndicator(getString(R.string.tab_calender), null),
                CalenderFragment::class.java,
                null)
        mTabHost.addTab(
                mTabHost.newTabSpec("tab_stack").setIndicator(getString(R.string.tab_stack), null),
                CalenderFragment::class.java,
                null)
    }
}
