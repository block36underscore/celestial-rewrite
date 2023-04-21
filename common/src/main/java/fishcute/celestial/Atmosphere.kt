package fishcute.celestial

import com.google.gson.JsonObject
import fishcute.celestial.expressions.Expression
import fishcute.celestial.expressions.NEGATIVE_ONE
import fishcute.celestial.expressions.ONE
import fishcute.celestial.expressions.ZERO

fun loadAtmospheres(dimensions: JsonObject) {

}


class AtmosphereData(
    val hasThickFog: Boolean,
    fogColor: ColorEntry,
    skyColor: ColorEntry,
    cloudHeight: Expression,
    cloudColor: ColorEntry,
    fogStart: Expression,
    fogEnd: Expression,
    twilightColor: ColorEntry,
    twilightAlpha: Expression,
    voidCullingLevel: Expression
) {
    val fogColor: ColorEntry
    val skyColor: ColorEntry
    val cloudHeight: Expression
    val cloudColor: ColorEntry
    val fogStart: Expression
    val fogEnd: Expression
    val twilightColor: ColorEntry
    val twilightAlpha: Expression
    val voidCullingLevel: Expression

    init {
        this.fogColor = fogColor
        this.skyColor = skyColor
        this.cloudHeight = cloudHeight
        this.cloudColor = cloudColor
        this.fogStart = fogStart
        this.fogEnd = fogEnd
        this.twilightColor = twilightColor
        this.twilightAlpha = twilightAlpha
        this.voidCullingLevel = voidCullingLevel
    }

    fun useSimpleFog(): Boolean {
        return fogStart == NEGATIVE_ONE || fogEnd == NEGATIVE_ONE
    }

    fun updateColorEntries() {
        skyColor.tick()
        fogColor.tick()
        cloudColor.tick()
        twilightColor.tick()
    }

    companion object {
        val DEFAULT_COLOR_SKY: ColorEntry = ColorEntry(decodeColor("#78a7ff"))
        val DEFAULT_COLOR_FOG: ColorEntry = ColorEntry(decodeColor("#c0d8ff"))
        val DEFAULT_COLOR_CLOUD: ColorEntry = ColorEntry(decodeColor("#ffffff"))
        val DEFAULT_COLOR_TWILIGHT: ColorEntry = ColorEntry(decodeColor("#b23333"))
        val DEFAULT = AtmosphereData(
            false,
            DEFAULT_COLOR_FOG,
            DEFAULT_COLOR_SKY,
            Expression.Const(128.0),
            DEFAULT_COLOR_CLOUD,
            NEGATIVE_ONE,
            NEGATIVE_ONE,
            DEFAULT_COLOR_TWILIGHT,
            NEGATIVE_ONE,
            ZERO
        )

        fun createEnvironmentRenderInfoFromJson(o: JsonObject?, dimension: String): AtmosphereData {
            if (o == null) {
                warn("Failed to read \"sky.json\" for dimension \"$dimension\" while loading environment render info.")
                return DEFAULT
            }
            if (!o.has("environment")) {
                log("Skipped loading environment.")
                return DEFAULT
            }
            val environment = o.getAsJsonObject("environment")
            val fog = environment.getAsJsonObject("fog")
            val clouds = environment.getAsJsonObject("clouds")
            return AtmosphereData(
                fog.getOptional("has_thick_fog", false),
                ColorEntry.createColorEntry(environment, "fog_color", DEFAULT_COLOR_FOG, true),
                ColorEntry.createColorEntry(environment, "sky_color", DEFAULT_COLOR_SKY, true),
                clouds.getOptional("height", Expression.Const(128.0)),
                ColorEntry.createColorEntry(clouds, "color", DEFAULT_COLOR_CLOUD, true),
                fog.getOptional("fog_start", NEGATIVE_ONE),
                fog.getOptional("fog_end", NEGATIVE_ONE),
                ColorEntry.createColorEntry(environment, "twilight_color", DEFAULT_COLOR_TWILIGHT, false),
                environment.getOptional("twilight_alpha", ONE),
                environment.getOptional("void_culling_level", ZERO)
            )
        }
    }
}

