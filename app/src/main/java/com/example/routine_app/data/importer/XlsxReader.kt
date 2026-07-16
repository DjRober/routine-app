package com.example.routine_app.data.importer

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream

/**
 * Lector de archivos .xlsx sin dependencias externas.
 *
 * Un .xlsx es un ZIP de XML. Usamos únicamente APIs nativas de Android
 * ([ZipInputStream] + [XmlPullParser]), evitando librerías pesadas como Apache POI.
 *
 * Devuelve cada hoja como una lista de filas, y cada fila como lista de celdas (texto).
 * Soporta cadenas compartidas (sharedStrings), cadenas en línea y números.
 */
object XlsxReader {

    /** Lee todas las hojas del archivo. Preserva el orden y el nombre de cada hoja. */
    fun read(input: InputStream): LinkedHashMap<String, List<List<String>>> {
        val entries = HashMap<String, ByteArray>()
        ZipInputStream(input).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) entries[entry.name] = zip.readBytes()
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }

        val shared = entries["xl/sharedStrings.xml"]?.let { parseSharedStrings(it) } ?: emptyList()
        val relTargets = entries["xl/_rels/workbook.xml.rels"]?.let { parseRels(it) } ?: emptyMap()
        val sheetRefs = entries["xl/workbook.xml"]?.let { parseWorkbook(it) } ?: emptyList()

        val result = LinkedHashMap<String, List<List<String>>>()
        for ((name, rid) in sheetRefs) {
            val target = relTargets[rid] ?: continue
            val path = if (target.startsWith("xl/")) target else "xl/$target"
            val bytes = entries[path] ?: entries["xl/${target.removePrefix("/xl/").removePrefix("/")}"]
            if (bytes != null) result[name] = parseSheet(bytes, shared)
        }
        return result
    }

    private fun newParser(bytes: ByteArray): XmlPullParser =
        Xml.newPullParser().apply { setInput(ByteArrayInputStream(bytes), null) }

    private fun parseSharedStrings(bytes: ByteArray): List<String> {
        val parser = newParser(bytes)
        val result = ArrayList<String>()
        var sb: StringBuilder? = null
        var inText = false
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "si" -> sb = StringBuilder()
                    "t" -> inText = true
                }
                XmlPullParser.TEXT -> if (inText) sb?.append(parser.text)
                XmlPullParser.END_TAG -> when (parser.name) {
                    "t" -> inText = false
                    "si" -> { result.add(sb?.toString() ?: ""); sb = null }
                }
            }
            event = parser.next()
        }
        return result
    }

    private fun parseWorkbook(bytes: ByteArray): List<Pair<String, String>> {
        val parser = newParser(bytes)
        val sheets = ArrayList<Pair<String, String>>()
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "sheet") {
                val name = parser.getAttributeValue(null, "name") ?: ""
                val rid = parser.getAttributeValue(null, "r:id")
                    ?: parser.getAttributeValue(null, "id") ?: ""
                sheets.add(name to rid)
            }
            event = parser.next()
        }
        return sheets
    }

    private fun parseRels(bytes: ByteArray): Map<String, String> {
        val parser = newParser(bytes)
        val map = HashMap<String, String>()
        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG && parser.name == "Relationship") {
                val id = parser.getAttributeValue(null, "Id")
                val target = parser.getAttributeValue(null, "Target")
                if (id != null && target != null) map[id] = target.removePrefix("/")
            }
            event = parser.next()
        }
        return map
    }

    private fun parseSheet(bytes: ByteArray, shared: List<String>): List<List<String>> {
        val parser = newParser(bytes)
        val rows = ArrayList<List<String>>()
        var rowCells = HashMap<Int, String>()
        var maxCol = -1

        var colIndex = 0
        var cellType: String? = null
        val value = StringBuilder()
        var inValue = false
        var inInlineText = false

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "row" -> { rowCells = HashMap(); maxCol = -1 }
                    "c" -> {
                        val ref = parser.getAttributeValue(null, "r")
                        colIndex = if (ref != null) columnIndex(ref) else colIndex + 1
                        cellType = parser.getAttributeValue(null, "t")
                        value.setLength(0)
                    }
                    "v" -> inValue = true
                    "t" -> if (cellType == "inlineStr") inInlineText = true
                }
                XmlPullParser.TEXT -> if (inValue || inInlineText) value.append(parser.text)
                XmlPullParser.END_TAG -> when (parser.name) {
                    "v" -> inValue = false
                    "t" -> inInlineText = false
                    "c" -> {
                        val raw = value.toString()
                        val resolved = when (cellType) {
                            "s" -> raw.toIntOrNull()?.let { shared.getOrNull(it) } ?: ""
                            else -> raw
                        }
                        rowCells[colIndex] = resolved
                        if (colIndex > maxCol) maxCol = colIndex
                    }
                    "row" -> {
                        val list = ArrayList<String>(maxCol + 1)
                        for (i in 0..maxCol) list.add(rowCells[i] ?: "")
                        rows.add(list)
                    }
                }
            }
            event = parser.next()
        }
        return rows
    }

    /** Convierte una referencia de celda ("B12") en índice de columna base 0. */
    private fun columnIndex(ref: String): Int {
        var idx = 0
        for (c in ref) {
            if (!c.isLetter()) break
            idx = idx * 26 + (c.uppercaseChar() - 'A' + 1)
        }
        return idx - 1
    }
}
