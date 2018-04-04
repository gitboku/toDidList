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
    fun getEntityFromDate(selectDate: Int): Diary

    @Update
    fun update(diary: Diary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: Diary)

    @Delete
    fun delete(diary: Diary)
}