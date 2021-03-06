package kouhei.first.greenbag.daymemory

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.TypedValue
import android.widget.Toast

open class MyAppCompatActivity: AppCompatActivity() {

    // onActivityResultで受け取った結果がどこから来たものか判別するのに使う。
    val GALLERY = 1

    // パーミッションを求めるダイアログにユーザーが応答したとき、
    // requestLocationPermission()が渡してonRequestPermissionsResult()が受け取る合言葉
    private val READ_PERMISSION_REQUEST_CODE = 1000

    /**
     * CardViewで表示される日記のリスト
     */
    var diaryList: ArrayList<Diary> = ArrayList()

    /**
     * RecyclerViewのレイアウトマネージャー
     */
    val manager = GridLayoutManager(this, 2)

    /**
     * RecyclerViewとListを紐づけるアダプター
     */
    val adapter = DiaryAdapter(this, diaryList)

    /**
     * RecyclerViewの日記リストのモデル
     */
    var mDiaryViewModel: DiaryViewModel? = null

    /**
     * RecyclerViewに表示するべき要素をdiaryTextListに追加する
     */
    fun addDiary(diaryList: List<Diary>) {
        diaryList.forEach { diary ->
            this.diaryList.add(diary)
        }
    }

    fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    /**
     * ユーザーに対しパーミッションを求める
     */
    fun requestLocationPermission() {
        // shouldShowRequestPermissionRationale():
        // アプリがパーミッションを既にリクエストしていて、ユーザーがそのパーミッションを拒否した場合、このメソッドは true を返します。
        // https://developer.android.com/training/permissions/requesting
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            /*
            ACTION_PICKでリクエストを投げると、写真の選択後にプロバイダのきょが得られる。
            それを別のcontextで処理しようとするとシステムはセキュリティ例外をスローする
            https://stackoverflow.com/questions/38301605/reading-com-google-android-apps-photos-contentprovider-mediacontentprovider-requ
             */
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            "com.google.android.apps.photos.contentprovider.impl.MediaContentProvider"),
                    READ_PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, getString(R.string.request_permission_msg), Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Photosから読み込む許可を取得しました", Toast.LENGTH_SHORT).show()
            } else {
                // リクエストが拒否された
                Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun choosePhotoFromGallery() {
        // ACTION_PICK: データから項目を選択し、選択した項目を返す。
        // https://developer.android.com/reference/android/content/Intent.html#ACTION_PICK
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // 起動したActivityの結果をonActivityResultでそれを受け取ることができる。
        // resolveActivity() を呼び出して、インテントを処理できるアプリがあるかを確認する必要がある。
        if (galleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(galleryIntent, GALLERY)
        } else {
            Toast.makeText(this, getString(R.string.cannot_found_appropriate_app), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 他のActivityに遷移するメソッド
     * intent: Intent
     * 一つ目のコンストラクタはContext。ActivityはContextのサブクラスなのでthisを使う
     * 二つ目はIntentが送られるアプリコンポーネントのClass（開始されるActivity）
     */
    fun moveToAnotherPage(intent: Intent){
        startActivity(intent)
    }
}