package com.example.kouhei.todidlist

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData

/**
 * UIコントローラーからデータに関するロジックを切り離すために作成される。
 * 原則として、ActivityやFragmentは「データをスクリーンに描く」のが役割。
 *
 * ViewModelProviderに渡されるライフサイクルにスコープされる。
 * つまり、Activityに渡されると、それがDestroyされるまで生きてる
 */
class DiaryViewModel(application: Application): AndroidViewModel(application) {
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