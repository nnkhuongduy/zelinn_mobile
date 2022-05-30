package com.example.zelinn.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zelinn.classes.ListModel

class ListViewModel: ViewModel() {
    private val _list = MutableLiveData<ListModel>()

    val list: LiveData<ListModel> = _list

    fun setList(list: ListModel) {
        _list.value = list
    }
}