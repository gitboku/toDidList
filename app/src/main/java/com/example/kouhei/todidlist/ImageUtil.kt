package com.example.kouhei.todidlist

import android.content.Context
import kotlin.concurrent.thread

/**
 * 日記の画像を内部ストレージから取得する。
 * 現状(2018/06/07)では日記と画像は１対１なので、画像配列の最初を取り出す。
 */
fun getImageNameFromDb(imageDao: ImageDao, selectDate: String): String? {
    val imageList = imageDao.getImagesWithCalendarDate(selectDate)
    if (imageList.isNotEmpty()){
        val image = imageList.first()
        // EditDiaryActivityの背景に画像を設定する。画像Entityがなければ何もしない。
        return image.imageName
    }
    return null
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