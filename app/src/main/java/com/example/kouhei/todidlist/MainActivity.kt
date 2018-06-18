package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import kotlin.system.exitProcess

const val EXTRA_DATE = "com.example.todidList.SELECTED_DATE"
const val FROM_CLASS = "com.example.todidList.FROM_CLASS"

@Suppress("UsePropertyAccessSyntax")
class MainActivity :  MyAppCompatActivity() {

    private var db: AppDatabase

    companion object {
        val EDIT_DIARY: String = EditDiaryActivity::class.java.simpleName
        val STACK_PAGE: String = MainStackActivity::class.java.simpleName
        var nowTimeStamp: Long = System.currentTimeMillis()
        var selectDate = getDateTimeString()!!.toInt()
    }

    init {
        try {
            db = AppDatabase.getInstance(this)!!
        } catch (e: NullPointerException) {
            Log.e("myError", "db is null in MainActivity.")
            exitProcess(0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
            intent.putExtra(FROM_CLASS, this.localClassName)
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

    /**
     * textViewの文章を更新する
     */
    private fun updateTextView(db: AppDatabase, selectDate: Int) {
        val thread = launch {
            val diary = db.diaryDao().getEntityWithDate(selectDate)
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
