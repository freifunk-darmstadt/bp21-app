package de.freifunk.powa.storeIntern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.set
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.* // ktlint-disable no-wildcard-imports
import junit.framework.AssertionFailedError
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.* // ktlint-disable no-wildcard-imports
import kotlin.Comparator
import kotlin.io.path.exists

class StoreMapTests {

    lateinit var thisContext: Context

    @Before
    fun setup() {
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        val path = Paths.get(thisContext.filesDir.path + File.separator + mapDir)
        if (path.exists()) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete)
        }
    }
    @After
    fun cleanup() {
        val path = Paths.get(thisContext.filesDir.path + File.separator + mapDir)
        if (path.exists()) {
            Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete)
        }
    }

    @Test
    fun testSaveBitmapToInternalStorage() {
        val fileName = "testBitmap"
        val file = Paths.get(thisContext.filesDir.path + File.separator + mapDir + File.separator + fileName + ".jpg").toFile()
        val b1 = createBitmap()

        saveBitmapToInternalStorage(thisContext, fileName, b1)
        assertTrue(file.exists())

        val bytes = file.readBytes()
        val b2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        assertBitmapEquals(compressBitmap(b1), b2)
    }

    @Test
    fun testLoadListOfInternalStorageImages() {

        assertEquals(0, loadListOfInternalStorageImages(thisContext).size)

        val fn0 = "testBitmap0"
        val b0 = createBitmap()
        saveBitmapToInternalStorage(thisContext, fn0, b0)

        var fl = loadListOfInternalStorageImages(thisContext)
        assertEquals(1, fl.size)
        assertEquals(fn0, fl[0].name)
        assertBitmapEquals(compressBitmap(b0), fl[0].bitmap)

        val fn1 = "testBitmap1"
        val b1 = createBitmap()
        saveBitmapToInternalStorage(thisContext, fn1, b1)

        fl = loadListOfInternalStorageImages(thisContext)
        assertEquals(2, fl.size)
        fl = fl.sortedBy { it.name }

        assertEquals(fn0, fl[0].name)
        assertBitmapEquals(compressBitmap(b0), fl[0].bitmap)
        assertEquals(fn1, fl[1].name)
        assertBitmapEquals(compressBitmap(b1), fl[1].bitmap)
    }

    @Test
    fun testDeleteFileFromInternalStorage() {
        val fileName = "testBitmap"
        val file = Paths.get(thisContext.filesDir.path + File.separator + mapDir + File.separator + fileName + ".jpg").toFile()
        val b1 = createBitmap()

        saveBitmapToInternalStorage(thisContext, fileName, b1)
        assertTrue(file.exists())

        assertTrue(deleteFileFromInternalStorage(thisContext, fileName))
        assertFalse(file.exists())

        assertTrue(deleteFileFromInternalStorage(thisContext, fileName))
    }

    private fun createBitmap(): Bitmap {
        val bm = Bitmap.createBitmap(8, 8, Bitmap.Config.RGB_565)
        for (x in 0..7) {
            for (y in 0..7) {
                bm.set(x, y, x + y)
            }
        }
        return bm
    }

    private fun compressBitmap(bitmap: Bitmap): Bitmap {
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outStream)
        val toByteArray = outStream.toByteArray()
        val inStream: InputStream = ByteArrayInputStream(toByteArray)
        return BitmapFactory.decodeStream(inStream)
    }

    private fun assertBitmapEquals(bitmap1: Bitmap, bitmap2: Bitmap) {
        val buffer1: ByteBuffer = ByteBuffer.allocate(bitmap1.height * bitmap1.rowBytes)
        bitmap1.copyPixelsToBuffer(buffer1)
        val buffer2: ByteBuffer = ByteBuffer.allocate(bitmap2.height * bitmap2.rowBytes)
        bitmap2.copyPixelsToBuffer(buffer2)

        if (!Arrays.equals(buffer1.array(), buffer2.array())) {
            throw AssertionFailedError("Expected <${buffer1.array().map { it.toString() }.joinToString()}> but was <${buffer2.array().map { it.toString() }.joinToString()}>")
        }
    }
}
