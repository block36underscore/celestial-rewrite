@file:JvmName("Loader")

package fishcute.celestial

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import fishcute.celestial.obj.ICelestialObject
import fishcute.celestial.sky.createSkyObjectFromJson
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation


private const val dimensions_location = "celestial:sky/dimensions.json"

fun reloadCelestial() {
    loadResources()
    println("loaded rp")
}

private fun loadResources() {
    warnings = 0
    errors = 0

    var dimensionCount = 0
    var objectCount = 0

    renderInfoRegistrar.reload()

    if (!fileExists(dimensions_location)) {
        log("Found no dimension.json file. Skipping initialization")
        return
    }

    val dimensionList: JsonArray = getFile(dimensions_location)!!.getAsJsonArray("dimensions")

    for (dimension in getAsStringList(dimensionList)!!) {
        log("Loading sky for dimension \"$dimension\"")
        val celestialObjects  = ArrayList<ICelestialObject>()
        for (i in getAllCelestialObjects(dimension)) {
            log("[$dimension] Loading celestial object \"$i\"")
            val obj = getFile("celestial:sky/$dimension/objects/$i.json")!!
            val o = createSkyObjectFromJson(obj)
            celestialObjects.add(o)
            objectCount++
        }
        renderInfoRegistrar.register(
            ResourceLocation(dimension),
            CelestialRenderInfo(
                celestialObjects,
            AtmosphereData.createEnvironmentRenderInfoFromJson(getFile("celestial:sky/$dimension/sky.json"), dimension)
        ))
        dimensionCount++
    }
    //Util.initalizeToReplaceMap(setupVariables()); // Probably not needed

    log("Finished loading skies for $dimensionCount dimension(s). Loaded $objectCount celestial object(s) with $warnings warning(s) and $errors error(s).");
    Minecraft.getInstance().player?.displayClientMessage(Component.literal("${ChatFormatting.GRAY}[Celestial] Reloaded with " + warnings + " warning(s) and " +errors + " error(s)."), false);
}

fun getAsStringList(array: JsonArray): ArrayList<String> {
    val returnObject = ArrayList<String>()
    for (a in array) {
        if (a != null && !a.isJsonNull) returnObject.add(a.asString) else warn("Found null JsonElement in array \"$array\"")
    }
    return returnObject
}

fun getAllCelestialObjects(dimension: String): ArrayList<String> {
    val o: JsonObject? = getFile("celestial:sky/$dimension/sky.json")
    if (o == null) {
        log("Found no sky.json for dimension\"$dimension\", skipping dimension.")
        return ArrayList()
    }
    val skyObjectList: JsonArray = o.getAsJsonArray("sky_objects")
    return getAsStringList(skyObjectList)
}