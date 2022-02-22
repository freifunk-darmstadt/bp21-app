package de.freifunk.powa.api

import de.freifunk.powa.model.Map
import java.io.File

abstract class ExportConsumer
/**
 * @param exportName the name of this Exporter. May be JSON or TXT
 * @param description the description that should be displayed if a Exporter is selected
 */(val exportName: String, val fileType: String, val description: String) {

    abstract fun export(file: File, maps: List<Map>)
}