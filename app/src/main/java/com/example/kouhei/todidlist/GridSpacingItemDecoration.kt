package com.example.kouhei.todidlist

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View


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