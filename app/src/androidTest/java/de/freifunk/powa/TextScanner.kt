package de.freifunk.powa

import android.content.Context

class TextScanner {
    /**
     * This Method returns all lines of a text file
     * The Testcases are in src/main/assets
     */
    fun scan(context: Context, file: String): MutableList<String> {
        val inputStream = context.assets.open(file)
        val lineList = mutableListOf<String>()
        inputStream.bufferedReader().forEachLine {
            lineList.add(it)
        }
        return lineList
    }

    /**
     * This Method split the given String at the given delimiter
     */
    fun decomposeString(str: String, del: String): Array<String> {
        return str.split(del).toTypedArray()
    }
}
