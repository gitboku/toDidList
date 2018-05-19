package com.example.kouhei.todidlist

import android.arch.lifecycle.*
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main_stack.*

class MainStackActivity : MyAppCompatActivity() {
    private var diaryTextList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_stack)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(main_stack_page_toolbar)
        val db = AppDatabase.getInstance(this)

        // layoutManagerを登録
        diary_recycler_view.layoutManager = LinearLayoutManager(this)
        // Adapterを登録
        diary_recycler_view.adapter = DiaryAdapter(diaryTextList)

        // abstract ItemDecorationを継承したクラス(この場合はDividerItemDecoration)で、Decoratorを作成する
        diary_recycler_view.addItemDecoration(DividerItemDecoration(diary_recycler_view.context, LinearLayoutManager(this).orientation))

        // UIコントローラーにデータの扱いを書くと設計としてよくないので、データを管理する専門のクラスにデータ管理を任せる
        val mModel = ViewModelProviders.of(this).get(AllDiaryViewModel::class.java)

        // Create the observer which updates the UI.
        // ObserverにはonChange一つしかインターフェースがないので、SAM変換によりコードを省略できる。
        mModel.getAllDiaries(db!!.diaryDao()).observe(this, Observer<List<Diary>> {
            mDiaryLiveData -> addDiary(mDiaryLiveData)
        })

    }

    /**
     * RecyclerViewに表示するべき要素をdiaryTextListに追加する
     *
     * TODO: 日記本文がリストに表示されない場合がある
     */
    private fun addDiary(diaryList: List<Diary>?) {
        diaryList?.forEach { diary ->
            diaryTextList.add(diary.diaryText.toString())
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_move_to_calendar_page -> {
            val intent = Intent(this, MainActivity::class.java)
            moveToAnotherPage(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_stack, menu)
        return true
    }
}
