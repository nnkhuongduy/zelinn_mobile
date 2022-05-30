package com.example.zelinn.ui.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zelinn.classes.CardModel
import com.example.zelinn.classes.ListModel

class CardViewModel : ViewModel() {
    private val _card = MutableLiveData<CardModel>()
    private val _list = MutableLiveData<ListModel>()

    val card: LiveData<CardModel> = _card
    val list: LiveData<ListModel> = _list

    fun setCard(card: CardModel) {
        this._card.value = card
    }

    fun setList(list: ListModel) {
        this._list.value = list
    }
}