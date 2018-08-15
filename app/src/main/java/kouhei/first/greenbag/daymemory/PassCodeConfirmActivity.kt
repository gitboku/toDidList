package kouhei.first.greenbag.daymemory

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import kouhei.first.greenbag.daymemory.MyApplication.Companion.PASSCODE
import kotlinx.android.synthetic.main.activity_pass_code_confirm.*

class PassCodeConfirmActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code_confirm)

        // 「パスコードが違います」のメッセージは、初期設定では見えない。
        pass_code_wrong.visibility = View.INVISIBLE

        val passCode = intent.getStringExtra(PASSCODE)

        // パスコードを入力パネルのソフトウェアキーボードの「決定」ボタンを押したときの操作。
        // 参考：https://developer.android.com/training/keyboard-input/style#Action
        pass_code_panel.setOnEditorActionListener { textView, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handled = true
                if(textView.text.toString() == passCode) {
                    // passCode が合っていれば、MainStackActivityに飛ぶ
                    goToMainStackActivity()
                } else {
                    // 間違えていれば「パスコードが違います」のメッセージを出す。
                    pass_code_wrong.visibility = View.VISIBLE
                    // パスコードフォームの入力値も消す。
                    textView.text = null
                }
            }
            handled
        }
    }

    /**
     * MainStackActivityに遷移するアクション。
     */
    private fun goToMainStackActivity() {
        val mIntent = Intent(this, MainStackActivity::class.java)
        moveToAnotherPage(mIntent)
    }
}
