package com.example.prova_progetto.db

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class FruitListRepository(
    private val fruitVegDao: FruitVegetableDao,
    private val itemsListDao: ItemsListDao,
    private val listFruitCrossRefDao: ListFruitCrossRefDao
) {

    val allFruitVeg: Flow<List<FruitVegetable>> = fruitVegDao.getAllFruitVeg()

    val allFruitVegNames: Flow<List<String>> = fruitVegDao.getAllFruitVegNames()

    val allList: Flow<List<ItemsList>> = itemsListDao.getLists()


    fun getFruitVegById(fruitVegId : String): Flow<FruitVegetable> {
        return fruitVegDao.getFruitvegById(fruitVegId)
    }

    fun getFruitVegByListId(listId: Long): Flow<List<FruitVegInfo>> {
        return listFruitCrossRefDao.getFruitInfoByListId(listId)
    }

    fun getFruitVegNames(): Flow<List<String>> {
        return fruitVegDao.getAllFruitVegNames()
    }

    fun getFilteredFruitVeg(text: String) : Flow<List<FruitVegetable>>{
        return fruitVegDao.getFilteredFruitVeg(text)
    }

    @WorkerThread
    suspend fun insertFruitVeg(fruitVeg: FruitVegetable){
        fruitVegDao.insertFruitVeg(fruitVeg)
    }

    @WorkerThread
    suspend fun insertList(itemsList: ItemsList){
        itemsListDao.insertList(itemsList)
    }

    @WorkerThread
    suspend fun insertOrUpdateListFruitCrossRef(listCrossRef: ListFruitsCrossRef){
        listFruitCrossRefDao.insertOrUpdateFruitListCrossRef(listCrossRef)
    }

    @WorkerThread
    suspend fun updateListTitleById(itemsListId: Long, newTitleList: String){
        itemsListDao.updateListById(itemsListId, newTitleList)
    }

    @WorkerThread
    suspend fun deleteFruitVegById(fruitVegId : String) {
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

    @WorkerThread
    suspend fun updateQuantity(fruitId: String, listId: Long, quantity: Int){
        listFruitCrossRefDao.updateQuantity(fruitId, listId, quantity)
    }
}