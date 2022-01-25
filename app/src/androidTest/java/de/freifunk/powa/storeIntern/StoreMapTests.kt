package de.freifunk.powa.storeIntern

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.graphics.set
import androidx.test.platform.app.InstrumentationRegistry
import de.freifunk.powa.TextScanner
import junit.framework.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists

class StoreMapTests {

    lateinit var thisContext: Context
    lateinit var scanner: TextScanner
    @Before
    fun setup() {
        thisContext = InstrumentationRegistry.getInstrumentation().targetContext
        Paths.get(thisContext.filesDir.path+ File.separator+ mapDir).deleteIfExists()
    }
    @After
    fun cleanup() {
        Paths.get(thisContext.filesDir.path+ File.separator+ mapDir).deleteIfExists()
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