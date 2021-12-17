package de.freifunk.powa.image

import android.Manifest
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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

    //create ComponentActivity to load and handle loading the image
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null){
            setImageVisibility(false)

            //loads the image from the URI and stores it to the imageview
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri!!))
            showImgIv.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_image)

        showImgIv = findViewById(R.id.showImgIv)
        loadImgBtn = findViewById(R.id.loadImageBtn)

        showImgIv.isInvisible = true

        //request permissions on Button press and open system image selector
        loadImgBtn.setOnClickListener {
            checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PermissionActivity.WRITE_EXTERNAL_STORAGE_CODE)
            checkPermission(
                Manifest.permission.CAMERA,
                CAMERA_PERMISSION_CODE)

            getContent.launch("image/*")
        }
    }

    /**
     * set the visibility of the imageView and the load image button
     * @param value the value to set the visibility of the imageView to
     */
    private fun setImageVisibility(value: Boolean){
        loadImgBtn.isVisible = value
        showImgIv.isInvisible = value
    }
}