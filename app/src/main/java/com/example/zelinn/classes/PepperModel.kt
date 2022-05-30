package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PepperListModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("cards") @Expose var cards: List<CardModel>,
): Serializable {
    override fun toString(): String = "$id; $name; ${cards.toString()}"
}

data class PepperModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("faved") @Expose var faved: Boolean,
    @SerializedName("lists") @Expose var lists: List<PepperListModel>,
    var expanded: Boolean = false,
): Serializable {
    override fun toString(): String = "$id; $name; ${lists.toString()}; $expanded; $faved"
}
