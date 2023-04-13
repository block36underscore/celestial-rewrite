package fishcute.celestial.sky

import ColorEntry
import com.google.gson.JsonObject
import fishcute.celestial.VertexPoint
import fishcute.celestial.convertToPointUvList
import fishcute.celestial.expressions.Expression
import fishcute.celestial.expressions.ExpressionContext
import fishcute.celestial.expressions.ZERO
import fishcute.celestial.getOptional
import fishcute.celestial.obj.CelestialObject
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

    ): CelestialObject {

}

fun createSkyObjectFromJson(o: JsonObject) : BasicObject? {

    val type = findObjectType(o)

    //if (type == CelestialObjectType.SKYBOX) return CelestialObject.createSkyBoxObjectFromJson(o, name, dimension)

    val display = o.getAsJsonObject("display")
    val rotation = o.getAsJsonObject("rotation")
    //I love parameters
    val ctx = ExpressionContext()

    val cobject = BasicObject(
        ResourceLocation("minecraft", o.getOptional( "texture", "")),
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
        ColorEntry.createColorEntry(o, "solid_color", null, false),
        createBasicObjectPropertiesFromJson(o.getAsJsonObject("properties")),
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