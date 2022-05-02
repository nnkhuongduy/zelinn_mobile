package com.example.zelinn.ui.board

import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zelinn.classes.BoardModel
import com.example.zelinn.classes.RetrofitInstance
import com.example.zelinn.interfaces.PostUpdateBoardBody
import com.example.zelinn.interfaces.UploadBoardThumbnailResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel: ViewModel() {
    private val _board = MutableLiveData<BoardModel>()
    private val _updateBoard = MutableLiveData<BoardModel>()
    private val _updateUri = MutableLiveData<Uri?>()
    private val _boardFlag = MutableLiveData<Boolean>().apply {
        this.value = true
    }
    private val _updateChanged = MutableLiveData<Boolean>().apply {
        this.value = false
    }
    private val _updating = MutableLiveData<Boolean>().apply {
        this.value = false
    }

    val board: LiveData<BoardModel> = _board
    val updateBoard: LiveData<BoardModel> = _updateBoard
    val updateUri: LiveData<Uri?> = _updateUri
    val boardFlag: LiveData<Boolean> = _boardFlag
    val updateChanged: LiveData<Boolean> = _updateChanged
    val updating: LiveData<Boolean> = _updating

    fun setBoard(board: BoardModel) {
        _board.value = board
    }

    fun resetUpdateBoard() {
        _updateBoard.value = _board.value
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

    fun setBoardFlag(flag: Boolean) {
        _boardFlag.value = flag
    }

    private fun setUpdateChanged() {
        val updateBoard = updateBoard.value
        val currentBoard = board.value

        _updateChanged.value = updateBoard.toString() != currentBoard.toString() || updateUri.value != null
    }
}