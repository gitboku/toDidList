package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.arch.persistence.room.Room
import android.util.Log
import com.example.kouhei.todidlist.R.string.diary_yet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

const val DATE_PATTERN_TO_DATABASE = "yyyyMMdd"

class EditDiaryActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // kotlinではgetIntent()は"intent"でOK
        val selectDate = this.getSelectDate(intent.getLongExtra(EXTRA_DATE, 0))
        val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "applyDatabase").build()

        // 選択してる日付の日記Entityを取得し、日記本文を表示する
        thread {
            // MainActivityで選択してる日付のEntityを取得
            val diary = db.diaryDao().getEntityFromDate(selectDate)

            // DiaryのEntityはnullである場合がある。
            if (diary != null){
                diaryPanel.setText(diary.diaryText)
            } else {
                diaryPanel.setText(diary_yet)
            }
        }
    }

    /**
     * 選択している日付をInt型で、DATE_PATTERN_TO_DATABASEのフォーマットで返す。
     * 何らかの理由で失敗したら0を返す
     */
    private fun getSelectDate(timestamp: Long): Int {
        val selectDateString = this.getDateTimeString(timestamp)
        try {
            // selectDateStringがnullならselectDateはnullになる
            val selectDate: Int? = selectDateString?.toInt()

            // Smart Cast to "Int" from "Int?"
            if (selectDate != null) {
                return selectDate
            }
        }catch (e: Exception) {
            Log.e("cast error", "String? cast to int error: " + e.toString())
        }

        return 0
    }

    /**
     * Long型で受け取ったtimestampをフォーマットDATE_PATTERN_TO_DATABASEの日付に変更する
     */
    private fun getDateTimeString(timestamp: Long): String? {
        try {
            val sdf = SimpleDateFormat(DATE_PATTERN_TO_DATABASE)
            val netDate = Date(timestamp)

            return sdf.format(netDate)
        } catch (e: Exception) {

            return e.toString()
        }
    }
}