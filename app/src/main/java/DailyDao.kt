/**
 * Created by kouhei on 3/11/2018.
 */

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface DailyDao{
    // Data Access Object
    // ユーザはこのインターフェースを用いてDBの操作を行う
    // このクラスがDAOなのではなく、@DaoアノテーションをつけることでRoomが自動的にDAOを生成する

    @Query("SELECT * FROM daily")
    fun getAll(): List<Daily>

    @Insert
    fun insert(daily: Daily)

    @Delete
    fun delete(daily: Daily)

    // TODO: 日付でDailyを検索
    // TODO: 日付でDailyを更新
}