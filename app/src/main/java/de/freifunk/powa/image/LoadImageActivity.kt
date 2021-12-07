package de.freifunk.powa.image

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import de.freifunk.powa.R

class LoadImageActivity : AppCompatActivity() {

    private lateinit var showImgIv: ImageView
    private lateinit var loadImgBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_image)

        showImgIv = findViewById(R.id.showImgIv)
        loadImgBtn = findViewById(R.id.loadImageBtn)

        loadImgBtn.setOnClickListener {

        }
    }
}