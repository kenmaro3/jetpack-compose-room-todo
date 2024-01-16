package com.example.roomtodo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomtodo.Todo

@Dao
interface TodoDao{
    @Query("select * from todos order by created_at asc")
    fun getAll(): MutableList<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun post(todo: Todo)

    @Delete
    fun delete(todo: Todo)
}