package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsListDao {
    @Query("SELECT * FROM items_list ORDER BY creation_date DESC")
    fun getLists(): Flow<List<ItemsList>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(list: ItemsList)

    // La chiave esterna è CASCADE quindi elimina entry dalla cross table
    @Query("DELETE FROM items_list WHERE items_list_id = :itemsListId")
    suspend fun deleteListById(itemsListId: Long)

    @Query("DELETE FROM items_list")
    suspend fun deleteAllLists()

    @Query("UPDATE items_list SET list_title = :newTitleList WHERE items_list_id = :itemsListId")
    suspend fun updateListById(itemsListId: Long, newTitleList: String)
}