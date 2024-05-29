package com.example.prova_progetto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "listFruitCrossRef",
    primaryKeys = ["listId","fruitId"],
    foreignKeys = [
        ForeignKey(
            entity = ItemsList::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FruitVegetable::class,
            parentColumns = ["fruitName"],
            childColumns = ["fruitId"],
            onDelete = ForeignKey.CASCADE
        ),
    ])
data class ListFruitsCrossRef (
    @ColumnInfo(name = "listId") val listId: Long,
    @ColumnInfo(name = "fruitId") val fruitId: String,
    @ColumnInfo(name = "quantity") val quantity : Int = 1

)