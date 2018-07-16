package com.example.kouhei.todidlist

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
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
        var selectDate = getShapedTimeStamp()
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

        // Toolbar のタイトルを選択した日付に変更(EditPage と同じフォーマットでOK）
        main_page_toolbar.title  = selectDate.toString().shapeForEditUi()
        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(main_page_toolbar)

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
        textViewUpdate(db, getSelectDate(nowTimeStamp))

        // カレンダーの日付を押下したときのリスナー
        // CalendarView.OnDateChangeListener has only abstract onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth)
        // よって、SAM変換によりonSelectedDayChangeを省略できる
        calendar.setOnDateChangeListener { calendar, year, month, dayOfMonth ->
            nowTimeStamp = getCalendarTimeStamp(year, month, dayOfMonth)
            selectDate = getSelectDate(nowTimeStamp)

            // Toolbar のタイトルを選択した日付に変更(EditPage と同じフォーマットでOK）
            main_page_toolbar.title  = selectDate.toString().shapeForEditUi()

            textViewUpdate(db, selectDate)
        }

        // 日記ページをクリックしたときのリスナー
        textView.setOnClickListener {
            val intent = Intent(this, EditDiaryActivity::class.java)
            // カレンダー部分で選択してる日付をTimeStampをLong型で渡す
            intent.putExtra(EXTRA_DATE, nowTimeStamp)
            intent.putExtra(FROM_CLASS, this.localClassName)
            moveToAnotherPage(intent)
        }
    }

    /**
     * textViewの日記本文と背景画像を読み込んで表示する。
     * calendarがクリックされたときに呼びだす。
     */
    private fun textViewUpdate(db: AppDatabase, targetDate: String) {
        updateDiaryText(db, targetDate)
        runBlocking { updateDiaryImage(db, targetDate) }
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
    private fun updateDiaryText(db: AppDatabase, targetDate: String) {
        val thread = launch {
            val diary = db.diaryDao().getEntityWithDate(targetDate)
            textView.text = diary?.diaryText ?: getText(R.string.diary_yet)
        }
        thread.start()
    }

    /**
     * textViewの背景画像を更新する。
     * 背景画像がない場所を押下したら背景画像を消す。
     * 画像の読み込みには時間がかかる可能性があり、suspend functionにしてある。
     * よって、runBlocking{ updateDiaryImage() }のようにして使う。
     */
    private suspend fun updateDiaryImage(db: AppDatabase, targetDate: String) {
        val loadedImageName = async {
            var nowImageName: String? = null
            // 日記の画像を内部ストレージから取得して、diaryPanelの背景にセットする。
            // 現状(2018/06/07)では日記と画像は１対１なので、画像配列の最初を取り出す。
            val imageList = db.imageDao().getImagesWithCalendarDate(targetDate)
            if (imageList.isNotEmpty()){
                val image = imageList.first()
                nowImageName = image.imageName
            }
            return@async nowImageName
        }.await()
        if (loadedImageName != null) {
            main_page_layout.background = BitmapDrawable(resources, getImageFromInternalStorage(this, loadedImageName))
        } else {
            main_page_layout.background = null
        }
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_calendar, menu)
        return true
    }
}
