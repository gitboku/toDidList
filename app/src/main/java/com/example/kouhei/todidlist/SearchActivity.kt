package com.example.kouhei.todidlist

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_search.*
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DATE

class SearchActivity : MyAppCompatActivity() {

    private var nowTimeStamp = 0
    private var diaryList: ArrayList<Diary> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(search_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        nowTimeStamp = intent.getIntExtra(SELECTED_DATE, 0)

        val manager = GridLayoutManager(this, 2)
        search_recycler_view.layoutManager = manager

        val adapter = DiaryAdapter(this, diaryList)
        search_recycler_view.adapter = adapter

        search_recycler_view.addItemDecoration(GridSpacingItemDecoration(2, dpToPx(10), true))
        search_recycler_view.itemAnimator = DefaultItemAnimator()

        // 参考：https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
        // DiaryAdapterで定義したsetOnItemClickListener()を呼ぶ
        adapter.setOnItemClickListener(View.OnClickListener {
            val intent = Intent(applicationContext, EditDiaryActivity::class.java)
            val year  = adapter.selectedDate.substring(0, 4).toInt()
            val month = adapter.selectedDate.substring(4, 6).toInt() - 1 // monthはなぜか[0-11]
            val day   = adapter.selectedDate.substring(6, 8).toInt()
            intent.putExtra(SELECTED_DATE, getCalendarTimeStamp(year, month, day))
            intent.putExtra(MyApplication.SELECTED_DIARY_ID, adapter.diaryId)
            moveToAnotherPage(intent)
        })

        // 受け取ったインテントがsearch画面からのものなら、検索を行う
        // https://developer.android.com/guide/topics/search/search-dialog#ReceivingTheQuery
        if (Intent.ACTION_SEARCH.equals(intent)) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            // TODO: search function
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainStackActivity::class.java)
                intent.putExtra(SELECTED_DATE, nowTimeStamp) // 受け取ったintentをそのまま返す
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
        menuInflater.inflate(R.menu.menu_search, menu)

        // 検索フォームの設定
        // https://developer.android.com/guide/topics/search/search-dialog#ConfiguringWidget
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search_menu_search_view).actionView as SearchView

        // setSearchableInfo()を呼び出して、それをSearchableInfoに渡すことで、SearchViewを実行可能にする
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false) // 検索フォームをアイコン化してはならない
        return true
    }
}
