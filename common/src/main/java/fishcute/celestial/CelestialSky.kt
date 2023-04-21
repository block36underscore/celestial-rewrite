@file:JvmName("CelestialSky")

package fishcute.celestial

import net.minecraft.client.Minecraft

var forceUpdateVariables = false

fun getDimensionRenderInfo() = renderInfoRegistrar.get(Minecraft.getInstance().level!!.dimension().location())!!

fun getDimensionEnvironmentRenderInfo() = renderInfoRegistrar.get(Minecraft.getInstance().level!!.dimension().location())!!.environment

fun doesDimensionHaveCustomSky(): Boolean {
    return dimensionHasCustomSky && getDimensionRenderInfo() != null
}