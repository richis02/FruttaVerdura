package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FruitVegetableDao {
    @Query("SELECT * FROM fruit_veg WHERE fruit_veg_name = :fruitVegId")
    fun getFruitvegById(fruitVegId: String): Flow<FruitVegetable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFruitVeg(fruitVeg: FruitVegetable)

    @Query("SELECT * FROM fruit_veg")
    fun getAllFruitVeg(): Flow<List<FruitVegetable>>

    @Query("SELECT fruit_veg_name FROM fruit_veg ORDER BY fruit_veg_id")
    fun getAllFruitVegNames(): Flow<List<String>>

    @Query("SELECT * FROM fruit_veg WHERE fruit_veg_name LIKE :text || '%'")
    fun getFilteredFruitVeg(text: String) : Flow<List<FruitVegetable>>

    // La chiave esterna è CASCADE quindi elimina entry dalla cross table
    @Query("DELETE FROM fruit_veg WHERE fruit_veg_name = :fruitVegId")
    suspend fun deleteFruitVegById(fruitVegId: String)

    @Query("DELETE FROM fruit_veg")
    suspend fun deleteAllFruitVeg()
}