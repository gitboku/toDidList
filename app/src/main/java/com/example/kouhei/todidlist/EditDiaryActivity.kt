package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.content.Intent
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.MenuItem
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.widget.Toast
import java.io.IOException
import java.nio.channels.GatheringByteChannel
import kotlin.concurrent.thread

class EditDiaryActivity : MyAppCompatActivity() {

    private var db: AppDatabase? = null
    private var nowTimeStamp: Long = 0
    private var selectDate: Int = 0

    // パーミッションを求めるダイアログにユーザーが応答したとき、
    // requestLocationPermission()が渡してonRequestPermissionsResult()が受け取る合言葉
    private val READ_PERMISSION_REQUEST_CODE = 1000

    companion object {
        val MAIN_PAGE: String = MainActivity::class.java.simpleName
        val STACK_PAGE: String = MainStackActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        db = AppDatabase.getInstance(this)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(edit_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // kotlinではgetIntent()は"intent"でOK
        nowTimeStamp = intent.getLongExtra(EXTRA_DATE, 0)
        selectDate = getSelectDate(nowTimeStamp)

        // Toolbarの色を、選択された月のテーマカラーに変更
        edit_page_toolbar.setBackgroundColor(getMonthColor(this, selectDate.toString().substring(4, 6)))

        // 選択してる日付の日記Entityを取得し、日記本文を表示する
        thread {
            val diary = db?.diaryDao()?.getEntityWithDate(selectDate)

            // DiaryのEntityはnullである場合がある。
            if (diary != null){
                diaryPanel.setText(diary.diaryText)
            } else {
                // TODO when click datePanel in MainActivity, sometimes error here.
                diaryPanel.setText(R.string.diary_yet)
            }
        }
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

        // 画像保存ボタン（このボタンを押しただけでは画像はsaveされない）
        // 画像をsaveするのはR.id.action_saveが押されたとき。
        R.id.action_image -> {
            showPicturesFromExternalStorage()
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
    private fun showPicturesFromExternalStorage() {
        // Android 6, API 23以上でパーミッションの確認が必要
        // https://akira-watson.com/android/external-storage-image.html
        if (Build.VERSION.SDK_INT >= 23) {
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                // すでに許可されているなら、ExternalStorageから画像を選ばせる
                showPictureDialog()
            } else {
                // 許可されていなければ、許可を求める
                requestLocationPermission()
            }
        } else {
            // 23より下のAPIならパーミッションチェックしないでExternalStorageから画像を選ばせる
            showPictureDialog()
        }
    }

    /**
     * setResultで必要なデータを渡しておけば起動元のアクティビティのonActivityResultでそれを受け取ることができる。
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // RESULT_OK = -1, RESULT_CANCELED = 0
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.activity_canceled), Toast.LENGTH_SHORT).show()
            return
        }

        if (data != null && requestCode == GALLERY) {
            val contentURI = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                val bitmapDrawable = BitmapDrawable(resources, bitmap)
                Toast.makeText(this, getString(R.string.image_not_saved_yet), Toast.LENGTH_SHORT).show()
                edit_page_layout.background = bitmapDrawable

            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, getString(R.string.failed_get_image), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 許可を求める
    fun requestLocationPermission() {
        // shouldShowRequestPermissionRationale():
        // アプリがパーミッションを既にリクエストしていて、ユーザーがそのパーミッションを拒否した場合、このメソッドは true を返します。
        // https://developer.android.com/training/permissions/requesting
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_PERMISSION_REQUEST_CODE)
        } else {
            val toast = Toast.makeText(this, getString(R.string.request_permission_msg), Toast.LENGTH_SHORT)
            toast.show()

            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    READ_PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * パーミッションを求めるダイアログにユーザーが応答すると、システムがこのメソッドを呼び出す。
     * コールバックにはrequestPermission()に渡されたものと同じリクエストコードが渡される。
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            READ_PERMISSION_REQUEST_CODE -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // リクエストが許可された
                // TODO: EditDiaryActivity::readImageFromExternalStorage()とすれば、
                // requestLocationPermission()とonRequestPermissionsResult()はMyAppCompatActivityに移せる？
                showPictureDialog()
            } else {
                // リクエストが拒否された
                val toast = Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_SHORT)
                toast.show()
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
    fun saveDiary() {
        val diaryDao = db?.diaryDao()
        thread {
            val diary = diaryDao?.getEntityWithDate(selectDate)

            if (diary != null){
                diaryDao.updateDiaryWithDate(diaryPanel.text.toString(), selectDate)
            } else {
                val diary = Diary()
                diary.diaryText = diaryPanel.text.toString()
                diary.calendarDate = selectDate
                diaryDao?.insert(diary)
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