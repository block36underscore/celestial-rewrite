@file:JvmName("CelestialSky")

package fishcute.celestial

import net.minecraft.client.Minecraft

var forceUpdateVariables = false

fun getDimensionRenderInfo() = Minecraft.getInstance().level?.dimension()?.location()
    ?.let { renderInfoRegistrar.get(it) }

fun getDimensionEnvironmentRenderInfo() = Minecraft.getInstance().level?.dimension()?.location()
    ?.let { renderInfoRegistrar.get(it) }?.environment

fun doesDimensionHaveCustomSky(): Boolean {
    return Minecraft.getInstance().level?.dimension()?.location()
        ?.let { renderInfoRegistrar.get(it) } != null
}