package com.example.kouhei.todidlist

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface DiaryDao{
    // Data Access Object
    // ユーザはこのインターフェースを用いてDBの操作を行う
    // このクラスがDAOなのではなく、@DaoアノテーションをつけることでRoomが自動的にDAOを生成する

    // List<T>を返すこともできるが、LiveData<>でラップすることにより通知を得ることができる
    @Query("SELECT * FROM diary ORDER BY diary_date DESC")
    fun getAllDiaries(): LiveData<List<Diary>>

    /**
     * MainActivityで選択してる日付のEntityを取得
     */
    @Query("SELECT * FROM diary WHERE diary_date = :selectDate")
    fun getEntityWithDate(selectDate: String): Diary?

    @Query("UPDATE diary SET diary_text = :diaryText WHERE diary_date = :diaryDate")
    fun updateDiaryWithDate(diaryText: String, diaryDate: String)

    @Update
    fun update(diary: Diary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: Diary)

    @Delete
    fun delete(diary: Diary)
}