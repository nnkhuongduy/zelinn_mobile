package com.example.zelinn

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.zelinn.classes.BoardModel

class CardActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        val imageView = findViewById<ImageView>(R.id.card_activity_image)
        val avaView = findViewById<ImageView>(R.id.card_activity_member_ava)
        Glide.with(this).load("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/VAN_CAT.png/1024px-VAN_CAT.png").into(imageView)
        Glide.with(this).load("https://static01.nyt.com/images/2021/09/14/science/07CAT-STRIPES/07CAT-STRIPES-mediumSquareAt3X-v2.jpg").into(avaView)
    }
}