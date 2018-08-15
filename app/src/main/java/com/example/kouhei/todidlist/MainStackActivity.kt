package com.example.kouhei.todidlist

import android.app.AlertDialog
import android.arch.lifecycle.*
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DATE
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DIARY_ID
import kotlinx.android.synthetic.main.activity_main_stack.*


class MainStackActivity : MyAppCompatActivity() {
    var nowTimeStamp = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_stack)
        setSupportActionBar(main_stack_page_toolbar) // アプリ上部のToolbarを呼び出す
        nowTimeStamp = intent.getLongExtra(SELECTED_DATE, nowTimeStamp)

        val passcode = intent.getStringExtra(MyApplication.PASSCODE)
        if (passcode != null) {
            val dialogMessage = "パスコードを " + passcode + " に設定しました"
            val alert = AlertDialog.Builder(this)
            alert.setMessage(dialogMessage).setPositiveButton(getString(R.string.ok), null).show()
        }

        mDiaryViewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)

        diary_recycler_view.layoutManager = manager
        diary_recycler_view.adapter = adapter

        diary_recycler_view.addItemDecoration(GridSpacingItemDecoration(2, dpToPx(10), true))
        diary_recycler_view.itemAnimator = DefaultItemAnimator()

        // 参考：https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
        // DiaryAdapterで定義したsetOnItemClickListener()を呼ぶ
        adapter.setOnItemClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, EditDiaryActivity::class.java)
            val year  = adapter.selectedDate.substring(0, 4).toInt()
            val month = adapter.selectedDate.substring(4, 6).toInt() - 1 // monthはなぜか[0-11]
            val day   = adapter.selectedDate.substring(6, 8).toInt()
            intent.putExtra(SELECTED_DATE, getCalendarTimeStamp(year, month, day))
            intent.putExtra(SELECTED_DIARY_ID, adapter.diaryId)
            moveToAnotherPage(intent)
        })

        // RecyclerViewにDiaryデータを表示する
        // ObserverにはonChanged一つしかインターフェースがないので、SAM変換によりコードを省略できる。
        val db = AppDatabase.getInstance(this)
        mDiaryViewModel?.getAllDiaries(db!!.diaryDao())?.observe(this, Observer<List<Diary>> { mDiaryLiveData ->
            if (mDiaryLiveData != null) {
                adapter.setDiaries(mDiaryLiveData)
                addDiary(adapter.mDiaries)

                // MainStackActivityを表示したとき、RecyclerViewが一番上に移動しているようにする
                manager.scrollToPosition(adapter.itemCount)
            }
        })
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // 検索ボタン
        // SearchViewのvisible <-> invisibleを行う
        R.id.search_button -> {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(SELECTED_DATE, nowTimeStamp)
            moveToAnotherPage(intent)
            true
        }

        R.id.add_diary -> {
            // 新しい日記作成のため、日記編集画面に遷移する。
            // CardViewをタップしたときはadapter.setOnItemClickListener()から遷移する
            val intent = Intent(this, EditDiaryActivity::class.java)
            intent.putExtra(SELECTED_DATE, nowTimeStamp)
            moveToAnotherPage(intent)
            true
        }

        R.id.set_passcode -> {
            val intent = Intent(this, PassCodeSetActivity::class.java)
            moveToAnotherPage(intent)
            true
        }

        R.id.about_app -> {
            val intent = Intent(this, AboutActivity::class.java)
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
