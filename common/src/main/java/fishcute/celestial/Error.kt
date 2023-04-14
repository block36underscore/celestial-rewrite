package fishcute.celestial

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft


var warnings = 0
var errors = 0

var errorList = ArrayList<String>()

var hasShownWarning = false

fun sendErrorInGame(i: String, unloadResources: Boolean) {
    errors++
    if (Minecraft.getInstance().player == null) return
    if (errorList.contains(i) || errorList.size > 25) return
    errorList.add(i)
    sendMessage( "${ChatFormatting.RED}[Celestial] $i", false)
    if (errorList.size >= 25) sendMessage(
        "${ChatFormatting.RED}[Celestial] Passing 25 error messages. Muting error messages.",
        false
    )
    if (unloadResources) {
        sendMessage( "${ChatFormatting.RED}[Celestial] Unloading Celestial resources.", false)
    }
}

fun sendWarnInGame(i: String) {
    if (Minecraft.getInstance().player == null) return
    if (errorList.contains(i)) return
    errorList.add(i)
    sendMessage("${ChatFormatting.YELLOW}[Celestial] $i", false)
}

fun warn(i: Any) {
    warnings++
    if (!Minecraft.getInstance().isPaused) {
        log("[Warn] $i")
        sendWarnInGame(i.toString())
    }
}

fun log(i: Any) {
    if (!Minecraft.getInstance().isPaused) println("[Celestial] $i")
}