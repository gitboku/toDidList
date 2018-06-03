package com.example.kouhei.todidlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AlertDialog
import android.widget.Toast

open class MyAppCompatActivity: AppCompatActivity() {

    // 画像を置く外部ストレージのパスを設定
    var filePath = Environment.getExternalStorageDirectory().path + "DCIM/Camera/" + R.string.neko_jpg_for_develop

    // onActivityResultで受け取った結果がどこから来たものか判別するのに使う。
    val GALLERY = 1

    // パーミッションを求めるダイアログにユーザーが応答したとき、
    // requestLocationPermission()が渡してonRequestPermissionsResult()が受け取る合言葉
    private val READ_PERMISSION_REQUEST_CODE = 1000

    /**
     * https://demonuts.com/pick-image-gallery-camera-android/
     */
    fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle(getString(R.string.where_image))
        // pictureDialogItemsの中から一つ選ぶToastが表示される
        // 配列の二つ目に"use camera"を乗せて、takeCamera()を作り、setItems()でwhen 1 -> takeCamera()みたいにすれば、
        // 写真を選ぶ選択肢を追加できる。
        val pictureDialogItems = arrayOf(getString(R.string.choose_image_from_gallery))
        pictureDialog.setItems(pictureDialogItems,
                // 使用してない変数は"_"にrenameできるが、後で何なのかわからなくなるのでそのままにする。
                { dialog, which ->
                    choosePhotoFromGallery()
                })
        pictureDialog.show()
    }

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
                Toast.makeText(this, "Photosから読み込む許可を取得しました", Toast.LENGTH_SHORT).show()
            } else {
                // リクエストが拒否された
                val toast = Toast.makeText(this, getString(R.string.permission_denied_msg), Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    private fun choosePhotoFromGallery() {
        // ACTION_PICK: データから項目を選択し、選択した項目を返す。
        // https://developer.android.com/reference/android/content/Intent.html#ACTION_PICK
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        // 起動したActivityの結果をonActivityResultでそれを受け取ることができる。
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