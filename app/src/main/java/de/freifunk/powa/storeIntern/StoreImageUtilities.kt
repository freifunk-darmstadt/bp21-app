package de.freifunk.powa.storeIntern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.IllegalArgumentException
import java.nio.file.Paths

const val mapDir = "maps"

fun saveBitmapToInternalStorage(context: Context, filename: String, bitmap: Bitmap): Boolean {
    return try {
        val dir = File(context.filesDir, mapDir)

        dir.mkdirs()

        var filenameVar = filename
        if (!filenameVar.endsWith(".jpg")){
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

fun renameFileInInternalStorage(context: Context, filename: String, newName: String){
    val fD = context.filesDir
    var path = "${fD.path}${File.separator}$mapDir${File.separator}$filename"

    if (!path.endsWith(".jpg")){
        path = "$path.jpg"
    }
    val fileOld = Paths.get("${fD.path}${File.separator}$mapDir${File.separator}$path").toFile()

    if(!fileOld.exists()){
        return
    }

    path = "${fD.path}${File.separator}$mapDir${File.separator}$newName"

    if (!path.endsWith(".jpg")){
        path = "$path.jpg"
    }

    val fileNew = Paths.get("${fD.path}${File.separator}$mapDir${File.separator}$path").toFile()

    if (fileNew.exists()){
        throw IllegalArgumentException("File with new name already exists")
    }

    fileOld.renameTo(fileNew)
}

fun deleteFileFromInternalStorage(context: Context, filename: String): Boolean {
    val fD = context.filesDir
    var path = "${fD.path}${File.separator}$mapDir${File.separator}$filename"

    if (!path.endsWith(".jpg")){
        path = "$path.jpg"
    }

    val file = Paths.get(path).toFile()

    if (file.exists()) {
        return file.delete()
    }
    return true
}
