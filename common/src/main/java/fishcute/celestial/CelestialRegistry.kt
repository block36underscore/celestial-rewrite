package fishcute.celestial

import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

class ReloadableRegistry<T>(var map: HashMap<ResourceLocation, T> = HashMap()) {
    fun get(key: ResourceLocation) = map[key]

    fun register(key: ResourceLocation, value: T) = map.put(key, value)

    fun reload() {
        map = HashMap()
    }
}

val renderInfoRegistrar: ReloadableRegistry<CelestialRenderInfo> = ReloadableRegistry()

fun getAtmosphereData(): AtmosphereData? {
    return if (Minecraft.getInstance().level == null) null
    else renderInfoRegistrar.get(Minecraft.getInstance().level!!.dimension().location())?.environment
}
