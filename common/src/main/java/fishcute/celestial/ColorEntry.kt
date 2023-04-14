import com.google.gson.JsonObject
import fishcute.celestial.doesDimensionHaveCustomSky
import fishcute.celestial.expressions.Expression
import fishcute.celestial.expressions.ONE
import fishcute.celestial.expressions.ZERO
import fishcute.celestial.forceUpdateVariables
import fishcute.celestial.getOptional
import fishcute.celestial.sendErrorInGame
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.util.CubicSampler
import net.minecraft.world.phys.Vec3
import org.apache.commons.lang3.tuple.MutablePair
import java.awt.Color


class ColorEntry {

    constructor(): this(null, "#ffffff", 1, false, ONE, ONE, ONE)
    constructor(
        colors: ArrayList<MutablePair<Color, Expression>>?,
        baseColor: String,
        updateFrequency: Int,
        ignoreSkyEffects: Boolean,
        r: Expression,
        g: Expression,
        b: Expression
    ) {
        this.colors = colors
        if (baseColor == "inherit") inheritColor =
            true else if (baseColor == "#skyColor" || baseColor == "#fogColor" || baseColor == "#cloudColor" || baseColor == "#twilightColor") cloneColor =
            baseColor else this.baseColor = decodeColor(baseColor)
        this.updateFrequency = updateFrequency
        this.ignoreSkyEffects = ignoreSkyEffects
        red = r
        green = g
        blue = b
    }

    constructor(color: Color) {
        storedColor = color
        isBasicColor = true
        inheritColor = false
        ignoreSkyEffects = false
    }

    fun tick() {
        if (isBasicColor) return
        if (updateTick <= 0 || forceUpdateVariables) {
            updateTick = updateFrequency
            updateColor()
        } else updateTick--
    }

    var red:Expression = ONE
    var green:Expression = ONE
    var blue:Expression = ONE
    var isBasicColor = false
    var colors: ArrayList<MutablePair<Color, Expression>>? = null
    var baseColor = Color(255, 255, 255)
    var cloneColor: String? = null
    var inheritColor = false
    var ignoreSkyEffects: Boolean
    var storedColor = Color(255, 255, 255)
    var updateFrequency = 0
    var updateTick = 0
    fun setInheritColor(c: Color) {
        if (inheritColor) baseColor = c
    }

    fun updateColor() {
        if (cloneColor != null && doesDimensionHaveCustomSky()) {
            baseColor = decodeColor(cloneColor!!)
        }
        storedColor = resultColor
    }

    val resultColor: Color
        get() {
            if (colors!!.size == 0) return Color(
                (baseColor.red * red.invoke()).toInt(),
                (baseColor.green * green.invoke()).toInt(),
                (baseColor.blue * blue.invoke()).toInt()
            )
            var r = baseColor.red
            var g = baseColor.green
            var b = baseColor.blue
            var value: Double
            for (color in colors!!) {
                value = color.value.invoke()
                if (value > 1) value = 1.0 else if (value <= 0) continue
                if (r <= 0) r = 1
                if (g <= 0) g = 1
                if (b <= 0) b = 1
                r = (r * (1 - (r - color.key.red).toFloat() / r * value)).toInt()
                g = (g * (1 - (g - color.key.green).toFloat() / g * value)).toInt()
                b = (b * (1 - (b - color.key.blue).toFloat() / b * value)).toInt()
            }
            return Color(
                (r * red.invoke()).toInt(),
                (g * green.invoke()).toInt(),
                (b * blue.invoke()).toInt()
            )
        }

    companion object {
        fun createColorEntry(
            o: JsonObject?,
            elementName: String,
            defaultEntry: ColorEntry,
            optionalSkyEffects: Boolean
        ): ColorEntry {
            if (o == null) return defaultEntry //TODO: Make this properly null safe
            try {
                o.get(elementName).getAsJsonObject().getAsJsonArray(elementName)
            } catch (e: Exception) {
                if (o.has(elementName)) {
                    val color: String = o.getOptional( elementName, "#ffffff")
                    return if (color == "inherit") ColorEntry(
                        ArrayList<MutablePair<Color, Expression>>(),
                        "inherit",
                        0,
                        false,
                        ONE,
                        ONE,
                        ONE
                    ) else ColorEntry(decodeColor(color))
                }
                return defaultEntry
            }
            val colorObject: JsonObject = o.get(elementName).getAsJsonObject()
            val colors = ArrayList<MutablePair<Color, Expression>>()
            val baseColor: String = o.getOptional("base_color", "#ffffff")
            if (colorObject.has("colors")) {
                try {
                    for (color in colorObject.getAsJsonArray("colors")) {
                        colors.add(
                            MutablePair(
                                decodeColor(color.getAsJsonObject().getOptional("color", "#ffffff")),
                                color.getAsJsonObject().getOptional("alpha", ZERO)
                            )
                        )
                    }
                } catch (e: Exception) {
                    sendErrorInGame("Failed to parse color entry \"$elementName\".", false)
                }
            }
            return ColorEntry(
                colors,
                baseColor,
                o.getOptional("update_frequency", 0),
                optionalSkyEffects && o.getOptional("ignore_sky_effects", false),
                o.getOptional("red", ONE),
                o.getOptional("green", ONE),
                o.getOptional("blue", ONE)
            )
        }
    }
}

var getRealSkyColor = false
var getRealFogColor = false
fun decodeColor(hex: String): Color {
    return try {
        when (hex) {
            "#skyColor" -> return getSkyColor()
            "#fogColor" -> return getFogColor()
        }
        Color.decode(if (hex.startsWith("#")) hex else "#$hex")
    } catch (ignored: java.lang.Exception) {
        sendErrorInGame("Failed to parse HEX color \"$hex\"", false)
        Color(0, 0, 0)
    }
}

fun getSkyColor(): Color {
    getRealSkyColor = true
    val vec33 = CubicSampler.gaussianSampleVec3(
        Minecraft.getInstance().player!!.position()
    ) { ix: Int, jx: Int, kx: Int ->
        Vec3.fromRGB24(
            Minecraft.getInstance().level!!.getBiome(BlockPos(ix, jx, kx))
                .value().skyColor
        )
    }
    getRealSkyColor = false
    return Color((vec33.x * 255).toInt(), (vec33.y * 255).toInt(), (vec33.z * 255).toInt())
}

fun getFogColor(): Color {
    getRealFogColor = true
    val vec33 = CubicSampler.gaussianSampleVec3(
        Minecraft.getInstance().player!!.position()
    ) { ix: Int, jx: Int, kx: Int ->
        Vec3.fromRGB24(
            Minecraft.getInstance().level!!.getBiome(BlockPos(ix, jx, kx))
                .value().fogColor
        )
    }
    getRealFogColor = false
    return Color((vec33.x * 255).toInt(), (vec33.y * 255).toInt(), (vec33.z * 255).toInt())
}

fun getDayLight(): Float {
    return 1 - (1 + (Minecraft.getInstance().level!!.getStarBrightness(Minecraft.getInstance().frameTime) - 0.5) * 2).toFloat()
}