package de.freifunk.powa

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.api.ExportConsumerJSON
import de.freifunk.powa.api.PowaApi
import de.freifunk.powa.image.LoadImageActivity
import de.freifunk.powa.image.MapListActivity
import de.freifunk.powa.permissions.GeneralPermissionRequestCode
import de.freifunk.powa.permissions.LOCATION_STRING_SEPARATOR
import de.freifunk.powa.permissions.PERMISSIONS
import de.freifunk.powa.permissions.getGpsLocation
import de.freifunk.powa.permissions.locationToString
import de.freifunk.powa.permissions.requestAllPermissions
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
            var gpsLocation = getGpsLocation(this) { location ->
                var coords = locationToString(location).split(LOCATION_STRING_SEPARATOR).toTypedArray()
                var longitude = coords[0]
                var latitide = coords[1]
                var gpsScan = ScanActivity(this, outdoorName, longitude.toFloat(), latitide.toFloat(), null, 1, null)
                gpsScan.startScan()
                Toast.makeText(this, "GPS Location: " + longitude.toFloat() + " " + latitide.toFloat(), Toast.LENGTH_SHORT).show()
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
