package com.example.kouhei.todidlist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(about_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
