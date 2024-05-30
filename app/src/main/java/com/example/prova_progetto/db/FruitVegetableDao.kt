package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FruitVegetableDao {
    //TODO: FILTRO SULLA DATA
    @Query("SELECT * FROM fruit_veg WHERE fruit_veg_id = :fruitVegId") //TODO: FORSE HA SENSO FARLO RISPETTO ALL'ID
    fun getFruitvegById(fruitVegId: Long): Flow<FruitVegetable>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFruitVeg(fruitVeg: FruitVegetable)

    @Query("SELECT * FROM fruit_veg")
    fun getAllFruitVeg(): Flow<List<FruitVegetable>>

    // La chiave esterna è CASCADE quindi elimina entry dalla cross table
    @Query("DELETE FROM fruit_veg WHERE fruit_veg_id = :fruitVegId")
    suspend fun deleteFruitVegById(fruitVegId: Long)

    @Query("DELETE FROM fruit_veg")
    suspend fun deleteAllFruitVeg()
}