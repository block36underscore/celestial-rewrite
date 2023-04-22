package fishcute.celestial

import fishcute.celestial.obj.ICelestialObject


class CelestialRenderInfo(val skyObjects: ArrayList<ICelestialObject>, environment: AtmosphereData) {
    val environment: AtmosphereData

    init {
        this.environment = environment
    }
}