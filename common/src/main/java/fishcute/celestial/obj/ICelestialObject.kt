package fishcute.celestial.obj

import com.mojang.math.Vector3f
import fishcute.celestial.VertexPoint
import net.minecraft.resources.ResourceLocation

interface ICelestialObject {
    fun degX(): Float
    fun degY(): Float
    fun degZ(): Float

    fun posX(): Float
    fun posY(): Float
    fun posZ(): Float

    fun alpha(): Float
    fun distance(): Float
    fun scale(): Float
    fun moonPhase(): Int

    fun vertices(): ArrayList<VertexPoint>

    fun colors(): Vector3f
    fun solidColors(): Vector3f
    fun texture(): ResourceLocation

    fun hasNext(): Boolean
    fun next()

}
