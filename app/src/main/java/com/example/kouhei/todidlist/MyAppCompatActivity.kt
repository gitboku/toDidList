package com.example.kouhei.todidlist

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity

open class MyAppCompatActivity: AppCompatActivity() {


    /**
     * 他のActivityに遷移するメソッド
     * intent: Intent
     * 一つ目のコンストラクタはContext。ActivityはContextのサブクラスなのでthisを使う
     * 二つ目はIntentが送られるアプリコンポーネントのClass（開始されるActivity）
     */
    fun moveToAnotherPage(intent: Intent){
        startActivity(intent)
    }
}