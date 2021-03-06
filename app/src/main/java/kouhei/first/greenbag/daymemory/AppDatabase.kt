package kouhei.first.greenbag.daymemory

import android.arch.persistence.room.*

@Database(entities = [Diary::class], version = 1) // Kotlin 1.2からは arrayOf(kouhei.first.greenbag.daymemory.Diary::class)の代わりに[kouhei.first.greenbag.daymemory.Diary::class]と書ける
abstract class AppDatabase : RoomDatabase() {

    // DAOを取得する。
    // この抽象クラスの実装はRoomライブラリのアノテーション処理で自動生成される
    abstract fun diaryDao(): DiaryDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "pre-release1").build()
                }
            }
            return INSTANCE
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}
