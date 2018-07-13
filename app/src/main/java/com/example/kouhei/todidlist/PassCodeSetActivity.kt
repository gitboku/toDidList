package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pass_code_set.*
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.kouhei.todidlist.MyApplication.Companion.APP_NEED_PASSCODE

class PassCodeSetActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code_set)

        setSupportActionBar(pass_code_set_toolbar) // アプリ上部のToolbarを呼び出す
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Toolbarの戻るボタンを表示

        // パスコードを使うかどうかのトグルを初期化する。
        // init{}に配置したらretResource()でエラーが出る。
        val initData = getSharedPreferences(getString(R.string.preference_file_name), AppCompatActivity.MODE_PRIVATE)
        val initChecked = initData.getBoolean(APP_NEED_PASSCODE, false)
        lock_switch.isChecked = initChecked
        setVisibility(initChecked)

        // トグルが押下された（パスコードを使うかどうかを変更した）ら、その情報をPreferenceに保存する。
        lock_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            val data = getSharedPreferences(getString(R.string.preference_file_name), AppCompatActivity.MODE_PRIVATE)
            val editor = data.edit()
            editor.putBoolean(APP_NEED_PASSCODE, isChecked)
            editor.apply()

            setVisibility(isChecked)
        }

        // パスコードを入力パネルのソフトウェアキーボードの「決定」ボタンを押したときの操作。
        // 入力されたのが４桁ならパスコードを設定する。
        // 参考：https://developer.android.com/training/keyboard-input/style#Action
        passcode_panel.setOnEditorActionListener { textView, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handled = true
                myLogging("textView.text = "+ textView.text)
            }
            handled
        }
    }

    /**
     * Viewの要素が見えるかどうか(visibility)をセットする。
     */
    fun setVisibility(isChecked: Boolean) {
        val visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
        navigate_message.visibility = visibility
        passcode_panel.visibility = visibility
    }

    /**
     * Toolbarのボタンが押されたときに呼ばれるメソッド
     */
    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        // 戻るボタン
        // ※R.id.homeは自分が作ったものなので反応しない。android.R.id.homeはAndroid SDKのもの
        android.R.id.home -> {
            val mIntent = Intent(this, MainStackActivity::class.java)
            moveToAnotherPage(mIntent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}