package com.example.kouhei.todidlist

import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.app.DatePickerDialog.OnDateSetListener
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DATE
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DIARY_ID
import kotlinx.android.synthetic.main.activity_edit_diary.*
import kotlin.concurrent.thread

class EditDiaryActivity : MyAppCompatActivity(), OnDateSetListener {

    private val DEFAULT_DIARY_ID = -1
    private var diaryId: Int = DEFAULT_DIARY_ID

    private val closedLineMax = 2   // 本文を閉じたときの最大行数
    private val openedLineMax = 100 // 本文を開いたときの最大行数

    private lateinit var db: AppDatabase
    private var nowTimeStamp: Long = 0
    private lateinit var selectDate: String

    /**
     * 新しくsaveされる画像のURI
     */
    private var newImageUri: String? = null

    /**
     * 既存の日記のimage_uri
     */
    private var oldImageUri: String? = null

    /**
     * 画像が削除されたのかどうかを示すフラグ
     * 'newUri == null && oldUri != null'の場合に考えられる以下の２パターンを判別するのに使う
     * 　・古い画像を削除した。
     * 　・画像は変更しなかった。
     */
    private var isImageDelete: Boolean = false

    /**
     * 日記編集ページで操作される日記エンティティ
     */
    private lateinit var diary: Diary
    private var isNewDiary = true

    /**
     * diaryエンティティのDAO(Data Access Object)
     * insertやupdateはこれを用いる。
     */
    private lateinit var diaryDao: DiaryDao

    init {
        try {
            db = AppDatabase.getInstance(this)!!
            diaryDao = db.diaryDao()

        } catch (e: Exception) {
            // もしDBを取得する段階でエラーを出したら前のページに戻る
            val mIntent = getMyIntent()
            moveToAnotherPage(mIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // kotlinではgetIntent()は"intent"でOK
        nowTimeStamp = intent.getLongExtra(SELECTED_DATE, 0)
        selectDate = getSelectDate(nowTimeStamp)

        // DBから選択し、結果がNull(新規の日記)なら新しいDiary エンティティを使用する
        diaryId = intent.getIntExtra(SELECTED_DIARY_ID, DEFAULT_DIARY_ID)
        if (diaryId != DEFAULT_DIARY_ID) {
            thread {
                diary = diaryDao.selectDiary(diaryId)
                oldImageUri = diary.imageUri
                isNewDiary = false
            }
        } else {
            isNewDiary = true
        }

        // Toolbarのタイトルを日付にする
        edit_page_toolbar.title  = selectDate.shapeForEditUi()

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(edit_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 日記本文を表示するViewがタップされたときの動作
        // TODO: use animation
        diaryPanel.setOnClickListener {
            // EditTextの現在の状態は最大行数で判断する
            if (diaryPanel.maxLines == closedLineMax) {
                // 閉じていたら開く
                changeViewStatus(diaryPanel, ViewGroup.LayoutParams.MATCH_PARENT, openedLineMax)
            } else {
                // 開いていたら閉じる
                changeViewStatus(diaryPanel, ViewGroup.LayoutParams.WRAP_CONTENT, closedLineMax)
            }
        }

        // ImageViewの部分をタップしたら、画面が全画面で見えるようにほかのViewを隠す
        diaryImage.setOnClickListener {
            toggleViewVisibility()
        }

        // 選択してる日付の日記Entityと内部ストレージの画像を取得し、日記本文を表示する
        if (!isNewDiary) {
            loadDiaryAndImage(diary)
        }
    }

    /**
     * status barとToolbarとEditTextがvisibilityを入れ替える
     */
    private fun toggleViewVisibility() {
        // EditText
        diaryPanel.visibility = if (diaryPanel.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        // Toolbar
        edit_page_toolbar.visibility = if (edit_page_toolbar.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
        // status bar
        window.decorView.systemUiVisibility = if (window.decorView.systemUiVisibility == View.SYSTEM_UI_FLAG_VISIBLE) {
            View.SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    /**
     * EditTextの、以下のステータスを変更する
     * ・layoutPrams.height
     * ・maxLines
     */
    private fun changeViewStatus(view: EditText, height: Int, maxLine: Int) {
        view.layoutParams.height = height
        view.maxLines = maxLine
    }

    /**
     * EditDiaryActivityを開始したとき、日記の本文と背景画像を取得して表示する。
     */
    private fun loadDiaryAndImage(existingDiary: Diary) {
        // 日記本文を表示する
        diaryPanel.setText(existingDiary.diaryText)

        oldImageUri = existingDiary.imageUri
        if (oldImageUri != null) {
            try {
                updateBackgroundImage(oldImageUri!!)
            } catch (e: Exception) {
                Log.e("myTag", getString(R.string.picture_cannot_read))
                val dialogMessage = getString(R.string.picture_cannot_read)
                val alert = android.app.AlertDialog.Builder(this)
                alert.setMessage(dialogMessage).setPositiveButton(getString(R.string.ok), null).show()
                e.printStackTrace()
            }
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        /* 保存ボタン */
        R.id.action_save -> {
            saveDiary()

            moveToAnotherPage(getMyIntent())
            true
        }

        /* 保存しないで戻るボタン
         ※R.id.homeは自分が作ったものなので反応しない。android.R.id.homeはAndroid SDKのもの
         */
        android.R.id.home -> {
            val mIntent = getMyIntent()
            moveToAnotherPage(mIntent)
            true
        }

        /*
        画像を外部ストレージから選ぶボタン
        EditPanelに画像が表示されるが、saveはまだされない
        画像をsaveするのはR.id.action_saveが押されたとき。
        */
        R.id.action_image -> {
            selectPicturesFromGallery()
            true
        }

        // 日記の日付変更ボタン
        R.id.action_date -> {
            showCalendarPanel()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * 日記の日付を選択するCalendarView を表示する
     * 参考： https://akira-watson.com/android/datepicker-timepicker.html
     */
    private fun showCalendarPanel() {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePick")
    }

    /**
     * OnDateSetListener インターフェースのメソッド。
     * DateDialogFragment の結果を受け取る
     * 参考： https://akira-watson.com/android/datepicker-timepicker.html
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // カレンダーダイアログで選んだ日付をセットする(この値をdiary.diary_dateにsaveする)
        selectDate = getSelectDate(getCalendarTimeStamp(year, month, dayOfMonth))
        // 成型して、タイトル部分に表示
        edit_page_toolbar.title = selectDate.shapeForEditUi()
    }

    /**
     * 外部ストレージにある画像一覧を表示する。
     * ユーザはその中から画像を一つ選び、選んだ画像がEditPanelに表示される（まだsaveはされない）
     * 外部ストレージにアクセスする許可がなければ、許可を求める。
     * ユーザーが拒否すれば、このアプリで画像は扱えない。
     */
    private fun selectPicturesFromGallery() {
        // Android 6, API 23以上でパーミッションの確認が必要だが、そもそもAPI 22 以下はターゲットにしていない。
        // https://akira-watson.com/android/external-storage-image.html
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // すでに許可されているなら、ExternalStorageから画像を選ばせる
            showPictureDialog()
        } else {
            // 許可されていなければ、許可を求める
            requestLocationPermission()
        }
    }

    /**
     * 「画像を選ぶ」ボタンを押したときに、フォトアプリにアクセスする権限があれば呼ばれるメソッド。
     * 「どこから画像を選ぶか」のダイアログが表示される。
     * https://demonuts.com/pick-image-gallery-camera-android/
     */
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(getString(R.string.where_image))

        // pictureDialogItems の中から一つ選ぶダイアログが表示される
        // MutableList<> にしようとしたらsetItems のタイミングでCastエラーが出た。
        val pictureDialogItems = if (diaryImage.drawable == null) {
            // 画像がないときは「画像を選ぶ」だけ。
            arrayOf(getString(R.string.choose_image_from_gallery))
        } else {
            // 画像があれば「削除」もある。
            arrayOf(getString(R.string.choose_image_from_gallery),
                    getString(R.string.delete_image))
        }

        /* 各選択肢にアクションの内容を追記する。
         when の順番はダイアログの選択肢の順番に依存しているので、
          pictureDialogItems の順番を変えたらwhen の順番も変えねばならない。 */
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> {
                    choosePhotoFromGallery()
                }
                1 -> {
                    // 背景も消す。
                    diaryImage.setImageBitmap(null)
                    newImageUri = null
                    isImageDelete = true
                }
            }
        }
        pictureDialog.show()
    }

    /**
     * フォトアプリから戻ってきたときに呼ばれるメソッド。
     * 画像を背景に表示したり、「画像が変更された」フラグを立てたりする
     * setResultで必要なデータを渡しておけば起動元のアクティビティのonActivityResultでそれを受け取ることができる。
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
         Galleryに行っても画像を選ばずに戻ってきた。
        RESULT_OK = -1, RESULT_CANCELED = 0
          */
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.activity_canceled), Toast.LENGTH_SHORT).show()
            return
        }

        if (data != null && requestCode == GALLERY) {
            // Gallery から画像を選んで戻ってきたときの動作
            newImageUri = data.dataString // 他のフォトアプリ上に保存されている画像のURL
            try {
                updateBackgroundImage(newImageUri!!)
                // ロジック上必要ないけど念のために更新する
                // updateBackgroundImage()が終わる前に更新してはいけない
                isImageDelete = false
                Toast.makeText(this, getString(R.string.image_not_saved_yet), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.failed_get_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 背景の画像を表示する。
     */
    private fun updateBackgroundImage(uriString: String) {
        Glide.with(this).load(Uri.parse(uriString)).into(diaryImage)
    }

    private fun getMyIntent(): Intent {
        val intent = Intent(this, MainStackActivity::class.java)
        intent.putExtra(SELECTED_DATE, nowTimeStamp) // MainPageから来たintentをそのまま返す

        return intent
    }

    /**
     * 日記をsaveする
     * 画像もここでsaveする
     * UpdateかInsertかはDiaryのEntityがnullかどうかで判断
     */
    private fun saveDiary() {
        thread {
            if (isNewDiary) {
                val newDiary = Diary()
                newDiary.diaryText = diaryPanel.text.toString()
                newDiary.diaryDate = selectDate
                newDiary.imageUri = getSaveUri()

                diaryDao.insert(newDiary)
            } else {
                diary.diaryDate = selectDate
                diary.diaryText = diaryPanel.text.toString()
                diary.imageUri  = getSaveUri()

                diaryDao.update(diary)
            }
        }
    }

    /**
     * 日記をinsertしたりupdateしたりするときのdiary.image_uri に入れるべきURI文字列を返す。
     */
    private fun getSaveUri(): String? {
        return if (newImageUri != null) {
            // 画像が変更されていれば、新しいURIを返す。
            newImageUri
        } else {
            if (oldImageUri == null) {
                // もともと画像はなかった場合
                null
            } else {
                // 'newUri == null && oldUri != null'の場合に考えられる以下の２パターンがある
                if (isImageDelete) {
                    // 画像を削除した
                    null
                } else {
                    // 画像は変更しなかった
                    oldImageUri
                }
            }
        }
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }
}