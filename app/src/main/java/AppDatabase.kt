/**
 * Created by kouhei on 3/11/2018.
 */

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Daily::class], version = 1) // Kotlin 1.2からは arrayOf(Daily::class)の代わりに[Daily::class]と書ける
abstract class AppDatabase : RoomDatabase() {

    // DAOを取得する。
    // この抽象クラスの実装はRoomライブラリのアノテーション処理で自動生成される
    abstract fun dailyDao(): DailyDao

    // valでも良い。
    // abstract val dao: UserDao
}