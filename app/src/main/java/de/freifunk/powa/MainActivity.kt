package de.freifunk.powa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.image.LoadImageActivity
import de.freifunk.powa.scan.scan
import de.freifunk.powa.scan.handleScanResults
import de.freifunk.powa.scan.handleScanFailure

class MainActivity : AppCompatActivity() {

    private lateinit var goToLoadImgActivityBtn: Button
    private lateinit var goToScanActivityBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        goToLoadImgActivityBtn = findViewById(R.id.goToLoadImageActivityBtn)

        goToLoadImgActivityBtn.setOnClickListener {
            startActivity(Intent(this, LoadImageActivity::class.java))
        }

        goToScanActivityBtn = findViewById(R.id.goToScanActivityBtn)

        goToScanActivityBtn.setOnClickListener {
            scan(this@MainActivity, ::handleScanResults, ::handleScanFailure)
        }


    }
}
