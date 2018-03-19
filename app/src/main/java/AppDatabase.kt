/**
 * Created by kouhei on 3/11/2018.
 */

import android.arch.persistence.room.*

@Database(entities = [Diary::class], version = 1) // Kotlin 1.2からは arrayOf(Diary::class)の代わりに[Diary::class]と書ける
abstract class AppDatabase : RoomDatabase() {

    // DAOを取得する。
    // この抽象クラスの実装はRoomライブラリのアノテーション処理で自動生成される
    abstract fun diaryDao(): DiaryDao
}