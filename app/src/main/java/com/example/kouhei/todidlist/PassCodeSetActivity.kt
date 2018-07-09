package com.example.kouhei.todidlist

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pass_code_set.*

class PassCodeSetActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_code_set)

        // 「キャンセル」ボタン。MainStackActivityにもどる。
        cancel_button.setOnClickListener {
            val intent = Intent(this, MainStackActivity::class.java)
            moveToAnotherPage(intent)
        }
    }
}
