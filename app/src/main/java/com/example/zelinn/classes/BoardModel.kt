package com.example.zelinn.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BoardModel(
    @SerializedName("_id") @Expose var id: String,
    @SerializedName("name") @Expose var name: String,
    @SerializedName("image") @Expose var image: String,
)