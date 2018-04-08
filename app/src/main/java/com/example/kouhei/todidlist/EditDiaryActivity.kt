package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.kouhei.todidlist.R.string.diary_yet
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

const val DATE_PATTERN_TO_DATABASE = "yyyyMMdd"

class EditDiaryActivity : AppCompatActivity() {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "applyDatabase").build()

    private var selectDate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(edit_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // kotlinではgetIntent()は"intent"でOK
        selectDate = this.getSelectDate(intent.getLongExtra(EXTRA_DATE, 0))

        // 選択してる日付の日記Entityを取得し、日記本文を表示する
        thread {
            val diary = db.diaryDao().getEntityWithDate(selectDate)

            // DiaryのEntityはnullである場合がある。
            if (diary != null){
                diaryPanel.setText(diary.diaryText)
            } else {
                // TODO when click datePanel in MainActivity, sometimes error here.
                diaryPanel.setText(diary_yet)
            }
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            saveDiary()
            moveToMainPage()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * 日記をsaveする
     * UpdateかInsertかはDiaryのEntityがnullかどうかで判断
     */
    fun saveDiary() {
        val diaryDao = db.diaryDao()
        thread {
            val diary = diaryDao.getEntityWithDate(selectDate)

            if (diary != null){
                diaryDao.updateDiaryWithDate(diaryPanel.text.toString(), selectDate)
            } else {
                val diary = Diary()
                diary.diaryText = diaryPanel.text.toString()
                diary.calendarDate = selectDate
                diaryDao.insert(diary)
            }
        }
    }

    /**
     * MainActivityに遷移するメソッド
     */
    fun moveToMainPage(){
        // 一つ目のコンストラクタはContext。ActivityはContextのサブクラスなのでthisを使う
        // 二つ目はIntentが送られるアプリコンポーネントのClass（開始されるActivity）
        val intent = Intent(this, MainActivity::class.java)

        // カレンダー部分で選択してる日付をInt型で、DATE_PATTERN_TO_DATABASEのフォーマットで渡す
        intent.putExtra(EXTRA_DATE, intent.getLongExtra(EXTRA_DATE, 0))

        startActivity(intent)
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
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