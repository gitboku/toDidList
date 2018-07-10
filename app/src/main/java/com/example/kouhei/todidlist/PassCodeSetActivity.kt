package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pass_code_set.*
import android.support.v7.app.AppCompatActivity
import com.example.kouhei.todidlist.MyApplication.Companion.APP_NEED_PASSCODE
import kotlinx.android.synthetic.main.activity_main_stack.*

class PassCodeSetActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code_set)
        setSupportActionBar(pass_code_set_toolbar) // アプリ上部のToolbarを呼び出す

        // パスコードを使うかどうかのトグルを初期化する。
        // init{}に配置したらretResource()でエラーが出る。
        val initData = getSharedPreferences(getString(R.string.preference_file_name), AppCompatActivity.MODE_PRIVATE)
        lock_switch.isChecked = initData.getBoolean(APP_NEED_PASSCODE, false)

        // 「キャンセル」ボタン。MainStackActivityにもどる。
        cancel_button.setOnClickListener {
            val intent = Intent(this, MainStackActivity::class.java)
            moveToAnotherPage(intent)
        }

        // トグルが押下された（パスコードを使うかどうかを変更した）ら、その情報をPreferenceに保存する。
        lock_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            val data = getSharedPreferences(getString(R.string.preference_file_name), AppCompatActivity.MODE_PRIVATE)
            val editor = data.edit()
            editor.putBoolean(APP_NEED_PASSCODE, isChecked)
            editor.apply()
        }
    }
}
