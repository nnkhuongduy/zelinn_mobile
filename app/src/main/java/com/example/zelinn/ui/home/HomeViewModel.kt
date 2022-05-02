package com.example.zelinn.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zelinn.classes.BoardModel

class HomeViewModel : ViewModel() {

    private val _boards = MutableLiveData<List<BoardModel>>()
    private val _boardName = MutableLiveData<String>()
    private val _boardThumbnail = MutableLiveData<Uri?>()
    private val _boardPermission = MutableLiveData<String>()
    private val _boardsFlag = MutableLiveData<Boolean>().apply {
        this.value = true
    }
    val boards: LiveData<List<BoardModel>> = _boards
    val boardName: LiveData<String> = _boardName
    val boardThumbnail: LiveData<Uri?> = _boardThumbnail
    val boardPermission: LiveData<String> = _boardPermission
    val boardsFlag: LiveData<Boolean> = _boardsFlag

    fun setBoards(boards: List<BoardModel>) {
        _boards.value = boards
    }
    fun setBoard(thumbnail: Uri?, name: String, permission: String) {
        _boardThumbnail.value = thumbnail
        _boardName.value = name
        _boardPermission.value = permission
    }
    fun setBoardName(name: String) {
        _boardName.value = name
    }
    fun setBoardThumbnail(thumbnail: Uri) {
        _boardThumbnail.value = thumbnail
    }
    fun setBoardPermission(permission: String) {
        _boardPermission.value = permission
    }
    fun setBoardsFlag(flag: Boolean) {
        _boardsFlag.value = flag
    }
}