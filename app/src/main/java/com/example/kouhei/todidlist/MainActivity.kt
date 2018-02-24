package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CalendarView
import java.util.*

const val EXTRA_DATE = "com.example.kouhei.todidList.SELECTED_DATE"

class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var myCalendar = findViewById<CalendarView>(R.id.calendar)
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
