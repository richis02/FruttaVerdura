package com.example.prova_progetto.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FruitVegViewModel(private val repository: FruitListRepository) : ViewModel() {

    // Variabili membro LiveData fanno da cache e vengono aggiornate in caso di cambiamento
    val allFruitVeg: LiveData<List<FruitVegetable>> = repository.allFruitVeg.asLiveData()

    val allList: LiveData<List<ItemsList>> = repository.allList.asLiveData()

    val allFruitVegNames: LiveData<List<String>> = repository.allFruitVegNames.asLiveData()

    fun getFruitVeg(fruitVegId : String): LiveData<FruitVegetable> {
        return repository.getFruitVegById(fruitVegId).asLiveData()
    }

    fun getAllFruitsVegOfList(listId : Long): LiveData<List<FruitVegInfo>> {
        return repository.getFruitVegByListId(listId).asLiveData()
    }

    private val _filterText = MutableLiveData<String>()
    private val filteredFruitVeg: LiveData<List<FruitVegetable>> = _filterText.switchMap { text ->
        repository.getFilteredFruitVeg(text).asLiveData()
    }

    fun setFilterText(text: String) {
        _filterText.value = text
    }

    fun getFilteredFruitVeg(text: String): LiveData<List<FruitVegetable>> {
        setFilterText(text)
        return filteredFruitVeg
    }

    fun insertFruitVeg(fruitVeg: FruitVegetable) = viewModelScope.launch {
        repository.insertFruitVeg(fruitVeg)
    }

    fun insertList(itemsList: ItemsList) = viewModelScope.launch {
        repository.insertList(itemsList)
    }

    fun insertFruitListCrossRef(listCrossRef: ListFruitsCrossRef) = viewModelScope.launch {
        repository.insertOrUpdateListFruitCrossRef(listCrossRef)
    }

    fun deleteFruitVeg(fruitVegId : String) = viewModelScope.launch {
        repository.deleteFruitVegById(fruitVegId)
    }

    fun deleteItemList(itemsListId: Long) = viewModelScope.launch {
        repository.deleteItemsListById(itemsListId)
    }

    fun deleteFruitListCrossRef(fruitId: String, listId: Long) = viewModelScope.launch {
        repository.deleteFruitListCrossRefByIds(fruitId, listId)
    }

    fun deleteAllFruitVeg() = viewModelScope.launch {
        repository.deleteAllFruitVeg()
    }

    fun deleteAllItemList() = viewModelScope.launch {
        repository.deleteAllItemsList()
    }

    fun deleteAllFruitListCrossRef() = viewModelScope.launch {
        repository.deleteAlListFruitCrossRef()
    }

    fun updateQuantity(fruitId: String, listId: Long, quantity: Int) = viewModelScope.launch {
        repository.updateQuantity(fruitId, listId, quantity)
    }

    fun updateListTitle(itemsListId: Long, newTitleList: String) = viewModelScope.launch {
        repository.updateListTitleById(itemsListId, newTitleList)
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