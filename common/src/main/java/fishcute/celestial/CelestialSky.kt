@file:JvmName("CelestialSky")

package fishcute.celestial

import net.minecraft.client.Minecraft

var forceUpdateVariables = false

fun getDimensionRenderInfo(): CelestialRenderInfo? {
    return try {
        renderInfoRegistrar.get(Minecraft.getInstance().level!!.dimension().location())
    } catch (e: Exception) {
        null
    }
}

fun getDimensionEnvironmentRenderInfo(): AtmosphereData? {
    return if (Minecraft.getInstance().level == null) null
    else renderInfoRegistrar.get(Minecraft.getInstance().level!!.dimension().location())?.environment
}

fun doesDimensionHaveCustomSky(): Boolean {
    return dimensionHasCustomSky && getDimensionRenderInfo() != null
}