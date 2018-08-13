package com.example.kouhei.todidlist

import android.app.AlertDialog
import android.app.SearchManager
import android.arch.lifecycle.*
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.*
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DATE
import com.example.kouhei.todidlist.MyApplication.Companion.SELECTED_DIARY_ID
import kotlinx.android.synthetic.main.activity_main_stack.*


class MainStackActivity : MyAppCompatActivity() {
    private var diaryList: ArrayList<Diary> = ArrayList()
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

        // 受け取ったインテントがsearch画面からのものなら、検索を行う
        // https://developer.android.com/guide/topics/search/search-dialog#ReceivingTheQuery
        if (Intent.ACTION_SEARCH.equals(intent)) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            // TODO: search function
        }

        val manager = GridLayoutManager(this, 2)
        diary_recycler_view.layoutManager = manager

        val adapter = DiaryAdapter(this, diaryList)
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

        // たとえActivityがdestroyされても、ViewModelは保持される
        val mDiaryViewModel = ViewModelProviders.of(this).get(DiaryViewModel::class.java)

        // RecyclerViewにDiaryデータを表示する
        // ObserverにはonChanged一つしかインターフェースがないので、SAM変換によりコードを省略できる。
        val db = AppDatabase.getInstance(this)
        mDiaryViewModel.getAllDiaries(db!!.diaryDao()).observe(this, Observer<List<Diary>> { mDiaryLiveData ->
            if (mDiaryLiveData != null) {
                adapter.setDiaries(mDiaryLiveData)
                addDiary(adapter.mDiaries)

                // MainStackActivityを表示したとき、RecyclerViewが一番上に移動しているようにする
                manager.scrollToPosition(adapter.itemCount)
            }
        })
    }

    /**
     * RecyclerViewに表示するべき要素をdiaryTextListに追加する
     */
    private fun addDiary(diaryList: List<Diary>) {
        diaryList.forEach { diary ->
            this.diaryList.add(diary)
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
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
