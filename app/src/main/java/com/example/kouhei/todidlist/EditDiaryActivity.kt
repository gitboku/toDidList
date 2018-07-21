package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
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
import com.example.kouhei.todidlist.MyApplication.Companion.isGrantedReadStorage
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class EditDiaryActivity : MyAppCompatActivity(), OnDateSetListener {

    private lateinit var db: AppDatabase
    private var nowTimeStamp: Long = 0
    private lateinit var selectDate: String
    private lateinit var newBitmap: Bitmap

    /**
     * 日記の画像が変更されたかどうかを示す。
     * falseなら、saveImage()の画像更新部分は飛ばす。
     */
    private var isImageChanged = false

    /**
     * 現在日記に紐づいた画像の名前。
     * 内部ストレージから画像を読み込んだり、新しい画像の名前を決めるときに使用する。
     */
    private var oldImageName: String? = null

    /**
     * 新しく選択した画像。
     * 日記の画像を新しく保存するときに使う。
     */
    private lateinit var saveBitmap: Bitmap

    init {
        try {
            db = AppDatabase.getInstance(this)!!
        } catch (e: Exception) {
            // もしDBを取得する段階でエラーを出したら前のページに戻る
            val mIntent = getMyIntent()
            moveToAnotherPage(mIntent)
        }
    }

    companion object {
        // 将来的に何かで使うかもしれないので一応置いておく。
        val MAIN_PAGE: String = MainActivity::class.java.simpleName
        val STACK_PAGE: String = MainStackActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // kotlinではgetIntent()は"intent"でOK
        nowTimeStamp = intent.getLongExtra(EXTRA_DATE, 0)
        selectDate = getSelectDate(nowTimeStamp)

        // Toolbarのタイトルを日付にする
        edit_page_toolbar.title  = selectDate.shapeForEditUi()

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(edit_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 選択してる日付の日記Entityと内部ストレージの画像を取得し、日記本文を表示する
        runBlocking { awaitLoadDiaryAndImage() }
    }

    /**
     * EditDiaryActivityを開始したとき、日記の本文と背景画像を取得して表示する。
     */
    private suspend fun awaitLoadDiaryAndImage() {
        async {
            // 日記の法文を取得して、diaryPanelにセットする。
            val diary = db.diaryDao().getEntityWithDate(selectDate)
            // Diary.diaryTextはnullである場合がある。
            if (diary != null){
                diaryPanel.setText(diary.diaryText)
            } else {
                // デフォルトメッセージをTextView.textに入れると、ユーザが何も書かずに保存したとき
                // 「日記はありません。」という日記ができてしまうので、プレースホルダーにいれる。
                // が、よく考えたらEditDiaryActivityにプレースホルダーは必要ない。
                // diaryPanel.hint = getString(R.string.diary_yet)
            }
        }.await() // タスクを作ると同時に実行する

        // coroutineは軽量スレッドとして考えることができるので、thread{}で囲む必要はない。
        val loadedImageURI = if (isGrantedReadStorage == PackageManager.PERMISSION_GRANTED) {
            async {
                // 日記の画像を内部ストレージから取得して、diaryPanelの背景にセットする。
                // 現状(2018/06/07)では日記と画像は１対１なので、画像配列の最初を取り出す。
                oldImageName = getImageNameFromDb(db.imageDao(), selectDate)
                return@async oldImageName
            }.await()
        } else {
            Toast.makeText(this, getString(R.string.not_granted_read_storage), Toast.LENGTH_SHORT).show()
            null
        }
        if (loadedImageURI != null) {
            try {
                val loadedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(loadedImageURI))
                edit_page_layout.background = BitmapDrawable(resources, loadedBitmap)
            } catch (e: FileNotFoundException) {
                Log.e("myTag", "ファイルが削除されています。 ")
                e.printStackTrace()
            }
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

        // 戻るボタン
        // ※R.id.homeは自分が作ったものなので反応しない。android.R.id.homeはAndroid SDKのもの
        android.R.id.home -> {
            val mIntent = getMyIntent()
            moveToAnotherPage(mIntent)
            true
        }

        // 画像を外部ストレージから選ぶボタン
        // EditPanelに画像が表示されるが、saveはまだされない
        // 画像をsaveするのはR.id.action_saveが押されたとき。
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
        edit_page_toolbar.title = getSelectDate(getCalendarTimeStamp(year, month, dayOfMonth)).shapeForEditUi()
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
        // pictureDialogItemsの中から一つ選ぶToastが表示される
        // 写真を選ぶ選択肢を追加できる。
        val pictureDialogItems = arrayOf(
                getString(R.string.choose_image_from_gallery),
                getString(R.string.delete_image))
        pictureDialog.setItems(pictureDialogItems) { dialog, which ->
            when (which) {
                0 -> {
                    choosePhotoFromGallery()
                }
                1 -> {
                    deleteImage(this, oldImageName, db.imageDao())
                    // 背景も消す。
                    edit_page_layout.background = null
                    Toast.makeText(this, getString(R.string.image_deleted), Toast.LENGTH_SHORT).show()
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

        // RESULT_OK = -1, RESULT_CANCELED = 0
        // Galleryに行っても写真を選ばずに戻ってきた。
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.activity_canceled), Toast.LENGTH_SHORT).show()
            return
        }

        if (data != null && requestCode == GALLERY) {
            // Gallery から写真を選んで戻ってきたときの動作
            val contentURI = data.data
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
        // 遷移元に戻るように、Intentの第二引数(Class<?> cls)を動的に生成
        val cls = when(intent.getStringExtra(FROM_CLASS)) {
            STACK_PAGE -> MainStackActivity::class.java
            else -> MainActivity::class.java
        }
        val intent = Intent(this, cls)
        intent.putExtra(EXTRA_DATE, nowTimeStamp) // MainPageから来たintentをそのまま返す
        intent.putExtra(FROM_CLASS, this.localClassName)

        return intent
    }

    /**
     * 日記をsaveする
     * 画像もここでsaveする
     * UpdateかInsertかはDiaryのEntityがnullかどうかで判断
     */
    private fun saveDiary() {
        if (isImageChanged) {
            val timeStamp = SimpleDateFormat(DATE_PATTERN_TO_DATABASE).format(Date())
            val imageFileName = "JPEG_" + timeStamp + ".jpg"
            // 画像を保存
            val imageURI = MediaStore.Images.Media.insertImage(contentResolver, saveBitmap, imageFileName, null)
            // 画像名をDBに保存する
            saveImageNameToDb(imageURI, db.imageDao(), selectDate) // 画像の名前をDBに保存する
        }

        val diaryDao = db.diaryDao()
        thread {
            val diaryEntity = diaryDao.getEntityWithDate(selectDate)

            // 初めて日記を保存するときは、DBにDiaryレコードはないのでnullが返ってきている。
            if (diaryEntity != null){
                diaryDao.updateDiaryWithDate(diaryPanel.text.toString(), selectDate)
            } else {
                val newDiary = Diary()
                newDiary.diaryText = diaryPanel.text.toString()
                newDiary.calendarDate = selectDate
                diaryDao.insert(newDiary)
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