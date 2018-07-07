package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.content.Intent
import android.os.Build
import android.view.Menu
import android.view.MenuItem
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.io.IOException
import kotlin.concurrent.thread

class EditDiaryActivity : MyAppCompatActivity() {

    private lateinit var db: AppDatabase
    private var nowTimeStamp: Long = 0
    private var selectDate: Int = 0
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

    init {
        try {
            db = AppDatabase.getInstance(this)!!
        } catch (e: Exception) {
            // もしDBを取得する段階でエラーを出したら前のページに戻る
            Toast.makeText(this, getString(R.string.failed_to_get_DB), Toast.LENGTH_SHORT).show()
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
        edit_page_toolbar.title  = selectDate.toString().shapeForEditUi()

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
        val loadedImageName = async {
            // 日記の画像を内部ストレージから取得して、diaryPanelの背景にセットする。
            // 現状(2018/06/07)では日記と画像は１対１なので、画像配列の最初を取り出す。
            val imageList = db.imageDao().getImagesWithCalendarDate(selectDate)
            if (imageList.isNotEmpty()){
                val image = imageList.first()
                // EditDiaryActivityの背景に画像を設定する。画像Entityがなければ何もしない。
                oldImageName = image.imageName
            }
            return@async oldImageName
        }.await()
        edit_page_layout.background = BitmapDrawable(resources, getImageFromInternalStorage(this, loadedImageName))
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            saveDiary()

            val mIntent = getMyIntent()
            moveToAnotherPage(mIntent)
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
            selectPicturesFromExternalStorage()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * 外部ストレージにある画像一覧を表示する。
     * ユーザはその中から画像を一つ選び、選んだ画像がEditPanelに表示される（まだsaveはされない）
     * 外部ストレージにアクセスする許可がなければ、許可を求める。
     * ユーザーが拒否すれば、このアプリで画像は扱えない。
     */
    private fun selectPicturesFromExternalStorage() {
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
            val contentURI = data.data
            try {
                // Photosから画像を取得する。
                newBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                Toast.makeText(this, getString(R.string.image_not_saved_yet), Toast.LENGTH_SHORT).show()
                // bitmapDrawableに変換してEditPanelの背景に表示
                edit_page_layout.background = BitmapDrawable(resources, newBitmap)
                isImageChanged = true

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
        // TODO: このif文内部を実行して抜けるのに１０秒近くかかる。もはやバグの領域。。。
        if (isImageChanged) {
            // 画像を内部ストレージに保存する
            val imageDao = db.imageDao()
            val newImageName = generateImageName(selectDate, oldImageName)
            saveImage(this, newImageName, newBitmap, imageDao)
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