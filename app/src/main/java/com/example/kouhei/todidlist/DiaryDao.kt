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
     * 日記を検索するときに使用する
     */
    @Query("SELECT * FROM diary WHERE diary_text LIKE :searchText ORDER BY diary_date DESC")
    fun searchDiaries(searchText: String): LiveData<List<Diary>>

    @Query("SELECT * FROM diary WHERE diary_id = :diaryId")
    fun selectDiary(diaryId: Int): Diary

    @Update
    fun update(diary: Diary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: Diary)

    @Delete
    fun delete(diary: Diary)
}