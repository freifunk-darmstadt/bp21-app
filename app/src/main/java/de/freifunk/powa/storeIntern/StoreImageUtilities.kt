package de.freifunk.powa.storeIntern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths

const val mapDir = "maps"

fun saveBitmapToInternalStorage(context: Context, filename: String, bitmap: Bitmap): Boolean {
    return try {
        val dir = File(context.filesDir, mapDir)

        dir.mkdirs()

        val fileOutputStream = FileOutputStream(File(dir, "$filename.jpg"))
        fileOutputStream.use {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it))
                throw IOException("Bitmap of file \"$filename\", couldn't be saved!")
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

fun loadListOfInternalStorageImages(context: Context): List<InternalStorageImage> {
    val fD = context.filesDir
    val maps = Paths.get(fD.path + File.separator + mapDir).toFile()
    return maps.listFiles()?.filter {
        it.canRead() && it.isFile && it.name.endsWith(".jpg")
    }?.map {
        val bytes = it.readBytes()
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        InternalStorageImage(it.name.removeSuffix(".jpg"), bitmap)
    } ?: listOf()
}

fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean {
    val fD = context.filesDir
    val file = Paths.get("${fD.path}${File.separator}$mapDir${File.separator}$filename.jpg").toFile()

    if (file.exists()) {
        return file.delete()
    }
    return true
}
