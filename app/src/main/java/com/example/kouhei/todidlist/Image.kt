package com.example.kouhei.todidlist

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Image {

    /**
     * 主キー。
     */
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    /**
     * 画像の名前。
     * 内部ストレージに保存するときもこの名前を使用する。
     * {calendarDate}_{incrementNumber}という名前になる。
     * 実際の運用ではNot-Nullだが、初期値が""になってしまうのでNullableにした
     */
    @ColumnInfo(name = "image_name")
    var imageName: String? = null

    /**
     * 画像が紐づいてる日記の日付
     * DATE_PATTERN_TO_DATABASEのフォーマットで保存されている
     */
    @ColumnInfo(name = "calendar_date")
    var calendarDate: Int = 0
}