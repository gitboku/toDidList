package com.example.kouhei.todidlist

import android.app.AlertDialog
import android.arch.lifecycle.*
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

        val manager = GridLayoutManager(this, 2)
        diary_recycler_view.layoutManager = manager

        val adapter = DiaryAdapter(diaryList)
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
            // MainActivityに戻るときは、MainPageから来たintentをそのまま返す
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
     * Decorator: マージンを設定する
     * https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
     */
    class GridSpacingItemDecoration(initSpanCount: Int, initSpacing: Int, initIncludedEdge: Boolean): RecyclerView.ItemDecoration() {
        var spanCount = initSpanCount
        var spacing = initSpacing
        var includeEdge = initIncludedEdge

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount
                if (position < spanCount) outRect.top = spacing // this is top edge
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) outRect.top = spacing // item top
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        val r = resources
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics))
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_stack, menu)
        return true
    }
}
