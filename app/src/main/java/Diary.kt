/**
 * Created by kouhei on 3/11/2018.
 */

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Diary {
    /**
     * ORMライブラリではテーブルに保存される各行のデータをクラスとして表現する
     * 必要なデータ構造をクラスとして表現することで、そのデータの保存に必要なテーブルが自動生成される
     * PrimaryKey is Non-null
     */
    @PrimaryKey
    var did: Int = 0

    // ColumnInfoアノテーションをつけることで詳細な設定ができる
    // name属性は後でSQLを書く時のカラム名として使う。デフォルトのカラム名はプロパティ名と同じ
    @ColumnInfo(name = "diary_text")
    var diaryText: String? = null

    // 日記の日付
    //CalendarView.getDate() gets the selected date in milliseconds since January 1, 1970 00:00:00 in getDefault() time zone.
    @ColumnInfo(name = "calendar_date")
    var calendarDate: Int = 0

    // 日記のイメージ画像のID
    @ColumnInfo(name = "image_id")
    var imageId: Int? = null
}