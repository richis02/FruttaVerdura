package com.example.prova_progetto.db

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FruitVegApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { FruitListRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { FruitListRepository(database.fruitVegDao(), database.itemsListDao(), database.listFruitCrossRefDao()) }
}