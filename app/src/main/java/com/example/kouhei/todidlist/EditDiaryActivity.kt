package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.arch.persistence.room.Room
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN_TO_DATABASE = "yyyyMMdd"

class EditDiaryActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "applyDatabase").build()

        // kotlinではgetIntent()は"intent"でOK
        var selectedDate = intent.getLongExtra(EXTRA_DATE, 0)

        diaryPanel.setText(getDateTime(selectedDate))
    }

    /**
     * Long型で受け取ったタイムスタンプを日付に変更する
     */
    private fun getDateTime(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat(DATE_PATTERN_TO_DATABASE)
            val netDate = Date(timestamp)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}