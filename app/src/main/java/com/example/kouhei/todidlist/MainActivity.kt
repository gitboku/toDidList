package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import java.util.*

const val EXTRA_DATE = "com.example.todidList.SELECTED_DATE"
const val FROM_CLASS = "com.example.todidList.FROM_CLASS"

@Suppress("UsePropertyAccessSyntax")
class MainActivity :  MyAppCompatActivity() {

    companion object {
        val EDIT_DIARY: String = EditDiaryActivity::class.java.simpleName
        val STACK_PAGE: String = MainStackActivity::class.java.simpleName
        var nowTimeStamp: Long = System.currentTimeMillis()
        var selectDate = getDateTimeString()!!.toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = AppDatabase.getInstance(this)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(main_page_toolbar)
        main_page_toolbar.setBackgroundColor(getMonthColor(this, getSelectDate(nowTimeStamp).toString().substring(4, 6)))

        // EditPageからのselectDateがなければ、defaultとしてinitのselectDateを渡す
        // 各caseでの最後の文がnowTimeStampに代入される
        nowTimeStamp = when (intent.getStringExtra(FROM_CLASS)) {
            EDIT_DIARY, STACK_PAGE -> {
                intent.getLongExtra(EXTRA_DATE, 0)
            }
            else -> {
                System.currentTimeMillis()
            }
        }

        calendar.setDate(nowTimeStamp)
        updateTextView(db, getSelectDate(nowTimeStamp))

        // CalendarView.OnDateChangeListener has only abstract onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        // よって、SAM変換によりonSelectedDayChangeを省略できる
        // The month that was set [0-11].
        calendar.setOnDateChangeListener { calendar, year, month, dayOfMonth ->
            nowTimeStamp = getCalendarTimeStamp(year, month, dayOfMonth)
            main_page_toolbar.setBackgroundColor(getMonthColor(this, getSelectDate(nowTimeStamp).toString().substring(4, 6)))
            updateTextView(db, getSelectDate(nowTimeStamp))
            selectDate = getSelectDate(nowTimeStamp)
        }

        textView.setOnClickListener {
            val intent = Intent(this, EditDiaryActivity::class.java)
            // カレンダー部分で選択してる日付をTimeStampをLong型で渡す
            intent.putExtra(EXTRA_DATE, nowTimeStamp)
            moveToAnotherPage(intent)
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_move_to_stack_page -> {
            val intent = Intent(this, MainStackActivity::class.java)
            intent.putExtra(EXTRA_DATE, nowTimeStamp)
            moveToAnotherPage(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    // CalendarViewで選択してる日付のタイムスタンプを取得する
    private fun getCalendarTimeStamp(year: Int, month: Int, dayOfMonth: Int): Long {
        val c = Calendar.getInstance()
        c.set(year, month, dayOfMonth)

        return c.timeInMillis
    }

    /**
     * textViewの文章を更新する
     */
    private fun updateTextView(db: AppDatabase?, selectDate: Int) {
        val diaryText = getString(R.string.diary_yet)
        if (db == null) textView.text = diaryText

        val thread = launch {
            val diary = db?.diaryDao()?.getEntityWithDate(selectDate)
            textView.text = diary?.diaryText ?: getText(R.string.diary_yet)
        }
        thread.start()
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_calendar, menu)
        return true
    }
}
