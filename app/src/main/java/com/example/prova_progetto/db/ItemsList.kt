package com.example.prova_progetto.db

import android.text.Editable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_list")
data class ItemsList(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "items_list_id") val itemsListId: Long = 0, //Inizializzazione necessaria altrimenti il parametro viene richiesto
    @ColumnInfo(name = "list_title") val listTitle: String
    // TODO: AGGIUNGERE DATA
)