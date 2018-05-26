package com.example.kouhei.todidlist

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN_TO_DATABASE = "yyyyMMdd"

/**
 * R.colorで設定した月のテーマカラーIDを取得する。
 * 引数の例："01", "12"
 * 返り値　：set**Color()の引数に使用する。
 */
fun getMonthColor(context: Context, month: String): Int {
    val colorId = when(month) {
        "01" -> R.color.colorOf01
        "02" -> R.color.colorOf02
        "03" -> R.color.colorOf03
        "04" -> R.color.colorOf04
        "05" -> R.color.colorOf05
        "06" -> R.color.colorOf06
        "07" -> R.color.colorOf07
        "08" -> R.color.colorOf08
        "09" -> R.color.colorOf09
        "10" -> R.color.colorOf10
        "11" -> R.color.colorOf11
        "12" -> R.color.colorOf12
        else -> R.color.colorPrimary
    }
    return context.getColor(colorId)
}

/**
 * 選択している日付をInt型で、DATE_PATTERN_TO_DATABASEのフォーマットで返す。
 * 何らかの理由で失敗したら0を返す
 */
fun getSelectDate(timestamp: Long): Int {
    val selectDateString = getDateTimeString(timestamp)
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
fun getDateTimeString(timestamp: Long = System.currentTimeMillis(),
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