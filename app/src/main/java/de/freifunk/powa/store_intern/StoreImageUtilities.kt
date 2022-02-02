package de.freifunk.powa.store_intern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception

fun saveBitmapToInternalStorage(context: Context, filename: String, bitmap: Bitmap): Boolean {
    return try {
        context.openFileOutput("$filename.jpg", Context.MODE_PRIVATE).use {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it))
                throw IOException("Bitmap of file \"$filename\", couldn't be saved!")
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

suspend fun loadListOfInternalStorageImages(context: Context): List<InternalStorageImage> {
    return withContext(Dispatchers.IO) {
        context.filesDir.listFiles()?.filter {
            it.canRead() && it.isFile && it.name.endsWith(".jpg")
        }?.map {
            val bytes = it.readBytes()
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            InternalStorageImage(it.name, bitmap)
        } ?: listOf()
    }
}

fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean {
    return try {
        context.deleteFile(filename)
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
