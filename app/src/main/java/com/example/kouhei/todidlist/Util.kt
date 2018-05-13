package com.example.kouhei.todidlist

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

const val DATE_PATTERN_TO_DATABASE = "yyyyMMdd"

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
 * Long型で受け取ったtimestampをフォーマットDATE_PATTERN_TO_DATABASEの日付に変更する
 */
private fun getDateTimeString(timestamp: Long): String? {
    try {
        val sdf = SimpleDateFormat(DATE_PATTERN_TO_DATABASE)
        val netDate = Date(timestamp)

        return sdf.format(netDate)
    } catch (e: Exception) {
        Log.e("myTag", "Long to date error: " + e.toString())
        return e.toString()
    }
}

/**
 * 現在日時をDATE_PATTERN_TO_DATABASE形式で取得する.<br></br>
 */
fun getNowDate(): Int {
    val df = SimpleDateFormat(DATE_PATTERN_TO_DATABASE)
    val date = Date(System.currentTimeMillis())
    return df.format(date).toInt()
}

/**
 * 主にデバッグに使用する、ログをLogcatに出力する関数
 * tagを指定しなければmyTagになる
 */
fun myLogging(msg: String, tag: String = "myTag") {
    Log.d("myTag", msg)
}