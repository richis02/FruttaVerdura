package com.example.prova_progetto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "fruit_veg")
data class FruitVegetable(
    @PrimaryKey @ColumnInfo(name = "fruit_veg_id") val fruitVegId: String,
    @ColumnInfo(name = "energy_kj") val energyJoule : Double,
    @ColumnInfo(name = "energy_kcal") val energyCal : Double,
    @ColumnInfo(name = "proteins") val proteins : Double,
    @ColumnInfo(name = "carbohydrates") val carbohydrates : Double,
    @ColumnInfo(name = "lipids") val lipids : Double,
    @ColumnInfo(name = "fibre") val fibre:Double,
    @ColumnInfo(name = "img") val img:String
    )