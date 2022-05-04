package com.example.zelinn.ui.board

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zelinn.classes.*

class BoardViewModel : ViewModel() {
    private val _board = MutableLiveData<BoardModel>()
    private val _updateBoard = MutableLiveData<BoardModel>()
    private val _updateUri = MutableLiveData<Uri?>()
    private val _boardFlag = MutableLiveData<Int>().apply {
        this.value = 0
    }
    private val _updateChanged = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    private val _selectedMembers = MutableLiveData<MutableList<MemberModel>>().apply {
        this.value = mutableListOf()
    }
    private val _memberListState = MutableLiveData<Boolean>().apply {
        this.value = true
    }
    private val _memberListQuery = MutableLiveData<String>().apply {
        this.value = ""
    }
    private val _owner = MutableLiveData<UserModel>()
    private val _listFlag = MutableLiveData<Int>().apply {
        this.value = 0
    }
    private val _lists = MutableLiveData<List<ListModel>>().apply {
        this.value = emptyList()
    }
    private val _card = MutableLiveData<CardModel>()

    val board: LiveData<BoardModel> = _board
    val updateBoard: LiveData<BoardModel> = _updateBoard
    val updateUri: LiveData<Uri?> = _updateUri
    val updateChanged: LiveData<Boolean> = _updateChanged
    val selectedMembers: LiveData<MutableList<MemberModel>> = _selectedMembers
    val memberListState: LiveData<Boolean> = _memberListState
    val boardFlag: LiveData<Int> = _boardFlag
    val memberListQuery: LiveData<String> = _memberListQuery
    val owner: LiveData<UserModel> = _owner
    val listFlag: LiveData<Int> = _listFlag
    val lists: LiveData<List<ListModel>> = _lists
    val card: LiveData<CardModel> = _card

    fun setBoard(board: BoardModel) {
        _board.value = board
    }

    fun resetUpdateBoard() {
        _updateBoard.value = _board.value!!.copy()
        _updateUri.value = null
        setUpdateChanged()
    }

    fun setUpdateName(name: String) {
        _updateBoard.value?.name = name
        setUpdateChanged()
    }

    fun setUpdateThumbnail(uri: Uri) {
        _updateUri.value = uri
        setUpdateChanged()
    }

    fun setUpdatePermission(permission: String) {
        _updateBoard.value?.permission = permission
        setUpdateChanged()
    }

    fun setUpdateDescription(description: String) {
        _updateBoard.value?.description = description
        setUpdateChanged()
    }

    fun resetBoard() {
        val r = (0..100).random()

        if (r != _boardFlag.value)
            _boardFlag.value = r
    }

    fun resetList() {
        val r = (0..100).random()

        if (r != _listFlag.value)
            _listFlag.value = r
    }

    private fun setUpdateChanged() {
        val updateBoard = updateBoard.value!!
        val currentBoard = board.value!!

        _updateChanged.value =
            updateBoard.name != currentBoard.name ||
            updateBoard.permission != currentBoard.permission ||
            updateBoard.description != currentBoard.description ||
            updateBoard.owner != currentBoard.owner
            updateUri.value != null
    }

    fun resetSelectedMembers() {
        _selectedMembers.value!!.clear()
    }

    fun addMember(member: MemberModel) {
        _selectedMembers.value!!.add(member)
        _selectedMembers.value = _selectedMembers.value!!
    }

    fun removeMember(member: MemberModel) {
        _selectedMembers.value!!.remove(member)
        _selectedMembers.value = _selectedMembers.value!!
    }

    fun setMemberListState(state: Boolean) {
        if (state != _memberListState.value)
            _memberListState.value = state
    }

    fun setMemberListQuery(query: String) {
        _memberListQuery.value = query
    }

    fun setTempOwner(owner: UserModel) {
        _owner.value = owner
    }

    fun updateOwner() {
        _updateBoard.value?.owner = _owner.value!!
        setUpdateChanged()
    }

    fun setLists(lists: List<ListModel>) {
        _lists.value = lists
    }

    fun setCard(card: CardModel) {
        _card.value = card
    }
}