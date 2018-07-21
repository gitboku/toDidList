package com.example.kouhei.todidlist

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.kouhei.todidlist.MyApplication.Companion.isGrantedReadStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.io.FileNotFoundException
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
        main_page_toolbar.title  = selectDate.shapeForEditUi()
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
            main_page_toolbar.title  = selectDate.shapeForEditUi()

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
        val loadedImageURI = async {
            return@async getImageNameFromDb(db.imageDao(), targetDate)
        }.await()
        if (loadedImageURI != null && isGrantedReadStorage == PackageManager.PERMISSION_GRANTED) {
            try {
                val loadedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(loadedImageURI))
                main_page_layout.background = BitmapDrawable(resources, loadedBitmap)
            } catch (e: FileNotFoundException) {
                main_page_layout.background = null
                Log.e("myTag", "ファイルが削除されています。 ")
                e.printStackTrace()
            }
        } else if (isGrantedReadStorage != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.not_granted_read_storage), Toast.LENGTH_SHORT).show()
            main_page_layout.background = null
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
