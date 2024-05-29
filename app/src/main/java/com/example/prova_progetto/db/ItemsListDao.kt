package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsListDao {
    @Query("SELECT * FROM itemsList")
    fun getLists(): Flow<List<ItemsList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(list: ItemsList)

    // La chiave esterna è CASCADE quindi elimina entry dalla cross table
    @Query("DELETE FROM itemsList WHERE id = :id")
    suspend fun deleteList(id: Long)
}