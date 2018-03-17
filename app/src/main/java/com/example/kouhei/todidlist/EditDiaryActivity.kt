package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*

class EditDiaryActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // kotlinではgetIntent()は"intent"でOK
        var selectedDate = intent.getLongExtra(EXTRA_DATE, 0)

        diaryPanel.setText(selectedDate.toString())
    }
}