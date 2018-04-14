package com.example.kouhei.todidlist

import android.arch.persistence.room.*
import com.example.kouhei.todidlist.Diary
import com.example.kouhei.todidlist.DiaryDao

@Database(entities = [Diary::class], version = 1) // Kotlin 1.2からは arrayOf(com.example.kouhei.todidlist.Diary::class)の代わりに[com.example.kouhei.todidlist.Diary::class]と書ける
abstract class AppDatabase : RoomDatabase() {

    // DAOを取得する。
    // この抽象クラスの実装はRoomライブラリのアノテーション処理で自動生成される
    abstract fun diaryDao(): DiaryDao
}