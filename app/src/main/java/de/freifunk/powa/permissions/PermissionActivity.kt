package de.freifunk.powa.permissions

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class PermissionActivity : AppCompatActivity() {

    companion object {
        // Used for Camera permission requests
        const val CAMERA_PERMISSION_CODE = 100
        // Used for Storage permission requests
        const val STORAGE_PERMISSION_CODE = 101
        // Used for Write External Storage permission requests
        const val WRITE_EXTERNAL_STORAGE_CODE = 102
    }

    // Function to check and request permission.
    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@PermissionActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@PermissionActivity, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this@PermissionActivity, "Permission already granted", Toast.LENGTH_SHORT).show()
            if (ContextCompat.checkSelfPermission(this@PermissionActivity, permission) ==
                PackageManager.PERMISSION_DENIED
            ) {
                // Requesting the permission
                ActivityCompat.requestPermissions(
                    this@PermissionActivity,
                    arrayOf(permission),
                    requestCode
                )
            } else {
                // Show that permission is already present
                Toast.makeText(
                    this@PermissionActivity,
                    "Permission already granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // This function is called when the user accepts or decline the permission.
        // Request Code is used to check which permission called this function.
        // This request code is provided when the user is prompt for permission.
        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == PermissionActivity.CAMERA_PERMISSION_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@PermissionActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PermissionActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
                }
            } else if (requestCode == PermissionActivity.STORAGE_PERMISSION_CODE) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@PermissionActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PermissionActivity, "Storage Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionActivity.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@PermissionActivity,
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@PermissionActivity,
                    "Camera Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == PermissionActivity.STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this@PermissionActivity,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@PermissionActivity,
                    "Storage Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
