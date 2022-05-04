package com.example.zelinn.classes

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class UserModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("email") @Expose var email: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("phone") @Expose var phone: String,
    @SerializedName("avatar") @Expose var avatar: String?,
    @SerializedName("favBoards") @Expose var favBoards: List<String>,
): Serializable {
    override fun toString(): String = "$id; $email; $name; $phone"
}

data class MemberModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("email") @Expose var email: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("pending") @Expose var pending: Boolean,
    @SerializedName("avatar") @Expose var avatar: String?,
    var selected: Boolean = false,
    var order: Int = 0,
): Serializable {
    override fun toString(): String = "$id; $email; $name; $pending; $avatar; $selected"
}