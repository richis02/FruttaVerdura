package com.example.prova_progetto.db

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FruitVegApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { FruitListRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { FruitListRepository(database.fruitVegDao(), database.itemsListDao(), database.listFruitCrossRefDao()) }
}