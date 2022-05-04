package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotificationModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("seen") @Expose var seen: Boolean,
    @SerializedName("title") @Expose var title: String,
    @SerializedName("label") @Expose var label: String,
    @SerializedName("description") @Expose var description: String,
    @SerializedName("icon") @Expose var icon: String?,
    @SerializedName("type") @Expose var type: String,
    @SerializedName("createdAt") @Expose var createdAt: String,
) : Serializable {
    override fun toString(): String = "$id; $seen; $title; $label; $description; $createdAt; $type"
}
