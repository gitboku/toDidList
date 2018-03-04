package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.CalendarView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import MySQLiteOpenHelper

const val EXTRA_DATE = "com.example.kouhei.todidList.SELECTED_DATE"

class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // データベースのインスタンスを作る
        var helper = MySQLiteOpenHelper(this)
        var db = helper.writableDatabase

        // CalendarView.OnDateChangeListener has only abstract onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        // よって、SAM変換によりonSelectedDayChangeを省略できる
        calendar.setOnDateChangeListener { calendar, year, month, dayOfMonth ->
            Log.d("myTag", "now date is $year/$month/$dayOfMonth")
            textView.text = "now date is $year/$month/$dayOfMonth"
        }
    }

    // 日付がタップされたときにEdit（または閲覧）画面に遷移するメソッド
    // xmlのonClick属性に対応させるには、引数にViewパラメータを含む必要がある。
    // TODO: カレンダーのインスタンスを引数に含める
    fun moveToEditPage(view: View){
        // 一つ目のコンストラクタはContext。ActivityはContextのサブクラスなのでthisを使う
        // 二つ目はIntentが送られるアプリコンポーネントのClass（開始されるActivity）
        var intent = Intent(this, EditDiaryActivity::class.java)

        // TODO: カレンダー部分で選択してる日付を渡すようにする
        intent.putExtra(EXTRA_DATE, Date())

        startActivity(intent)
    }
}
