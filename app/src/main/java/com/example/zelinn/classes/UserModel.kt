package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class UserModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("email") @Expose var email: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("phone") @Expose var phone: String,
    @SerializedName("birth") @Expose var birth: Date?,
): Serializable {
    override fun toString(): String = "$id; $email; $name; $phone"
}