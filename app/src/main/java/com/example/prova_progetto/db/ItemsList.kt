package com.example.prova_progetto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "itemsList")
data class ItemsList(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, //Inizializzazione necessaria altrimenti il parametro viene richiesto
    @ColumnInfo(name = "list_title") val listTitle: String
    // TODO: AGGIUNGERE DATA
)