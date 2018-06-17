package com.example.kouhei.todidlist

import android.arch.persistence.room.*

@Dao
interface ImageDao {

    /**
     * 作成されたファイル名でimageエンティティをinsertする。
     * updateはしない。
     */
    @Insert
    fun insert(image: Image)

    @Update
    fun update(image: Image)

    /**
     * 日記のcalendar_dateで画像のEntityをすべて取得する。
     * 今のところ(2018/6/7)日記と画像は１対１だが、１対多にする改修の可能性を見越して。
     */
    @Query("SELECT * FROM image WHERE calendar_date = :calendarDate")
    fun getImagesWithCalendarDate(calendarDate: Int): List<Image>

    /**
     * 画像ファイルの名前を指定して、対象の画像ファイル名と日記の関係をDBからdeleteする。
     */
    @Query("DELETE FROM image WHERE image_name = :imageName")
    fun deleteImageWithImageName(imageName: String)

    /**
     * 画像を一つ削除する。
     * 日記の画像を変更するときに使う。
     * 現状、これは使ってない。
     */
    @Delete
    fun delete(image: Image)
}