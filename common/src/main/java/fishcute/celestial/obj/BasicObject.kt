package fishcute.celestial.obj

import com.google.gson.JsonObject
import com.mojang.math.Vector3f
import fishcute.celestial.ColorEntry
import fishcute.celestial.VertexPoint
import fishcute.celestial.convertToPointUvList
import fishcute.celestial.expressions.Expression
import fishcute.celestial.expressions.ExpressionContext
import fishcute.celestial.expressions.ZERO
import fishcute.celestial.getOptional
import net.minecraft.resources.ResourceLocation

class BasicObject(
    val texture: ResourceLocation,
    val type: CelestialObjectType,
    val scale: Expression,
    val posX: Expression,
    val posY: Expression,
    val posZ: Expression,
    val distance: Expression,
    val degreesX: Expression,
    val degreesY: Expression,
    val degreesZ: Expression,
    val baseDegreesX: Expression,
    val baseDegreesY: Expression,
    val baseDegreesZ: Expression,
    val solidColor: ColorEntry,
    val celestialObjectProperties: BasicObjectProperties,
    val vertexList: ArrayList<VertexPoint>,

    ): ICelestialObject {
    override fun degX() = degreesX().toFloat()

    override fun degY() = degreesY().toFloat()

    override fun degZ() = degreesZ().toFloat()

    override fun posX() = posX.invoke().toFloat()

    override fun posY() = posY.invoke().toFloat()

    override fun posZ() = posZ.invoke().toFloat()

    override fun alpha() = celestialObjectProperties.alpha().toFloat()
    override fun distance() = distance.invoke().toFloat()
    override fun scale() = scale.invoke().toFloat()

    override fun moonPhase() = celestialObjectProperties.moonPhase().toInt()

    override fun vertices() = vertexList

    override fun colors() = Vector3f(
        celestialObjectProperties.red().toFloat(),
        celestialObjectProperties.green().toFloat(),
        celestialObjectProperties.blue().toFloat()
    )

    override fun solidColors(): Vector3f {
        val color = colors()
        return Vector3f(
            solidColor.storedColor.red * color.x(),
            solidColor.storedColor.green * color.y(),
            solidColor.storedColor.blue * color.z()
        )
    }

    override fun render() {
        TODO("Not yet implemented")
    }

    override fun renderAll() {
        this.render()
    }
    override fun texture() = texture

    override fun hasNext() = false

    override fun next() {}

}

fun createSkyObjectFromJson(o: JsonObject) : BasicObject {

    val type = findObjectType(o)

    //if (type == CelestialObjectType.SKYBOX) return CelestialObject.createSkyBoxObjectFromJson(o, name, dimension)

    var display = o.getAsJsonObject("display")
    if (display == null) display = JsonObject()
    var rotation = o.getAsJsonObject("rotation")
    if (rotation == null) rotation = JsonObject()

    var propertiesJson: JsonObject? = o.getAsJsonObject("properties")

    if (propertiesJson == null) propertiesJson = JsonObject()

    //I love parameters

    val cobject = BasicObject(
        ResourceLocation(o.getOptional( "texture", "")),
        type,
        display.getOptional( "scale", ZERO),
        display.getOptional( "pos_x", ZERO),
        display.getOptional( "pos_y", ZERO),
        display.getOptional( "pos_z", ZERO),
        display.getOptional( "distance", ZERO),
        rotation.getOptional( "degrees_x", ZERO),
        rotation.getOptional( "degrees_y", ZERO),
        rotation.getOptional( "degrees_z", ZERO),
        rotation.getOptional( "base_degrees_x", Expression.Const(-90.0)),
        rotation.getOptional( "base_degrees_y", ZERO),
        rotation.getOptional( "base_degrees_z", Expression.Const(90.0)),
        ColorEntry.createColorEntry(o, "solid_color", ColorEntry(), false),
        createBasicObjectPropertiesFromJson(propertiesJson),
//        o.getOptional( "parent", null),
//        dimension,
        convertToPointUvList(o, "vertex"),
    )

    return cobject
}

class BasicObjectProperties(
    val hasMoonPhases: Boolean,
    val moonPhase: Expression,
    val isSolid: Boolean,
    val red: Expression,
    val green: Expression,
    val blue: Expression,
    val alpha: Expression,
    val ignoreFog: Boolean,
)

fun createBasicObjectPropertiesFromJson(o: JsonObject): BasicObjectProperties {
    val ctx = ExpressionContext()

    return BasicObjectProperties(
        o.getOptional( "has_moon_phases", false),
        ctx.compile(o.getOptional( "moon_phase", "#moonPhase")),
        o.getOptional( "is_solid", false),
        ctx.compile(o.getOptional( "red", "1")),
        ctx.compile(o.getOptional( "green", "1")),
        ctx.compile(o.getOptional( "blue", "1")),
        ctx.compile(o.getOptional( "alpha", "1")),
        o.getOptional( "ignore_fog", false)
    )
}


enum class CelestialObjectType {
    DEFAULT,
    COLOR,
    SKYBOX
}


fun findObjectType(o: JsonObject): CelestialObjectType {
    val objectType: String = o.getOptional("type", "default")
    if (objectType != "skybox") {
        if (o.has("texture")) return CelestialObjectType.DEFAULT else if (o.has("solid_color")) return CelestialObjectType.COLOR
    }
    return getCelestialObjectType(objectType)
}

fun getCelestialObjectType(i: String?): CelestialObjectType {
    return when (i) {
        "color" -> CelestialObjectType.COLOR
        "skybox" -> CelestialObjectType.SKYBOX
        else -> CelestialObjectType.DEFAULT
    }
}