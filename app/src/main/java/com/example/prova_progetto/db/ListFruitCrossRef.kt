package com.example.prova_progetto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
@Entity(
    tableName = "list_fruit_cross_ref",
    primaryKeys = ["list_id","fruit_id"],
    foreignKeys = [
        ForeignKey(
            entity = ItemsList::class,
            parentColumns = ["items_list_id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FruitVegetable::class,
            parentColumns = ["fruit_veg_name"],
            childColumns = ["fruit_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ])
data class ListFruitsCrossRef(
    @ColumnInfo(name = "list_id") val listId: Long,
    @ColumnInfo(name = "fruit_id") val fruitId: String,
    @ColumnInfo(name = "quantity") val quantity : Int = 1
)