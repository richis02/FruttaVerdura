package com.example.prova_progetto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "fruit_veg",
    indices = [Index(value = ["fruit_veg_name"], unique = true)] //serve perchè usiamo il nome come chiave esterna quindi non deve essere nullo
)
data class FruitVegetable(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "fruit_veg_id") val fruitVegId: Int = 0, //attenzione non usato, serve solo per leggere correttamente il file csv
    @ColumnInfo(name = "fruit_veg_name") val fruitVegName: String,
    @ColumnInfo(name = "energy_kj") val energyJoule : Double,
    @ColumnInfo(name = "energy_kcal") val energyCal : Double,
    @ColumnInfo(name = "proteins") val proteins : Double,
    @ColumnInfo(name = "carbohydrates") val carbohydrates : Double,
    @ColumnInfo(name = "lipids") val lipids : Double,
    @ColumnInfo(name = "fibre") val fibre:Double,
    @ColumnInfo(name = "img") val img:String,
    @ColumnInfo(name = "photo") val photo:String
    )