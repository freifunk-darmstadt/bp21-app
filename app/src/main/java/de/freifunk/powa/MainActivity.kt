package de.freifunk.powa

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.image.LoadImageActivity
import de.freifunk.powa.image.MapListActivity
import de.freifunk.powa.permissions.* //ktlint-disable no-wildcard-imports
import de.freifunk.powa.scan.ScanActivity
import de.freifunk.powa.settings.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var goToLoadImgActivityBtn: Button
    private lateinit var goToScanActivityBtn: Button
    private var outdoorName = "Outdoormap_Scan_Collection"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val api = PowaApi.getInstance(this)
        api.registerExporter(ExportConsumerJSON())
        api.selectExporter(this) {
            api.exportData(this, consumer = it).readLines().forEach {
                Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
            }
            api.shareData(this, api.exportData(this, consumer = it))
        }*/

        setContentView(R.layout.activity_main)

        goToLoadImgActivityBtn = findViewById(R.id.goToLoadImageActivityBtn)

        goToLoadImgActivityBtn.setOnClickListener {
            startActivity(Intent(this, LoadImageActivity::class.java))
        }

        goToScanActivityBtn = findViewById(R.id.goToScanActivityBtn)

        goToScanActivityBtn.setOnClickListener {
            getGpsLocation(this) { location ->
                var coords =
                    locationToString(location).split(LOCATION_STRING_SEPARATOR).toTypedArray()
                var longitude = coords[0].toFloat()
                var latitude = coords[1].toFloat()

                var gpsScan = ScanActivity(this, outdoorName, null, null, null, 1, longitude, latitude, null)
                gpsScan.startScan()
                Toast.makeText(this, "GPS Location: " + longitude + " " + latitude, Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.mainToListBtn).setOnClickListener {
            // startActivity(Intent(this, ExportActivity::class.java))
            startActivity(Intent(this, MapListActivity::class.java))
        }

        requestAllPermissions(this@MainActivity)

        findViewById<Button>(R.id.goToSettingsBtn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GeneralPermissionRequestCode) {
            for (resultIndex in grantResults.indices) {
                if (grantResults[resultIndex] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission granted: ${PERMISSIONS[resultIndex]}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Permission denied: ${PERMISSIONS[resultIndex]}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
