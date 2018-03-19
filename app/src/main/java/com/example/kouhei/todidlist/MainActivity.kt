package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val EXTRA_DATE = "com.example.todidList.SELECTED_DATE"
const val DISP_DATE_FORMAT = "yyyy-MM-dd"

class MainActivity :  MyAppCompatActivity() {

    // アプリ起動時は当日のタイムスタンプで初期化
    private var nowTimeStamp: Long = Calendar.getInstance().timeInMillis
//    private var nowTimeStamp = calendar.date ← error

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // CalendarView.OnDateChangeListener has only abstract onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        // よって、SAM変換によりonSelectedDayChangeを省略できる
        // The month that was set [0-11].
        calendar.setOnDateChangeListener { calendar, year, month, dayOfMonth ->
            textView.text = getTextView("$year/$month/$dayOfMonth")
            nowTimeStamp = getNowTimeStamp(year, month, dayOfMonth)
            Log.d("myTag", nowTimeStamp.toString())
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

        // カレンダー部分で選択してる日付をTimeStampをLong型で渡す
        intent.putExtra(EXTRA_DATE, nowTimeStamp)

        startActivity(intent)
    }

    // CalendarViewで選択してる日付のタイムスタンプを取得する
    private fun getNowTimeStamp(year: Int, month: Int, dayOfMonth: Int): Long {
        var c = Calendar.getInstance()
        c.set(year, month, dayOfMonth)

        return c.timeInMillis
    }

    // textViewの部分に表示するための文章を返す。
    // TODO 選択してる日付から日記の内容をselectし、それを返すようにする
    private fun getTextView(any: Any): String {
        var myText = "now date is ${any}"

        return myText
    }
}
