package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface ListFruitCrossRefDao {
    @Query("SELECT fruitId, quantity FROM listFruitCrossRef WHERE listId= :listId")
    fun getFruitInfo(listId: Long): Flow<List<FruitQuantity>>

    //TODO: gestire inserimento con il viewmodel
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFruitListCrossRef(listCrossRef: ListFruitsCrossRef)

    @Query("DELETE FROM listFruitCrossRef WHERE fruitId = :fruitId AND listId = :listId")
    suspend fun deleteFruitListCrossRef(fruitId: String, listId: Long)
}

data class FruitQuantity(
    val fruitId: Int,
    val quantity: Int
)