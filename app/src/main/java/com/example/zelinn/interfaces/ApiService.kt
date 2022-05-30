package com.example.zelinn.interfaces

import com.example.zelinn.classes.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("/boards")
    fun getBoards(): Call<List<BoardModel>>

    @POST("/boards")
    fun postBoard(@Body requestBody: PostBoardBody): Call<Void>

    @GET("/boards/board")
    fun getBoard(@Query("id") id: String): Call<BoardModel>

    @POST("/boards/update")
    fun postUpdateBoard(@Body requestBody: PostUpdateBoardBody): Call<Void>

    @POST("/boards/invite")
    fun inviteToBoard(@Body requestBody: PostInviteBoardBody): Call<Void>

    @POST("/boards/members/query")
    fun queryMembersToInvite(@Body() requestBody: QueryUserInviteBody): Call<List<MemberModel>>

    @POST("/boards/invite/response")
    fun confirmBoardInvitation(@Body() requestBody: ConfirmBoardInvitationBody): Call<Void>

    @POST("/boards/members/remove")
    fun removeMembers(@Body() requestBody: RemoveMembersBody): Call<Void>

    @POST("/boards/leave")
    fun leaveBoard(@Body() requestLeaveBoardBody: LeaveBoardBody): Call<Void>

    @DELETE("/boards")
    fun deleteBoard(@Query("board") boardId: String): Call<Void>

    @POST("/auth/login")
    suspend fun login(@Body requestBody: PostLoginBody): Response<Void>

    @POST("/auth/verify")
    fun verifyUser(@Body requestBody: PostVerifyBody): Call<PostVerifyResponse>

    @GET("/auth")
    fun auth(): Call<UserModel>

    @POST("/users/register")
    suspend fun register(@Body requestBody: PostRegisterBody): Response<Void>

    @POST("/users/edit")
    fun editUser(@Body requestBody: PostEditUserBody): Call<UserModel>

    @POST("/users/fav")
    fun userFavBoard(@Body requestBody: PostUserFavBoardBody): Call<UserModel>

    @GET("/notifications")
    fun getNotifications(): Call<List<NotificationModel>>

    @Multipart
    @POST("/upload/avatar")
    fun uploadAvatarUser(@Part part: MultipartBody.Part): Call<UserModel>

    @Multipart
    @POST("/upload/thumbnail")
    fun uploadBoardThumbnail(@Part part: MultipartBody.Part): Call<UploadBoardThumbnailResponse>

    @POST("/lists")
    fun createList(@Body requestBody: CreateListBody): Call<Void>

    @GET("/lists")
    fun getLists(@Query("board") board: String): Call<List<ListModel>>

    @GET("/lists/list")
    fun getList(@Query("list") listId: String?, @Query("card") cardId: String?): Call<ListModel>

    @DELETE("/lists")
    fun deleteList(@Query("list") listId: String): Call<Void>

    @POST("/lists/update")
    fun updateList(@Body() body: UpdateListBody): Call<Void>

    @POST("/cards")
    fun createCard(@Body requestBody: CreateCardBody): Call<Void>

    @GET("/cards")
    fun getCards(@Query("board") board: String?, @Query("list") list: String?): Call<List<CardModel>>

    @GET("/cards/card")
    fun getCard(@Query("card") cardId: String): Call<CardModel>

    @DELETE("/cards")
    fun deleteCard(@Query("card") cardId: String): Call<Void>

    @POST("/cards/complete")
    fun completeCard(@Body() body: CompleteCardBody): Call<Void>

    @GET("/pepper")
    fun getPepper(@Query("date") date: String): Call<List<PepperModel>>
}