package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pass_code_set.*
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.example.kouhei.todidlist.MyApplication.Companion.APP_NEED_PASSCODE
import com.example.kouhei.todidlist.MyApplication.Companion.PASSCODE

class PassCodeSetActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code_set)

        setSupportActionBar(pass_code_set_toolbar) // アプリ上部のToolbarを呼び出す

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

            if (isChecked){
                Toast.makeText(this, getString(R.string.passcode_set_error_message), Toast.LENGTH_SHORT).show()
            }

            setVisibility(isChecked)
        }

        // パスコードを入力パネルのソフトウェアキーボードの「決定」ボタンを押したときの操作。
        // 入力されたのが４桁ならパスコードを設定する。
        // 参考：https://developer.android.com/training/keyboard-input/style#Action
        passcode_panel.setOnEditorActionListener { textView, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handled = true
                if(textView.text.length == 4) {
                    val passcodeString = textView.text.toString()
                    savePassCode(passcodeString)
                    goToMainStackActivity(passcodeString)
                } else {
                    Toast.makeText(this, getString(R.string.passcode_set_error_message), Toast.LENGTH_SHORT).show()
                }
            }
            handled
        }

        cancel_button.setOnClickListener {
            goToMainStackActivity()
        }
    }

    /**
     * パスコードをセットする。
     * パスコードは４桁の数字だが、Intにすると"0000"が"0"に変換されてしまうので、内部的にはStringで扱う。
     */
    private fun savePassCode(passcode: String) {
        val data = getSharedPreferences(getString(R.string.preference_file_name), AppCompatActivity.MODE_PRIVATE)
        val editor = data.edit()
        editor.putString(PASSCODE, passcode)
        editor.apply()
    }

    /**
     * Viewの要素が見えるかどうか(visibility)をセットする。
     */
    private fun setVisibility(isChecked: Boolean) {
        if (isChecked) {
            passcode_panel.visibility = View.VISIBLE
            cancel_button.visibility = View.INVISIBLE
        } else {
            passcode_panel.visibility = View.INVISIBLE
            cancel_button.visibility = View.VISIBLE
        }
    }

    /**
     * MainStackActivityに遷移するアクション。
     * passcodeはMainStackActivityで「パスコードを{passcode}に設定しました」というダイアログを出すのに使う。
     */
    private fun goToMainStackActivity(passcode: String? = null) {
        val mIntent = Intent(this, MainStackActivity::class.java)
        if (passcode != null) {
            mIntent.putExtra(PASSCODE, passcode)
        }
        moveToAnotherPage(mIntent)
    }
}
