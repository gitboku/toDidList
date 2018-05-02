package com.example.kouhei.todidlist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main_stack.*

class MainStackActivity : AppCompatActivity() {
    var diaryTextList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_stack)

        addDiary()

        // layoutManagerを登録
        diary_recycler_view.layoutManager = LinearLayoutManager(this)
        // Adapterを登録
        diary_recycler_view.adapter = DiaryAdapter(diaryTextList)

    }

    // TODO: search from RoomDatabase
    /**
     * RecyclerViewに表示するべき要素をdiaryTextListに追加する
     */
    fun addDiary() {
        diaryTextList.add("diary1")
        diaryTextList.add("diary2")
        diaryTextList.add("diary3")
        diaryTextList.add("diary4")
        diaryTextList.add("diary5")
        diaryTextList.add("diary6")
        diaryTextList.add("diary7")
        diaryTextList.add("diary8")
        diaryTextList.add("diary10")
        diaryTextList.add("diary11")
        diaryTextList.add("diary12")
        diaryTextList.add("diary13")
    }
}
