package com.example.kouhei.todidlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.concurrent.thread

/**
 * 新しく内部ストレージに保存する画像の名前を生成する
 * imageNameは{calendarDate}_{incrementNumber}いう形をしている(e.g 20180607_0.png)。
 * imageNameがnullならまだ画像はないものと判断する。
 */
fun generateImageName(selectDate: Int, imageName: String?): String {
    if (imageName != null) {
        return selectDate.toString() + "_" + imageName.substring(9).toInt().plus(1).toString()
    }
    return selectDate.toString() + "_0"
}

/**
 * 名前で指定されたファイルを内部ストレージから取得する。
 * imageNameかNull or Emptyだったり、ファイル読み込みに失敗したらnullを返す
 */
fun getImageFromInternalStorage(context: Context, imageName: String?): Bitmap? {
    if (imageName.isNullOrEmpty()) {
        return null
    }

    try {
        return convertCompressedByteArrayToBitmap(context.openFileInput(imageName).readBytes())
    } catch (e: FileNotFoundException) {
        Log.e("myError", "Image File not found: " + e.toString())
    }
    return null
}

/**
 * ByteArray画像をBitmap画像にデコードする。
 * 参考：https://gist.github.com/vvkirillov/6e0475a56b9b2b14cd97
 */
fun convertCompressedByteArrayToBitmap(src: ByteArray): Bitmap
{
    return BitmapFactory.decodeByteArray(src, 0, src.size)
}

/**
 * 画像を削除する。
 * imageNameの削除と、ByteArrayの削除を両方やる。
 */
fun deleteImage(context: Context, imageName: String?, imageDao: ImageDao) {
    if (imageName != null) {
        deleteImageFromInternalStorage(context, imageName)
        deleteImageNameFromDb(imageName, imageDao)
    }
}

/**
 * 内部ストレージから画像ファイルを削除する
 */
private fun deleteImageFromInternalStorage(context: Context, imageName: String) {
    context.deleteFile(imageName)
}

/**
 * 画像ファイルの名前を指定して、対象の画像ファイル名と日記の関係をDBからdeleteする。
 */
private fun deleteImageNameFromDb(imageName: String, imageDao: ImageDao) {
    thread {
        imageDao.deleteImageWithImageName(imageName)
    }
}

/**
 * 画像を保存する。
 * imageNameをDBに保存するのと、BitmapをByteArrayにして内部ストレージに保存するのを両方やる。
 */
fun saveImage(context: Context, newImageName: String, newBitmap: Bitmap, imageDao: ImageDao) {
    saveImageToInternalStorage(context, newImageName, newBitmap)
    saveImageNameToDb(newImageName, imageDao)
}

/**
 * imageNameをDBに保存する。
 */
private fun saveImageNameToDb(newImageName: String, imageDao: ImageDao) {
    val targetDate = newImageName.substring(0, 8).toInt()
    thread {
        val imageEntityList = imageDao.getImagesWithCalendarDate(targetDate)
        if (imageEntityList.isEmpty()) {
            // 画像がなければinsertする。
            val newImage = Image()
            newImage.calendarDate = targetDate
            newImage.imageName = newImageName
            imageDao.insert(newImage)
        } else {
            // 今は日記と画像が１対１なので、単純に最初を取り出してupdateする。
            val imageEntity = imageEntityList.first()
            imageEntity.imageName = newImageName
            imageDao.update(imageEntity)
        }
    }
}

/**
 * 渡されたBitmap画像を、日記の画像として内部ストレージに保存する。
 * 保存された画像と日記Entityとの関連付けはDBのImageテーブルで行う予定。
 * 参考：https://developer.android.com/training/data-storage/files
 */
private fun saveImageToInternalStorage(context: Context, imageName: String, bitmap: Bitmap) {
    try {
        // use: ブロック実行後に自動でclose()してくれる。
        context.openFileOutput(imageName, Context.MODE_PRIVATE).use {
            // it: Kotlinのラムダ式で、引数を指定しなかったときのデフォルト引数名
            it.write(convertBitmapToByteArray(bitmap))
        }
    } catch (e: FileNotFoundException) {
        Log.e("myError", "ファイルが見つかりませんでした。\n" + e.toString())
    } catch (e: IOException) {
        Log.e("myError", "write()でエラーが発生しました。\n" + e.toString())
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
                Log.e("myError", "ByteArrayOutputStream was not closed: " + e.toString())
            }
        }
    }
}