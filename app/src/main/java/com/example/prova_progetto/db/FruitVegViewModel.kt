package com.example.prova_progetto.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FruitVegViewModel(private val repository: FruitListRepository) : ViewModel() {

    // Variabili membro LiveData fanno da cache e vengono aggiornate in caso di cambiamento

    val allFruitVegNames: LiveData<List<String>> = repository.allFruitVegNames.asLiveData()

    val allListTitles: LiveData<List<ItemsList>> = repository.allListTitles.asLiveData()

    fun getFruitVegInfo(fruitVegName : String): LiveData<FruitVegetable> {
        return repository.getFruitVegInfoByFruitName(fruitVegName).asLiveData()
    }

    fun getFruitQuantities(listId : Long): LiveData<List<FruitQuantity>> {
        return repository.getFruitQuantitiesByListId(listId).asLiveData()
    }

    fun insertList(itemsList: ItemsList) = viewModelScope.launch {
        repository.insertList(itemsList)
    }

    fun insertFruitListCrossRef(listCrossRef: ListFruitsCrossRef) = viewModelScope.launch {
        repository.insertFruitListCrossRef(listCrossRef)
    }

    fun deleteFruitVegInfo(fruitVegName : String) = viewModelScope.launch {
        repository.deleteFruitVegInfoByFruitName(fruitVegName)
    }

    fun deleteItemList(itemsListId: Long) = viewModelScope.launch {
        repository.deleteItemsListById(itemsListId)
    }

    fun deleteFruitListCrossRef(fruitId: String, listId: Long) = viewModelScope.launch {
        repository.deleteFruitListCrossRefByIds(fruitId, listId)
    }
}

class FruitVegViewModelFactory(private val repository: FruitListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FruitVegViewModel::class.java)) {
            @Suppress("UNCHEKED_CAST")
            return FruitVegViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}