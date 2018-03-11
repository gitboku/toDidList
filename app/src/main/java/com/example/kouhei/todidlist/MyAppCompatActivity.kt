package com.example.kouhei.todidlist

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by kouhei on 3/6/2018.
 */

// openをつけないとfinal扱いとなる
open class MyAppCompatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }
}