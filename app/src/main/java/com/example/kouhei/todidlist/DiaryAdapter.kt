package com.example.kouhei.todidlist

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.diary_list_item.view.*

open class DiaryAdapter(private val myDataset: ArrayList<Diary>) :
        RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    // Cached copy of Diaries
    lateinit var mDiaries: List<Diary>
    var selectedDate: Int = 0

    lateinit var listener: View.OnClickListener

    fun setDiaries(diaryList: List<Diary>) {
        mDiaries = diaryList
        notifyDataSetChanged()
    }

    // 参考：https://qiita.com/so-ma1221/items/d1b84bf764bf82fe1ac3
    // このメソッドがMainStackActivityから呼ばれる
    fun setOnItemClickListener(listener: View.OnClickListener) {
        this.listener = listener
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryAdapter.ViewHolder {
        val diaryRow = LayoutInflater.from(parent.context)
                .inflate(R.layout.diary_list_item, parent, false)

        val viewHolder = ViewHolder(diaryRow)

        diaryRow.setOnClickListener {
            selectedDate = mDiaries[viewHolder.adapterPosition].calendarDate
            listener.onClick(diaryRow)
        }

        return viewHolder
    }

    // Called by RecyclerView to display the data at the specified position.
    // onCreateViewHolder で作成したリストアイテムにデータを紐づける
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // myDataset[position]をRecyclerViewの一要素に入れる
        holder.diaryDate.text = myDataset[position].calendarDate.toString().shapeForStackUi()
        holder.diaryText.text = myDataset[position].diaryText.toString()
    }

    override fun getItemCount() = myDataset.size

    // データを紐づけるのは onBindViewHolder() がやる
    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var diaryDate = itemView?.diary_date as TextView
        var diaryText = itemView?.diary_text as TextView
    }
}