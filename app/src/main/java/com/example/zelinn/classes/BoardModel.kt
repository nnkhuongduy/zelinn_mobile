package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BoardModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("thumbnail") @Expose var thumbnail: String,
    @SerializedName("permission") @Expose var permission: String,
): Serializable {
    override fun toString(): String = "$id; $name; $thumbnail; $permission"
}