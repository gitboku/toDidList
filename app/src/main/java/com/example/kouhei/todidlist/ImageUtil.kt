package com.example.kouhei.todidlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*
import kotlin.concurrent.thread

/**
 * 名前で指定されたファイルを内部ストレージから取得する。
 * imageNameかNull or Emptyだったり、ファイル読み込みに失敗したらnullを返す
 */
fun getImageFromInternalStorage(context: Context, imageName: String?): Bitmap? {
    if (imageName.isNullOrEmpty()) {
        return null
    }

    try {
        return convertByteArrayToBitmap(context.openFileInput(imageName).readBytes())
    } catch (e: FileNotFoundException) {
        Log.e("myError", "Image File not found: " + e.toString())
    }
    return null
}

/**
 * ByteArray画像をBitmap画像にデコードする。
 * 参考：https://gist.github.com/vvkirillov/6e0475a56b9b2b14cd97
 */
fun convertByteArrayToBitmap(src: ByteArray): Bitmap
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
 * imageNameをDBに保存する。
 */
fun saveImageNameToDb(newImageName: String, imageDao: ImageDao, targetDate: String) {
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