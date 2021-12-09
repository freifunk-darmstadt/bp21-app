package de.freifunk.powa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import de.freifunk.powa.image.LoadImageActivity
import de.freifunk.powa.scan.SingleScanActivity

class MainActivity : AppCompatActivity() {

    private lateinit var goToLoadImgActivityBtn: Button
    private lateinit var goToScanActivityBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToLoadImgActivityBtn = findViewById(R.id.goToLoadImageActivityBtn)

        goToLoadImgActivityBtn.setOnClickListener {
            val goToLoadImageActivityIntent = Intent(this, LoadImageActivity::class.java)
            startActivity(goToLoadImageActivityIntent)
        }

        goToScanActivityBtn = findViewById(R.id.goToScanActivityBtn)

        goToScanActivityBtn.setOnClickListener {
            val goToLoadImageActivityIntent = Intent(this, SingleScanActivity::class.java)
            startActivity(goToLoadImageActivityIntent)
        }
    }
}