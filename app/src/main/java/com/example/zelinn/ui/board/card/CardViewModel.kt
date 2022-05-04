package com.example.zelinn.ui.board.card

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cesarferreira.tempo.Tempo
import com.example.zelinn.classes.CardDateTime
import com.example.zelinn.classes.ListModel
import com.example.zelinn.classes.MemberModel
import java.util.*

class CardViewModel: ViewModel() {
    private val _list = MutableLiveData<String>().apply {
        this.value = ""
    }
    private val _name = MutableLiveData<String>().apply {
        this.value = ""
    }
    private val _start = MutableLiveData<CardDateTime>()
    private val _due = MutableLiveData<CardDateTime>()
    private val _participants = MutableLiveData<List<MemberModel>>()
    private val _description = MutableLiveData<String>().apply {
        this.value = ""
    }
    private val _changed = MutableLiveData<Int>()

    val list: LiveData<String> = _list
    val name: LiveData<String> = _name
    val start: LiveData<CardDateTime> = _start
    val due: LiveData<CardDateTime> = _due
    val participants: LiveData<List<MemberModel>> = _participants
    val description: LiveData<String> = _description
    val changed: LiveData<Int> = _changed

    private fun updateChanged() {
        val random = (0..10).random()

        if (random != _changed.value) _changed.value = random
    }

    fun setName(name: String) {
        _name.value = name
        updateChanged()
    }

    fun setStartDate(year: Int, month: Int, date: Int) {
        val newStart = CardDateTime(year, month, date, _start.value!!.hour, _start.value!!.minute)

        _start.value = newStart
        updateChanged()
    }

    fun setStartTime(hour: Int, minute: Int) {
        val newStart = CardDateTime(_start.value!!.year, _start.value!!.month, _start.value!!.date, hour, minute)

        _start.value = newStart
        updateChanged()
    }

    fun setDueDate(year: Int, month: Int, date: Int) {
        val newDue = CardDateTime(year, month, date, _due.value!!.hour, _due.value!!.minute)

        _due.value = newDue
        updateChanged()
    }

    fun setDueTime(hour: Int, minute: Int) {
        val newDue = CardDateTime(_due.value!!.year, _due.value!!.month, _due.value!!.date, hour, minute)

        _due.value = newDue
        updateChanged()
    }

    fun setParticipants(participants: List<MemberModel>) {
        _participants.value = participants
        updateChanged()
    }

    fun setDescription(description: String) {
        _description.value = description
        updateChanged()
    }

    fun reset(list: ListModel) {
        _list.value = list.id
        _name.value = ""
        _start.value = CardDateTime(null, null, null, null, null)
        _due.value = CardDateTime(null, null, null, null, null)
        _participants.value = emptyList()
        _description.value = ""
    }
}