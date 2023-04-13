@file:JvmName("Loader")

package fishcute.celestial

import net.minecraft.ChatFormatting

import net.minecraft.client.Minecraft




var dimensionHasCustomSky = false
fun reload() {


    println("loaded rp")
}

fun tick() {
    if (Minecraft.getInstance().level == null) return
    val level = Minecraft.getInstance().level!!



}
