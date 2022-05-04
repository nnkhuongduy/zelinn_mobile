package com.example.zelinn.interfaces

import com.example.zelinn.classes.MemberModel
import java.util.*

data class PostBoardBody(val name: String, val thumbnail: String, val permission: String)

data class PostEditUserBody(val name: String, val phone: String)

data class PostInviteBoardBody(val board: String, val members: List<String>)

data class PostLoginBody(val email: String)

data class PostRegisterBody(val email: String, val name: String, val phone: String)

data class PostUpdateBoardBody(
    val id: String,
    val name: String,
    val thumbnail: String,
    val permission: String,
    val owner: String,
    val description: String
)

data class PostUserFavBoardBody(val id: String)

data class PostVerifyBody(val email: String, val code: String)

data class ConfirmBoardInvitationBody(val notification: String, val result: Boolean)

data class RemoveMembersBody(val board: String, val members: List<String>)

data class CreateListBody(val board: String, val name: String, val position: Int)

data class CreateCardBody(val list: String, val name: String, val start: String?, val due: String?, val participants: List<String>?, val description: String)