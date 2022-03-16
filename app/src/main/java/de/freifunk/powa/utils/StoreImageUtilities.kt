package de.freifunk.powa.storeIntern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.freifunk.powa.model.InternalStorageImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.nio.file.Paths

const val mapDir = "maps"

/**
 * This function stores a Bitmap to the internal storage of the app.
 * @param bitmap the bitmap to save to internal storage
 * @param context the context this method is called in
 * @param filename the filename the image should be saved to
 */
fun saveBitmapToInternalStorage(context: Context, filename: String, bitmap: Bitmap): Boolean {
    return try {
        val dir = File(context.filesDir, mapDir)

        dir.mkdirs()

        var filenameVar = filename
        if (!filenameVar.endsWith(".jpg")) {
            filenameVar = "$filename.jpg"
        }

        val fileOutputStream = FileOutputStream(File(dir, filenameVar))
        fileOutputStream.use {
            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it))
                throw IOException("Bitmap of file \"$filenameVar\", couldn't be saved!")
        }
        true
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

/**
 * This function loads a list of Images from the internal storage and returns a List
 * @param context the context of the app to return the stored images from
 * @return a list containing all images stored for the app
 */
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

/**
 * This functions renames a image stored in the internal storage of the app
 * @param context the context of the app that is trying to rename a file
 * @param filename the old filename of the image
 * @param newName the new filename of the image
 */
fun renameFileInInternalStorage(context: Context, filename: String, newName: String) {
    val fD = context.filesDir
    var path = "${fD.path}${File.separator}$mapDir${File.separator}$filename"

    if (!path.endsWith(".jpg")) {
        path = "$path.jpg"
    }
    val fileOld = Paths.get(path).toFile()

    if (!fileOld.exists()) {
        return
    }

    path = "${fD.path}${File.separator}$mapDir${File.separator}$newName"

    if (!path.endsWith(".jpg")) {
        path = "$path.jpg"
    }

    val fileNew = Paths.get(path).toFile()

    if (fileNew.exists()) {
        throw IllegalArgumentException("File with new name already exists")
    }

    fileOld.renameTo(fileNew)
}

/**
 * This function deletes a image that is stored in the internal app storage
 * @param context the context the delete function is called from
 * @param filename the image file to delete
 */
fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean {
    val fD = context.filesDir
    var path = "${fD.path}${File.separator}$mapDir${File.separator}$filename"

    if (!path.endsWith(".jpg")) {
        path = "$path.jpg"
    }

    val file = Paths.get(path).toFile()

    if (file.exists()) {
        return file.delete()
    }
    return true
}
