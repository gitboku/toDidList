package com.example.kouhei.todidlist

/**
 * Created by kouhei on 3/11/2018.
 */

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface DiaryDao{
    // Data Access Object
    // ユーザはこのインターフェースを用いてDBの操作を行う
    // このクラスがDAOなのではなく、@DaoアノテーションをつけることでRoomが自動的にDAOを生成する

    @Query("SELECT * FROM diary")
    fun getAll(): List<Diary>

    @Query("SELECT * FROM diary WHERE calendar_date = :selectDate")
    fun getEntityFromDate(selectDate: Int): Diary

    @Insert
    fun insert(diary: Diary)

    @Delete
    fun delete(diary: Diary)

    // TODO: update diary with timestamp
}