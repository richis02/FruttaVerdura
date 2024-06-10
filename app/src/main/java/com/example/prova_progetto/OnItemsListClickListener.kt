package com.example.prova_progetto

interface OnItemsListClickListener {
    fun onItemClick(id: Long, name: String)

    fun onUpdateTitleClick(id: Long)
}