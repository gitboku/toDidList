package com.example.kouhei.todidlist

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN_TO_DATABASE = "yyyyMMddHHmmss"

val week_name = arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

/**
 * MainStackActivityのUIに表示する用の日付フォーマット変換
 * 拡張関数なので${string}.shapeForStackUi() のように使う。
 * ※最初の４桁が年、続く２桁が月、その次の２桁が日として認識される。
 */
fun String.shapeForStackUi(): String {
    val year  = this.substring(0, 4).toInt()
    val month = this.substring(4, 6).toInt()
    val day   = this.substring(6, 8).toInt()

    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day)
    val dayOfWeek = week_name[calendar.get(Calendar.DAY_OF_WEEK) - 1]

    return month.toString() + "/" + day.toString() + "\n" + year.toString() + "\n(" + dayOfWeek + ")"
}

/**
 * EditDiaryActivityに表示する用の日付フォーマット変換機（拡張関数）
 * ※最初の４桁が年、続く２桁が月、その次の２桁が日として認識される。
 */
fun String.shapeForEditUi(): String {
    val year  = this.substring(0, 4).toInt()
    val month = this.substring(4, 6).toInt()
    val day   = this.substring(6, 8).toInt()

    val calendar = Calendar.getInstance()
    calendar.set(year, month - 1, day)
    val dayOfWeek = week_name[calendar.get(Calendar.DAY_OF_WEEK) - 1]

    return year.toString() + " " + month.toString() + "/" + day.toString() + " (" + dayOfWeek + ")"
}

/**
 * 選択している日付をInt型で、DATE_PATTERN_TO_DATABASEのフォーマットで返す。
 * 何らかの理由で失敗したら0を返す
 */
fun getSelectDate(timestamp: Long): Int {
    val selectDateString = getShapedTimeStamp(timestamp)
    try {
        // selectDateStringがnullならselectDateはnullになる
        val selectDate: Int? = selectDateString?.toInt()

        // Smart Cast to "Int" from "Int?"
        if (selectDate != null) {
            return selectDate
        }
        Log.e("myTag", "selectDate is null")
    }catch (e: Exception) {
        Log.e("myTag", "String? cast to int error: " + e.toString())
    }

    return 0
}

/**
 * Long型で受け取ったtimestampをフォーマット formatString の形の日付に変更する
 * 引数に何も指定しなければ現在時刻で返す
 */
fun getShapedTimeStamp(timestamp: Long = System.currentTimeMillis(),
                       formatString: String = DATE_PATTERN_TO_DATABASE): String? {
    try {
        val sdf = SimpleDateFormat(formatString)
        val netDate = Date(timestamp)

        return sdf.format(netDate)
    } catch (e: Exception) {
        Log.e("myTag", "Long to date error: " + e.toString())
        return e.toString()
    }
}

/**
 * 年と月と日付を渡すと、日付のタイムスタンプを取得する
 * 月はなぜか[1-12]ではなく[0-11]なので注意。
 */
fun getCalendarTimeStamp(year: Int, month: Int, dayOfMonth: Int): Long {
    val c = Calendar.getInstance()
    c.set(year, month, dayOfMonth)

    return c.timeInMillis
}

/**
 * 主にデバッグに使用する、ログをLogcatに出力する関数
 * tagを指定しなければmyTagになる
 */
fun myLogging(msg: String, tag: String = "myTag") {
    Log.d("myTag", msg)
}