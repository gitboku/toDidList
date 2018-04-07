package com.example.kouhei.todidlist

/**
 * Created by kouhei on 3/11/2018.
 */

import android.arch.persistence.room.*

@Dao
interface DiaryDao{
    // Data Access Object
    // ユーザはこのインターフェースを用いてDBの操作を行う
    // このクラスがDAOなのではなく、@DaoアノテーションをつけることでRoomが自動的にDAOを生成する

    @Query("SELECT * FROM diary")
    fun getAll(): List<Diary>

    @Query("SELECT * FROM diary WHERE calendar_date = :selectDate")
    fun getEntityWithDate(selectDate: Int): Diary

    @Query("UPDATE diary SET diary_text = :diaryText WHERE calendar_date = :calendarDate")
    fun updateDiaryWithDate(diaryText: String, calendarDate: Int)

    @Update
    fun update(diary: Diary)

    @Insert
    fun insert(diary: Diary)

    @Delete
    fun delete(diary: Diary)
}