package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface ListFruitCrossRefDao {
    @Query("SELECT * FROM list_fruit_cross_ref WHERE list_id= :listId")
    fun getFruitInfoByListId(listId: Long): Flow<List<ListFruitsCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFruitListCrossRef(listCrossRef: ListFruitsCrossRef)

    @Query("DELETE FROM list_fruit_cross_ref WHERE fruit_id = :fruitId AND list_id = :listId")
    suspend fun deleteFruitListCrossRef(fruitId: String, listId: Long)

    @Query("DELETE FROM list_fruit_cross_ref")
    suspend fun deleteAllFruitListCrossRef()
}
