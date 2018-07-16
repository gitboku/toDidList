package com.example.kouhei.todidlist

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity
class Image {

    /**
     * 一つの日記に０個～複数個の画像が存在する。
     */

    /**
     * 主キー。
     */
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    /**
     * 内部ストレージに保存するときもこの名前を使用する。
     * "name"という名前がついてるが、実際はURIが保存される。
     */
    @ColumnInfo(name = "image_name")
    var imageName: String = ""

    /**
     * 画像が紐づいてる日記の日付
     * DATE_PATTERN_TO_DATABASEのフォーマットで保存されている
     */
    @ColumnInfo(name = "calendar_date")
    var calendarDate: String = ""

    /**
     * 画像が紐づいている日記のID
     */
    @ColumnInfo(name = "diary_id")
    var diaryId: Int = 0
}