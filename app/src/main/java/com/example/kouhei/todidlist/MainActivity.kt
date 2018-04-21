package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val EXTRA_DATE = "com.example.todidList.SELECTED_DATE"
const val FROM_CLASS = "com.example.todidList.FROM_CLASS"

@Suppress("UsePropertyAccessSyntax")
class MainActivity :  AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var nowTimeStamp: Long = 0
        var selectDate: Int = getNowDate()

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(main_page_toolbar)

        // EditPageからのselectDateがなければ、defaultとしてinitのselectDateを渡す
        // TODO: not use magic word "EditDiaryActivity"
        when (intent.getStringExtra(FROM_CLASS)) {
            "EditDiaryActivity" -> {
                nowTimeStamp = intent.getLongExtra(EXTRA_DATE, 0)
                calendar.setDate(nowTimeStamp)
            }
            else -> nowTimeStamp = 0
        }

        // CalendarView.OnDateChangeListener has only abstract onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        // よって、SAM変換によりonSelectedDayChangeを省略できる
        // The month that was set [0-11].
        calendar.setOnDateChangeListener { calendar, year, month, dayOfMonth ->
            textView.text = getTextView("$year/$month/$dayOfMonth")
            nowTimeStamp = getNowTimeStamp(year, month, dayOfMonth)
            selectDate = getSelectDate(nowTimeStamp)
        }

        textView.setOnClickListener {
            moveToEditPage(nowTimeStamp)
        }
    }

    /**
     * EditDairyActivityに遷移するメソッド
     * 引数にViewパラメータを入れればxmlのonClick属性に対応するが、今回はTextViewのListenerを使うのでいらない。
     */
    fun moveToEditPage(nowTimeStamp: Long){
        // 一つ目のコンストラクタはContext。ActivityはContextのサブクラスなのでthisを使う
        // 二つ目はIntentが送られるアプリコンポーネントのClass（開始されるActivity）
        val intent = Intent(this, EditDiaryActivity::class.java)

        // カレンダー部分で選択してる日付をTimeStampをLong型で渡す
        intent.putExtra(EXTRA_DATE, nowTimeStamp)

        startActivity(intent)
    }

    // CalendarViewで選択してる日付のタイムスタンプを取得する
    private fun getNowTimeStamp(year: Int, month: Int, dayOfMonth: Int): Long {
        val c = Calendar.getInstance()
        c.set(year, month, dayOfMonth)

        return c.timeInMillis
    }

    // textViewの部分に表示するための文章を返す。
    // TODO The diary text of the selected date is returned.
    private fun getTextView(any: Any): String {
        var myText = "now date is ${any}"

        return myText
    }
}
