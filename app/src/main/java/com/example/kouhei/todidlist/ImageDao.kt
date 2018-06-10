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
     * 画像を一つ削除する。
     * 日記の画像を変更するときに使う。
     */
    @Delete
    fun delete(image: Image)
}