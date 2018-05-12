package com.example.kouhei.todidlist

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class DiaryAdapter(private val myDataset: ArrayList<String>) :
        RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context)
                .inflate(R.layout.diary_list_item, parent, false) as TextView

        val viewHolder = ViewHolder(textView)

        textView.setOnClickListener {
            val position = viewHolder.adapterPosition
            Log.d("myTag", "this position is " + position)
        }
        /**
         * １OK：ViewHolderをクリックしたとき実行されるリスナーをセットする
         * ２OK：ViewHolderにLiveDataを表示する
         * 　　　１OK：LiveData<>のインスタンスをViewModelに作る
         * 　　　２OK：MainStackActivityにonChanged()を定義してObserverオブジェクトにする
         * 　　　３OK：observe()を使用してObserverオブジェクトをLiveDataオブジェクトにアタッチする
         * ３：日付情報をViewHolderに持たせる
         * ４：IntentをmoveToAnotherPage()に渡す
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