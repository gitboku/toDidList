package com.example.kouhei.todidlist

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Diary {

    /**
     * 一つの日付につき、日記は一つしか存在しない。（日記画像は0~複数個ある）
     * 本文が""な日記もある。
     */

    /**
     * ORMライブラリではテーブルに保存される各行のデータをクラスとして表現する
     * 必要なデータ構造をクラスとして表現することで、そのデータの保存に必要なテーブルが自動生成される
     * PrimaryKey is Non-null
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "diary_id")
    var diaryId: Int = 0

    // ColumnInfoアノテーションをつけることで詳細な設定ができる
    // name属性は後でSQLを書く時のカラム名として使う。デフォルトのカラム名はプロパティ名と同じ
    @ColumnInfo(name = "diary_text")
    var diaryText: String? = null

    // 日記の日付
    //CalendarView.getDate() gets the selected date in milliseconds since January 1, 1970 00:00:00 in getDefault() time zone.
    // DATE_PATTERN_TO_DATABASEのフォーマットで保存されている
    @ColumnInfo(name = "diary_date")
    var diaryDate: String = ""

    // 日記の画像のURI
    @ColumnInfo(name = "image_uri")
    var imageUri: String? = null
}