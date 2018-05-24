package com.example.kouhei.todidlist

import android.arch.lifecycle.*
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main_stack.*


class MainStackActivity : MyAppCompatActivity() {
    private var diaryTextList: ArrayList<String> = ArrayList()
    var nowTimeStamp = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_stack)
        setSupportActionBar(main_stack_page_toolbar) // アプリ上部のToolbarを呼び出す
        // Toolbarの色を、選択された月のテーマカラーに変更
        nowTimeStamp = intent.getLongExtra(EXTRA_DATE, nowTimeStamp)
        main_stack_page_toolbar.setBackgroundColor(getMonthColor(this, getSelectDate(nowTimeStamp).toString().substring(4, 6)))

        val adapter = DiaryAdapter(diaryTextList)
        diary_recycler_view.layoutManager = LinearLayoutManager(this)
        diary_recycler_view.adapter = adapter

        // 参考：https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
        // MainStackActivityで定義したsetOnItemClickListener()を呼ぶ
        adapter.setOnItemClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, EditDiaryActivity::class.java)
            val year  = adapter.selectedDate.toString().substring(0, 4).toInt()
            val month = adapter.selectedDate.toString().substring(4, 6).toInt() - 1 // monthはなぜか[0-11]
            val day   = adapter.selectedDate.toString().substring(6, 8).toInt()
            intent.putExtra(EXTRA_DATE, getCalendarTimeStamp(year, month, day))
            moveToAnotherPage(intent)
        })

        // abstract ItemDecorationを継承したクラス(この場合はDividerItemDecoration)で、Decoratorを作成する
        diary_recycler_view.addItemDecoration(DividerItemDecoration(diary_recycler_view.context, LinearLayoutManager(this).orientation))

        // たとえActivityがdestroyされても、ViewModelは保持される
        val mDiaryViewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)

        // RecyclerViewにDiaryデータを表示する
        // ObserverにはonChanged一つしかインターフェースがないので、SAM変換によりコードを省略できる。
        val db = AppDatabase.getInstance(this)
        mDiaryViewModel.getAllDiaries(db!!.diaryDao()).observe(this, Observer<List<Diary>> { mDiaryLiveData ->
            if (mDiaryLiveData != null) {
                adapter.setDiaries(mDiaryLiveData)
                addDiary(adapter.mDiaries)
            }
        })
    }

    /**
     * RecyclerViewに表示するべき要素をdiaryTextListに追加する
     */
    private fun addDiary(diaryList: List<Diary>) {
        diaryList.forEach { diary ->
            diaryTextList.add(diary.diaryText.toString())
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_move_to_calendar_page -> {
            // MainActivityに戻るときは、MainPageから来たintentをそのまま返す
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_DATE, nowTimeStamp)
            intent.putExtra(FROM_CLASS, this.localClassName)
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
