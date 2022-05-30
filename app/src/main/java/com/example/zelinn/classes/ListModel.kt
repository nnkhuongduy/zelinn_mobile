package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ListModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("priority") @Expose var priority: Int?,
    @SerializedName("cards") @Expose var cards: List<CardModel> = listOf(),
) : Serializable {
    override fun toString(): String = "$id; $name; $priority; $cards"
}