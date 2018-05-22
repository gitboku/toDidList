package com.example.kouhei.todidlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class DiaryAdapter(private val myDataset: ArrayList<String>) :
        RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    // Cached copy of Diaries
    lateinit var mDiaries: List<Diary>
    var nowTimeStamp: Int = 0

    fun setDiaries(diaryList: List<Diary>) {
        mDiaries = diaryList
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.diary_list_item, parent, false) as TextView

        val viewHolder = ViewHolder(textView)

        textView.setOnClickListener {
            nowTimeStamp = mDiaries[viewHolder.adapterPosition].calendarDate
        }
        /**
         * ３：MainStackActivityからEditDiaryActivityに遷移できるようにする。
         * 　　１：日付情報をViewHolderに持たせる
         * 　　２：IntentをmoveToAnotherPage()に渡す
         * ４：画像を保存できるようにする
         * ５：MainActivityで画像を表示してる時だけstatus barとToolbarを透明にする
         */
        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // myDataset[position]をRecyclerViewの一要素に入れる
        holder.textView.text = myDataset[position]
    }

    override fun getItemCount() = myDataset.size

    // RecyclerViewの一要素となるXML要素の型を引数に指定する
    // この場合はdiary_list_item.xmlのTextView
    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
}