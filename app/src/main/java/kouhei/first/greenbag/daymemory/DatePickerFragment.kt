package kouhei.first.greenbag.daymemory

import android.app.DatePickerDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.os.Bundle
import java.util.*

/**
 * EditDiaryActivity の"action_date"ボタンを押したときに表示するDatePickerDialog
 * 参考： https://akira-watson.com/android/datepicker-timepicker.html
 *
 */
class DatePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 現在時刻をピッカーのデフォルトとする
        val calendar = Calendar.getInstance()
        val year       = calendar.get(Calendar.YEAR)
        val month      = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity, activity as EditDiaryActivity, year, month, dayOfMonth)
    }
}