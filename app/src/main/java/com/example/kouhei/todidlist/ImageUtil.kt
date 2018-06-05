package com.example.kouhei.todidlist

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.IOException


/**
 * 外部ストレージから読み込んだ画像を、日記の画像として内部ストレージに保存する。
 * 保存された画像と日記Entityとの関連付けはDBのImageテーブルで行う予定。
 * 参考：https://developer.android.com/training/data-storage/files
 */
fun saveImageToInternalStorage(context: Context, filename: String, bitmap: Bitmap) {
    // use: ブロック実行後に自動でclose()してくれる。
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        // it: Kotlinのラムダ式で、引数を指定しなかったときのデフォルト引数名
        it.write(convertBitmapToByteArray(bitmap))
    }
}

/**
 * ビットマップ画像をByteArrayに変換する
 * 参考：https://gist.github.com/vvkirillov/6e0475a56b9b2b14cd97
 */
fun convertBitmapToByteArray(bitmap: Bitmap, quality: Int = 100, compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): ByteArray? {
    var baos: ByteArrayOutputStream? = null
    try {
        baos = ByteArrayOutputStream()
        bitmap.compress(compressFormat, quality, baos)
        return baos.toByteArray()
    } finally {
        if (baos != null) {
            try {
                baos.close()
            } catch (e: IOException) {
                myLogging("ByteArrayOutputStream was not closed: " + e.toString())
            }
        }
    }
}