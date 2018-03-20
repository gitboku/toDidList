package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.arch.persistence.room.Room

class EditDiaryActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "applyDatabase").build()

        // kotlinではgetIntent()は"intent"でOK
        var selectedDate = intent.getLongExtra(EXTRA_DATE, 0)

        diaryPanel.setText(selectedDate.toString())
    }
}