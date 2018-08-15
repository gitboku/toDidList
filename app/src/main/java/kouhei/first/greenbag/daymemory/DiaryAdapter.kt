package kouhei.first.greenbag.daymemory

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.diary_list_item.view.*

open class DiaryAdapter(private val context: Context, private val myDataset: ArrayList<Diary>) :
        RecyclerView.Adapter<DiaryAdapter.ViewHolder>() {

    // Cached copy of Diaries
    lateinit var mDiaries: List<Diary>
    lateinit var selectedDate: String
    var diaryId: Int? = null

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
            try {
                // CardViewがタップされたとき、「どの日記がタップされたか」はdiary_id で判別する。
                selectedDate = mDiaries[viewHolder.adapterPosition].diaryDate
                diaryId = mDiaries[viewHolder.adapterPosition].diaryId
                listener.onClick(diaryRow)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return viewHolder
    }

    // Called by RecyclerView to display the data at the specified position.
    // onCreateViewHolder で作成したリストアイテムにデータを紐づける
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // myDataset[position]をRecyclerViewの一要素に入れる
        // load()はnullableだが、Uri.parse()はnon-null
        val uriString = myDataset[position].imageUri
        val uri = if (uriString != null) Uri.parse(uriString) else null

        // 上から順に画像、日付、本文をCardViewに表示
        if (uri != null) {
            Glide.with(context).load(uri).into(holder.diaryImage)
        } else {
            holder.diaryImage.setImageResource(R.drawable.no_image_501)
        }
        holder.diaryDate.text = myDataset[position].diaryDate.shapeForStackUi()
        holder.diaryText.text = myDataset[position].diaryText.toString()
    }

    override fun getItemCount() = myDataset.size

    // データを紐づけるのは onBindViewHolder() がやる
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var diaryImage = itemView.diary_image
        var diaryDate = itemView.diary_date
        var diaryText = itemView.diary_text
    }
}