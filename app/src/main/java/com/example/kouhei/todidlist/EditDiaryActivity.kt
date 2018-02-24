package com.example.kouhei.todidlist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*

class EditDiaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // kotlinではgetIntent()はいらない
        var selectedDate = intent.getStringExtra(EXTRA_DATE)

        diaryText.setText("hello EditDiaryActivity !")
    }
}
