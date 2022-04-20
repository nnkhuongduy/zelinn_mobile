package com.example.zelinn.classes

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CardModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("due") @Expose var due: String,
    @SerializedName("thumbnail") @Expose @Nullable var thumbnail: String?,
) : Serializable {
    override fun toString(): String = "$id; $name; ${due.toString()}; $thumbnail;"
}