package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FruitVegetableDao {
    //TODO: FILTRO SULLA DATA
    @Query("SELECT * FROM fruit_table WHERE fruitName = :fruitKey")
    fun getFruitInfo(fruitKey: String): Flow<FruitVegetable>

    @Query("SELECT fruitName FROM fruit_table")
    fun getFruitNames(): Flow<List<String>>

    // La chiave esterna è CASCADE quindi elimina entry dalla cross table
    @Query("DELETE FROM fruit_table WHERE fruitName = :id")
    suspend fun deleteFruitVeg(id: String)
}