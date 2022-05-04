package com.example.zelinn.classes

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class CardModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("description") @Expose var description: String,
    @SerializedName("start") @Expose var start: Date?,
    @SerializedName("due") @Expose var due: Date?,
    @SerializedName("thumbnail") @Expose var thumbnail: String?,
    @SerializedName("participants") @Expose var participants: List<MemberModel>,
    @SerializedName("createdAt") @Expose var createdAt: Date,
) : Serializable {
    override fun toString(): String = "$id; $name; $description; ${start.toString()}; ${due.toString()}; $thumbnail;"
}