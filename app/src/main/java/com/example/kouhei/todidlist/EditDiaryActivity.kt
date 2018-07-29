package com.example.kouhei.todidlist

import android.os.Bundle
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.MediaStore
import android.app.DatePickerDialog.OnDateSetListener
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DATE
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DIARY_ID
import kotlinx.android.synthetic.main.activity_edit_diary.*
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class EditDiaryActivity : MyAppCompatActivity(), OnDateSetListener {

    private val DEFAULT_DIARY_ID = -1
    private var diaryId: Int = DEFAULT_DIARY_ID

    private lateinit var db: AppDatabase
    private var nowTimeStamp: Long = 0
    private lateinit var selectDate: String
    private lateinit var newBitmap: Bitmap

    /**
     * 新しくsaveされる画像のURI
     */
    private var newImageUri: String? = null

    /**
     * 既存の日記のimage_uri
     */
    private var oldImageUri: String? = null

    /**
     * 日記の画像が変更されたかどうかを示す。
     * falseなら、saveImage()の画像更新部分は飛ばす。
     */
    private var isImageChanged = false

    /**
     * 新しく選択した画像。
     * 日記の画像を新しく保存するときに使う。
     */
    private lateinit var saveBitmap: Bitmap

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

        // 選択してる日付の日記Entityと内部ストレージの画像を取得し、日記本文を表示する
        if (!isNewDiary) {
            loadDiaryAndImage(diary)
        }
    }

    /**
     * EditDiaryActivityを開始したとき、日記の本文と背景画像を取得して表示する。
     */
    private fun loadDiaryAndImage(existingDiary: Diary) {
        // 日記本文を表示する
        diaryPanel.setText(existingDiary.diaryText)

        val imageURI = existingDiary.imageUri
        if (imageURI != null) {
            oldImageUri = imageURI
            try {
                // TODO Glide を使用して画像を表示する。
                val loadedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(imageURI))
                edit_page_layout.background = BitmapDrawable(resources, loadedBitmap)
            } catch (e: FileNotFoundException) {
                Log.e("myTag", getString(R.string.picture_cannot_read))
                Toast.makeText(this, getString(R.string.picture_cannot_read), Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } else {
            oldImageUri = null
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            saveDiary()

            moveToAnotherPage(getMyIntent())
            true
        }

        /* 戻るボタン
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

        // 日付変更ボタン
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
     * https://demonuts.com/pick-image-gallery-camera-android/
     */
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(getString(R.string.where_image))

        // pictureDialogItems の中から一つ選ぶダイアログが表示される
        // MutableList<> にしようとしたらsetItems のタイミングでCastエラーが出た。
        val pictureDialogItems = if (edit_page_layout.background == null) {
            // 写真がないときは「画像を選ぶ」だけ。
            arrayOf(getString(R.string.choose_image_from_gallery))
        } else {
            // 写真があれば「削除」「エクスポート」もある。
            arrayOf(getString(R.string.choose_image_from_gallery),
                    getString(R.string.delete_image),
                    getString(R.string.store_to_other_app))
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
                    edit_page_layout.background = null
                    newImageUri = null
                    isImageChanged = true
                }
                2 -> {

                }
            }
        }
        pictureDialog.show()
    }

    /**
     * setResultで必要なデータを渡しておけば起動元のアクティビティのonActivityResultでそれを受け取ることができる。
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /*
         Galleryに行っても写真を選ばずに戻ってきた。
        RESULT_OK = -1, RESULT_CANCELED = 0
          */
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.activity_canceled), Toast.LENGTH_SHORT).show()
            return
        }

        if (data != null && requestCode == GALLERY) {
            // Gallery から写真を選んで戻ってきたときの動作

            val contentURI = data.data // 他のフォトアプリ上に保存されている画像のURL
            try {
                // Photosから画像を取得する。
                newBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                // bitmapDrawableに変換してEditPanelの背景に表示
                isImageChanged = true
                edit_page_layout.background = BitmapDrawable(resources, newBitmap)
                saveBitmap = BitmapDrawable(resources, newBitmap).bitmap

                Toast.makeText(this, getString(R.string.image_not_saved_yet), Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.failed_get_image), Toast.LENGTH_SHORT).show()
            }
        }
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
                newDiary.imageUri = saveAndGetImageUri()

                diaryDao.insert(newDiary)
            } else {
                diary.diaryDate = selectDate
                diary.diaryText = diaryPanel.text.toString()
                diary.imageUri  = saveAndGetImageUri()

                diaryDao.update(diary)
            }
        }
    }

    /**
     * 日記をinsertしたりupdateしたりするときのdiary.image_uri に入れるべきURI文字列を返す。
     */
    private fun saveAndGetImageUri(): String? {
        return if (isImageChanged) {
            if (edit_page_layout.background != null) {
                // 画像が変更されたときの挙動
                val timeStamp = SimpleDateFormat(DATE_PATTERN_TO_DATABASE).format(Date())
                val imageFileName = "JPEG_" + timeStamp + ".jpg"
                val savedUri = MediaStore.Images.Media.insertImage(contentResolver, saveBitmap, imageFileName, null)
                return savedUri
            } else {
                // 画像が削除されたときの挙動
                // TODO 外部ストレージから画像を削除した時の動作を入れる
                return null
            }
        } else {
            // 画像に変更がなかった場合は古いURIをそのまま返す。
            // もともと画像がなかったときは、oldImageUri にはnullが入っている。
            oldImageUri
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