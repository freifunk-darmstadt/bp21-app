package de.freifunk.powa.storeIntern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.set
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.Comparator

class StoreMapTests {

    lateinit var thisContext: Context
    fun Bitmap.equals(bitmap2: Bitmap): Boolean {
        val buffer1: ByteBuffer = ByteBuffer.allocate(this.height * this.rowBytes)
        this.copyPixelsToBuffer(buffer1)
        val buffer2: ByteBuffer = ByteBuffer.allocate(bitmap2.height * bitmap2.rowBytes)
        bitmap2.copyPixelsToBuffer(buffer2)
        return Arrays.equals(buffer1.array(), buffer2.array())
    }
    @Before
    fun setup() {
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        Files.walk(Paths.get(thisContext.filesDir.path+ File.separator+ mapDir)).sorted(Comparator.reverseOrder()).map(
            Path::toFile).forEach(File::delete)
    }
    @After
    fun cleanup() {
        Files.walk(Paths.get(thisContext.filesDir.path+ File.separator+ mapDir)).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete)
    }

    @Test
    fun testSaveBitmapToInternalStorage() {
        val fileName = "testBitmap"
        val file = Paths.get(thisContext.filesDir.path+ File.separator+ mapDir+File.separator+fileName+".jpg").toFile()
        val b1 = createBitmap()


        saveBitmapToInternalStorage(thisContext, fileName, b1)
        assertTrue(
            file.exists()
        )

        val bytes = file.readBytes()
        val b2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        assertEquals(b1, b2)
    }

    private fun createBitmap(): Bitmap {
        val bm = Bitmap.createBitmap(8, 8, Bitmap.Config.RGB_565)
        for (x in 0..7) {
            for (y in 0..7){
                bm.set(x,y, x+y)
            }
        }
        return bm
    }

    @Test
    fun testLoadListOfInternalStorageImages() {
        val fn0 = "testBitmap0"
        val b0 = createBitmap()
        saveBitmapToInternalStorage(thisContext, fn0, b0)

        var fl = loadListOfInternalStorageImages(thisContext)
        assertEquals(1, fl.size)
        assertEquals(fn0, fl[0].name)
        assertEquals(b0, fl[0].bitmap)

        val fn1 = "testBitmap1"
        val b1 = createBitmap()
        saveBitmapToInternalStorage(thisContext, fn1, b1)

        fl = loadListOfInternalStorageImages(thisContext)
        assertEquals(2, fl.size)
        fl.sortedBy { it.name }

        assertEquals(fn0, fl[0].name)
        assertEquals(b0, fl[0].bitmap)
        assertEquals(fn1, fl[1].name)
        assertEquals(b1, fl[1].bitmap)
    }

    @Test
    fun testDeleteFileFromInternalStorage() {
        val fileName = "testBitmap"
        val file = Paths.get(thisContext.filesDir.path+ File.separator+ mapDir+File.separator+fileName+".jpg").toFile()
        val b1 = createBitmap()


        saveBitmapToInternalStorage(thisContext, fileName, b1)
        assertTrue(
            file.exists()
        )

        deleteFileFromInternalStorage(thisContext, fileName)
        assertFalse(
            file.exists()
        )
    }

}