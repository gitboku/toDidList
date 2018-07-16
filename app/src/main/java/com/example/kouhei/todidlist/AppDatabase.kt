package com.example.kouhei.todidlist

import android.arch.persistence.room.*
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.migration.Migration



@Database(entities = [Diary::class, Image::class], version = 1) // Kotlin 1.2からは arrayOf(com.example.kouhei.todidlist.Diary::class)の代わりに[com.example.kouhei.todidlist.Diary::class]と書ける
abstract class AppDatabase : RoomDatabase() {

    // DAOを取得する。
    // この抽象クラスの実装はRoomライブラリのアノテーション処理で自動生成される
    abstract fun diaryDao(): DiaryDao
    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "dayMemory").build()
                }
            }
            return INSTANCE
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}
