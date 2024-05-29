package com.example.prova_progetto.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class FruitListRepository(
    private val fruitVegDao: FruitVegetableDao,
    private val itemsListDao: ItemsListDao,
    private val listFruitCrossRefDao: ListFruitCrossRefDao
) {

    val allFruitVegNames: Flow<List<String>> = fruitVegDao.getFruitNames()

    val allListTitles: Flow<List<ItemsList>> = itemsListDao.getLists()

    fun getFruitVegInfoByFruitName(fruitVegKey : String): Flow<FruitVegetable> {
        return fruitVegDao.getFruitInfo(fruitVegKey)
    }

    fun getFruitQuantitiesByListId(listId: Long): Flow<List<FruitQuantity>> {
        return listFruitCrossRefDao.getFruitInfo(listId)
    }

    @WorkerThread
    suspend fun insertList(itemsList: ItemsList){
        itemsListDao.insertList(itemsList)
    }

    @WorkerThread
    suspend fun insertFruitListCrossRef(listCrossRef: ListFruitsCrossRef){
        listFruitCrossRefDao.insertFruitListCrossRef(listCrossRef)
    }

    @WorkerThread
    suspend fun deleteFruitVegInfoByFruitName(fruitVegName : String) {
        fruitVegDao.deleteFruitVeg(fruitVegName)
    }

    @WorkerThread
    suspend fun deleteItemsListById(itemsListId: Long){
        itemsListDao.deleteList(itemsListId)
    }

    @WorkerThread
    suspend fun deleteFruitListCrossRefByIds(fruitId: String, listId: Long){
        listFruitCrossRefDao.deleteFruitListCrossRef(fruitId, listId)
    }
}