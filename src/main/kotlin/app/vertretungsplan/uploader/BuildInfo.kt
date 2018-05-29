package app.vertretungsplan.uploader

import java.io.FileInputStream
import java.util.*

class BuildInfo {
    val properties: Properties = Properties().apply {
        load(javaClass.getResourceAsStream("../../buildInfo.properties"))
    }

    val version: String
        get() = properties.getProperty("version")

    val release: Boolean
        get() = properties.getProperty("release") == "true"
}
