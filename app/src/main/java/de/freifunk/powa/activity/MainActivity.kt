package de.freifunk.powa.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.freifunk.powa.R
import de.freifunk.powa.api.PowaApi
import de.freifunk.powa.api.exporter.ExportConsumerJSON
import de.freifunk.powa.permissions.GeneralPermissionRequestCode
import de.freifunk.powa.permissions.LOCATION_STRING_SEPARATOR
import de.freifunk.powa.permissions.PERMISSIONS
import de.freifunk.powa.permissions.getGpsLocation
import de.freifunk.powa.permissions.locationToString
import de.freifunk.powa.permissions.requestAllPermissions
import de.freifunk.powa.utils.ScanWrapper
import kotlinx.android.synthetic.main.activity_load_image.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val OUTDOOR_MAP_NAME = "Outdoormap_Scan_Collection"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.goToLoadImageActivityBtn).setOnClickListener {
            startActivity(Intent(this, LoadImageActivity::class.java))
        }


        findViewById<Button>(R.id.goToScanActivityBtn).setOnClickListener {
            getGpsLocation(this) { location ->
                var coords =
                    locationToString(location).split(LOCATION_STRING_SEPARATOR).toTypedArray()
                var longitude = coords[0].toFloat()
                var latitude = coords[1].toFloat()

                var gpsScan = ScanWrapper(this,
                    Companion.OUTDOOR_MAP_NAME, null, null, null, 1, null)
                gpsScan.updateLocation(longitude, latitude)
                gpsScan.startScan()
            }
        }

        findViewById<Button>(R.id.mainToListBtn).setOnClickListener {
            startActivity(Intent(this, MapListActivity::class.java))
        }

        val api = PowaApi.getInstance(this)
        api.registerExporter(ExportConsumerJSON())
        val selectExporter = api.registerSelectExporter(this) {
            api.shareData(this, api.exportData(this, it))
        }

        findViewById<Button>(R.id.exportBtn).setOnClickListener {
            selectExporter.launch(Unit)
        }

        requestAllPermissions(this@MainActivity)

        findViewById<Button>(R.id.goToSettingsBtn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
