package com.example.kouhei.todidlist

import android.content.Context
import android.graphics.Color
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
        "01" -> R.color.colorOfJan
        "02" -> R.color.colorOfFeb
        "03" -> R.color.colorOfMar
        "04" -> R.color.colorOfApr
        "05" -> R.color.colorOfMay
        "06" -> R.color.colorOfJun
        "07" -> R.color.colorOfJul
        "08" -> R.color.colorOfAug
        "09" -> R.color.colorOfSep
        "10" -> R.color.colorOfOct
        "11" -> R.color.colorOfNov
        "12" -> R.color.colorOfDec
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