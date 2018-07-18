package com.example.kouhei.todidlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

open class MyAppCompatActivity: AppCompatActivity() {

    // onActivityResultで受け取った結果がどこから来たものか判別するのに使う。
    val GALLERY = 1

    // パーミッションを求めるダイアログにユーザーが応答したとき、
    // requestLocationPermission()が渡してonRequestPermissionsResult()が受け取る合言葉
    private val READ_PERMISSION_REQUEST_CODE = 1000

    /**
     * ユーザーに対しパーミッションを求める
     */
    fun requestLocationPermission() {
        // shouldShowRequestPermissionRationale():
        // アプリがパーミッションを既にリクエストしていて、ユーザーがそのパーミッションを拒否した場合、このメソッドは true を返します。
        // https://developer.android.com/training/permissions/requesting
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
        // TODO: resolveActivity() を呼び出して、インテントを処理できるアプリがあるかを確認する必要がある。
        startActivityForResult(galleryIntent, GALLERY)
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