package de.freifunk.powa.permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import de.freifunk.powa.R

//state all needed permissions here, need to be in manifest
val PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)
const val GeneralPermissionRequestCode = 13

fun checkAllPermissions(context: Activity) {
    val notGrantedPermissions = ArrayList<String> (PERMISSIONS.size)

    //go through all needed permissions
    for (permissionIndex in PERMISSIONS.indices) {

        //check whether the permission is already granted or not
        if (ContextCompat.checkSelfPermission(context,
                PERMISSIONS[permissionIndex]) == PackageManager.PERMISSION_DENIED) {
            //collect all denied permissions
                // (this function only does checking NOT requesting for permissions)
            notGrantedPermissions.add(PERMISSIONS[permissionIndex])
        } else {
            //Show that permission is granted
            Toast.makeText(
                context,
                "(check), Permission Granted: ${PERMISSIONS[permissionIndex]}",
                Toast.LENGTH_SHORT).show()
        }
    }
    requestSelectedPermissions(context, notGrantedPermissions.toTypedArray())
}

fun checkPermissions(context: Activity, permissions: Array<String>) {
    val notGrantedPermissions = ArrayList<String> (permissions.size)

    //go through all needed permissions
    for (permissionIndex in permissions.indices) {

        //check whether the permission is already granted or not
        if (ContextCompat.checkSelfPermission(context,
                permissions[permissionIndex]) == PackageManager.PERMISSION_DENIED) {
            //collect all denied permissions
                // (this function only does checking NOT requesting for permissions)
            notGrantedPermissions.add(permissions[permissionIndex])
        } else {
            //Show that permission is granted
            Toast.makeText(
                context,
                "(check), Permission Granted: ${permissions[permissionIndex]}",
                Toast.LENGTH_SHORT).show()
        }
    }
    requestSelectedPermissions(context, notGrantedPermissions.toTypedArray())
}


fun requestAllPermissions(context: Activity) {
    //request all needed permission
    ActivityCompat.requestPermissions(context,
        PERMISSIONS, GeneralPermissionRequestCode)
}

fun requestSelectedPermissions(context: Activity, permissions: Array<String>) {
    if(permissions.isEmpty())
        return

    //request all selected permission
    ActivityCompat.requestPermissions(context,
        permissions, GeneralPermissionRequestCode)
}

//override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if(requestCode == GeneralPermissionRequestCode) {
//            for (resultIndex in grantResults.indices) {
//
//                if (grantResults[resultIndex] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this@PermissionsDemoActivity,
//                        "Permission granted: ${PERMISSIONS[resultIndex]}", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(this@PermissionsDemoActivity,
//                         "Permission denied: ${PERMISSIONS[resultIndex]}", Toast.LENGTH_SHORT).show()
//                }
//
//            }
//        }
//    }