@file:JvmName("ClientTick")

package fishcute.celestial

import fishcute.celestial.Celestial.RELOAD_SKY
import net.minecraft.ChatFormatting

import net.minecraft.client.Minecraft




var dimensionHasCustomSky = false

fun tick() {
    if (Minecraft.getInstance().level != null) worldTick()
}

fun worldTick() {
    dimensionHasCustomSky = renderInfoRegistrar.map.containsKey(Minecraft.getInstance().level!!.dimension().location())
    updateStars()
    //updateVariableValues() //Probably not needed
    if (doesDimensionHaveCustomSky()) {
        getDimensionRenderInfo().environment.skyColor.setInheritColor(getSkyColor())
        getDimensionRenderInfo().environment.fogColor.setInheritColor(getFogColor())
    }
    while (RELOAD_SKY.consumeClick()) {
        reloadCelestial()
        if (!hasShownWarning) {
            hasShownWarning = true
            sendMessage(
                ChatFormatting.RED.toString() + "Note: This will not reload textures. Use F3-T to reload textures.",
                false
            )
        }
    }
}


fun updateStars() {}