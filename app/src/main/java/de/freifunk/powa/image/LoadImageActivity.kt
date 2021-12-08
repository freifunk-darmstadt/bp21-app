package de.freifunk.powa.image

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import de.freifunk.powa.R
import de.freifunk.powa.permissions.PermissionActivity


class LoadImageActivity : PermissionActivity() {

    private lateinit var showImgIv: ImageView
    private lateinit var loadImgBtn: Button

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null){
            setImageVilibility(false)
            val myBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
            showImgIv.setImageBitmap(myBitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_image)

        showImgIv = findViewById(R.id.showImgIv)
        loadImgBtn = findViewById(R.id.loadImageBtn)

        showImgIv.isInvisible = true

        loadImgBtn.setOnClickListener {
            Log.d("button", "ButtonClicked")
            checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PermissionActivity.WRITE_EXTERNAL_STORAGE_CODE)
            checkPermission(
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE)

            getContent.launch("image/*")
        }
    }

    private fun setImageVilibility(value: Boolean){
        loadImgBtn.isInvisible = value
        showImgIv.isVisible = value
    }
}