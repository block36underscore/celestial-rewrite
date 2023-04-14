package fishcute.celestial

import fishcute.celestial.obj.CelestialObject


class CelestialRenderInfo(val skyObjects: ArrayList<CelestialObject>, environment: AtmosphereData) {
    val environment: AtmosphereData

    init {
        this.environment = environment
    }
}