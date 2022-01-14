package de.freifunk.powa.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.freifunk.powa.R

class PermissionsDemoActivity : AppCompatActivity() {

    companion object {
        //state all needed permissions here
        val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        const val GeneralPermissionRequestCode = 13
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions_demo)

        //checkAllPermissions()
        requestSelectedPermissions(checkAllPermissions())
    }



    private fun checkAllPermissions(): Array<String> {
        val notGrantedPermissions = ArrayList<String> (PERMISSIONS.size)

        //go through all needed permissions
        for (permissionIndex in PERMISSIONS.indices) {

            //check whether the permission is already granted or not
            if (ContextCompat.checkSelfPermission(this@PermissionsDemoActivity, PERMISSIONS[permissionIndex]) == PackageManager.PERMISSION_DENIED) {
                //collect all denied permissions (this function only does checking NOT requesting for permissions)
                notGrantedPermissions.add(PERMISSIONS[permissionIndex])
            } else {
                //Show that permission is granted
                Toast.makeText(
                    this@PermissionsDemoActivity,
                    "(check), Permission Granted: ${PERMISSIONS[permissionIndex]}",
                    Toast.LENGTH_SHORT).show()
            }
        }

        return notGrantedPermissions.toTypedArray()
    }


    private fun requestAllPermissions() {
        //request all needed permission
        ActivityCompat.requestPermissions(this@PermissionsDemoActivity,
            PERMISSIONS, GeneralPermissionRequestCode)
    }

    private fun requestSelectedPermissions(permissions: Array<String>) {
        if(permissions.isEmpty())
            return

        //request all selected permission
        ActivityCompat.requestPermissions(this@PermissionsDemoActivity,
            permissions, GeneralPermissionRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == GeneralPermissionRequestCode) {
            for (resultIndex in grantResults.indices) {

                if (grantResults[resultIndex] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@PermissionsDemoActivity, "Permission granted: ${PERMISSIONS[resultIndex]}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PermissionsDemoActivity, "Permission denied: ${PERMISSIONS[resultIndex]}", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


}