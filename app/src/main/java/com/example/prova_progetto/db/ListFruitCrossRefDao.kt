package com.example.prova_progetto.db

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ListFruitCrossRefDao {
    @Query("SELECT fv.*, quantity, list_id FROM list_fruit_cross_ref as lfc JOIN fruit_veg AS fv ON lfc.fruit_id = fv.fruit_veg_name WHERE list_id= :listId")
    fun getFruitInfoByListId(listId: Long): Flow<List<FruitVegInfo>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFruitListCrossRef(listCrossRef: ListFruitsCrossRef) : Long

    @Query("UPDATE list_fruit_cross_ref SET quantity = quantity + :quantity WHERE fruit_id = :fruitId AND list_id = :listId")
    suspend fun updateQuantity(fruitId: String, listId: Long, quantity: Int)

    @Query("DELETE FROM list_fruit_cross_ref WHERE fruit_id = :fruitId AND list_id = :listId")
    suspend fun deleteFruitListCrossRef(fruitId: String, listId: Long)

    @Query("DELETE FROM list_fruit_cross_ref")
    suspend fun deleteAllFruitListCrossRef()

    // Custom function per gestire l'aumento della quantità
    suspend fun insertOrUpdateFruitListCrossRef(listCrossRef: ListFruitsCrossRef) {
        val id = insertFruitListCrossRef(listCrossRef)
        if (id == -1L) { // Conflict occurred, update the quantity
            updateQuantity(listCrossRef.fruitId, listCrossRef.listId, listCrossRef.quantity)
        }
    }
}

data class FruitVegInfo (
    @Embedded val fruitVeg : FruitVegetable,
    val quantity : Int
)



