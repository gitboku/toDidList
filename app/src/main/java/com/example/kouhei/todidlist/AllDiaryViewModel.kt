package com.example.kouhei.todidlist

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

/**
 * UIコントローラーからデータに関するロジックを切り離すために作成される
 *
 * ViewModelProviderに渡されるライフサイクルにスコープされる。
 * つまり、Activityに渡されると、それがDestroyされるまで生きてる
 */
class AllDiaryViewModel: ViewModel() {
    private var mDiaryLiveData: LiveData<List<Diary>>? = null
    fun getAllDiaries(diaryDao: DiaryDao): LiveData<List<Diary>> {
        if (mDiaryLiveData == null) {
            mDiaryLiveData = diaryDao.getAllDiaries()
        }
        // !! ← `T?`を`T`にCastする
        // Castに失敗するとNullPointerExceptionを投げる
        return mDiaryLiveData!! // !!
    }
}