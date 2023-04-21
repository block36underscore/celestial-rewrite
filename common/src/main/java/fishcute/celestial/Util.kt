@file:JvmName("Util")

package fishcute.celestial

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import fishcute.celestial.expressions.Expression
import fishcute.celestial.expressions.compile
import fishcute.celestial.sky.createSkyObjectFromJson
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import java.awt.Color
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

var getRealSkyColor = false
var getRealFogColor = false

val reader: Gson = Gson()
fun getFile(path: String): JsonObject? {
    return try {
        val inputStream = Minecraft.getInstance().resourceManager.getResource(ResourceLocation(path)).get().open()
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val jsonElement: JsonElement = reader.fromJson(bufferedReader, JsonElement::class.java)
        jsonElement.asJsonObject
    } catch (exception: Exception) {
        exception.printStackTrace()
        null
    }
}

fun fileExists(path: String) = Minecraft.getInstance().resourceManager.getResource(ResourceLocation(path)).isPresent

class VertexPoint(var pointX: String, var pointY: String, var pointZ: String, uvX: String?, uvY: String?) {
    var uvX: String? = null
    var uvY: String? = null
    var hasUv: Boolean

    init {
        hasUv = uvX != null || uvY != null
        this.uvX = uvX
        this.uvY = uvY
    }
}

fun sendMessage(text: String?, actionBar: Boolean) {
    text?.let { Component.literal(it) }?.let { Minecraft.getInstance().player?.displayClientMessage(it, actionBar) }
}

fun convertToPointUvList(o: JsonObject, name: String): ArrayList<VertexPoint> {
    val returnList = ArrayList<VertexPoint>()
    try {
        if (!o.has(name)) return ArrayList()
        for (e in o.getAsJsonArray(name)) {
            val entry = e.asJsonObject
            returnList.add(
                VertexPoint(
                    entry.getOptional("x", ""),
                    entry.getOptional("y", ""),
                    entry.getOptional("z", ""),
                    entry.getStringOrNull("uv_x"),
                    entry.getStringOrNull("uv_y")
                )
            )
        }
    } catch (e: java.lang.Exception) {
        sendErrorInGame("Failed to parse vertex point list \"$name\".", false)
        return ArrayList()
    }
    return returnList
}


// If JsonPrimitive worked a bit differently I would have used the following code:
//
// internal inline fun <reified T> JsonObject.getOptional(id: String, other: T) = if (this.has(id)) this[id] as T else other
//
// It's beautiful in a terrible way. I feel the need to mention it in a comment here
// Easily the most cursed thing I have ever written

internal fun JsonObject.getStringOrNull(id: String) =
    if (this.has(id)) this[id].asString
    else null

internal fun JsonObject.getOptionalExpression(id: String, other: String) =
    if (this.has(id)) compile(this[id].asString)
    else compile(other)

internal fun JsonObject.getOptional(id: String, other: Expression) =
    if (this.has(id)) compile(this[id].asString)
    else other

internal fun JsonObject.getOptional(id: String, other: String) =
    if (this.has(id)) this[id].asString
    else other

internal fun JsonObject.getOptional(id: String, other: Boolean) =
    if (this.has(id)) this[id].asBoolean
    else other

internal fun JsonObject.getOptional(id: String, other: Int) =
    if (this.has(id)) this[id].asInt
    else other

internal fun JsonObject.getAsBasicObject() = createSkyObjectFromJson(this)

private const val digits = "0123456789ABCDEF"

fun getDecimal(color: String): Int {
    var hex = color.uppercase(Locale.getDefault())
    var i = 0
    for (element in hex) {
        val c = element
        val d: Int = digits.indexOf(c)
        i = 16 * i + d
    }
    return i
}

fun debugInfo() =
"""Has custom sky: $dimensionHasCustomSky
Sky color: ${getDimensionEnvironmentRenderInfo().skyColor}
Fog color: ${getDimensionEnvironmentRenderInfo().fogColor}
""".trimMargin()