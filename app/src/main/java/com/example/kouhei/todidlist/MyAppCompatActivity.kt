package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AlertDialog

open class MyAppCompatActivity: AppCompatActivity() {

    // 画像を置く外部ストレージのパスを設定
    var filePath = Environment.getExternalStorageDirectory().path + "DCIM/Camera/" + R.string.neko_jpg_for_develop

    // onActivityResultで受け取った結果がどこから来たものか判別するのに使う。
    val GALLERY = 1

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