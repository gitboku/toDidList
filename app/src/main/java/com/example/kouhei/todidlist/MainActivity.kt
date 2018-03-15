package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import AppDatabase
import android.arch.persistence.room.Room

const val EXTRA_DATE = "com.example.todidList.SELECTED_DATE"

class MainActivity :  MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // CalendarView.OnDateChangeListener has only abstract onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        // よって、SAM変換によりonSelectedDayChangeを省略できる
        // The month that was set [0-11].
        calendar.setOnDateChangeListener { calendar, year, month, dayOfMonth ->
            Log.d("myTag", "now date is $year/$month/$dayOfMonth")
            textView.text = "now date is $year/$month/$dayOfMonth"
        }

        textView.setOnClickListener {
            moveToEditPage()
        }
    }

    // 日付がタップされたときにEdit（または閲覧）画面に遷移するメソッド
    // xmlのonClick属性に対応させるには、引数にViewパラメータを含む必要があるが、今回はTextViewのListenerを使うのでいらない。
    fun moveToEditPage(){
        // 一つ目のコンストラクタはContext。ActivityはContextのサブクラスなのでthisを使う
        // 二つ目はIntentが送られるアプリコンポーネントのClass（開始されるActivity）
        var intent = Intent(this, EditDiaryActivity::class.java)

        // カレンダー部分で選択してる日付を渡す
        intent.putExtra(EXTRA_DATE, calendar.date.toString())

        startActivity(intent)
    }
}
