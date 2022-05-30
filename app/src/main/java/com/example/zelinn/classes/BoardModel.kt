package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BoardModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("thumbnail") @Expose var thumbnail: String,
    @SerializedName("permission") @Expose var permission: String,
    @SerializedName("description") @Expose var description: String,
    @SerializedName("owner") @Expose var owner: UserModel,
    @SerializedName("members") @Expose var members: List<MemberModel>,
    @SerializedName("pending") @Expose var pending: List<MemberModel>,
    @SerializedName("faved") @Expose var faved: Boolean = false,
): Serializable {
    override fun toString(): String = "$id; $name; $thumbnail; $permission; $description; ${owner.id}; ${members.toString()}; ${pending.toString()}; $faved"
}