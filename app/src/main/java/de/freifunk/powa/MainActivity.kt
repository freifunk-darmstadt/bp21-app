package de.freifunk.powa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.image.LoadImageActivity

class MainActivity : AppCompatActivity() {

    private lateinit var goToLoadImgActivityBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToLoadImgActivityBtn = findViewById(R.id.goToLoadImageActivityBtn)

        goToLoadImgActivityBtn.setOnClickListener {
            val goToLoadImageActivityIntent = Intent(this, LoadImageActivity::class.java)
            startActivity(goToLoadImageActivityIntent)
        }
    }
}
