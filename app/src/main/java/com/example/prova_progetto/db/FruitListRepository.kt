package com.example.prova_progetto.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class FruitListRepository(
    private val fruitVegDao: FruitVegetableDao,
    private val itemsListDao: ItemsListDao,
    private val listFruitCrossRefDao: ListFruitCrossRefDao
) {

    val allFruitVeg: Flow<List<FruitVegetable>> = fruitVegDao.getAllFruitVeg()

    val allList: Flow<List<ItemsList>> = itemsListDao.getLists()

    fun getFruitVegById(fruitVegId : Long): Flow<FruitVegetable> {
        return fruitVegDao.getFruitvegById(fruitVegId)
    }

    fun getFruitVegByListId(listId: Long): Flow<List<ListFruitsCrossRef>> {
        return listFruitCrossRefDao.getFruitInfoByListId(listId)
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
    suspend fun deleteFruitVegById(fruitVegId : Long) {
        fruitVegDao.deleteFruitVegById(fruitVegId)
    }

    @WorkerThread
    suspend fun deleteItemsListById(itemsListId: Long){
        itemsListDao.deleteListById(itemsListId)
    }

    @WorkerThread
    suspend fun deleteFruitListCrossRefByIds(fruitId: String, listId: Long){
        listFruitCrossRefDao.deleteFruitListCrossRef(fruitId, listId)
    }

    @WorkerThread
    suspend fun deleteAllItemsList(){
        itemsListDao.deleteAllLists()
    }

    @WorkerThread
    suspend fun deleteAllFruitVeg(){
        fruitVegDao.deleteAllFruitVeg()
    }

    @WorkerThread
    suspend fun deleteAlListFruitCrossRef(){
        listFruitCrossRefDao.deleteAllFruitListCrossRef()
    }
}