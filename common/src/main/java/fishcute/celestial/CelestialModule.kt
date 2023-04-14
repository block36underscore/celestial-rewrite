package fishcute.celestial

import fishcute.celestial.expressions.FunctionList
import fishcute.celestial.expressions.Module
import fishcute.celestial.expressions.VariableList
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.level.LightLayer
import java.lang.Math.pow
import kotlin.math.floor
import kotlin.math.pow


// Not very null safe. Seemed to work out just fine in the original though
public val CELESTIAL_MODULE = Module("celestial",
    VariableList(
        hashMapOf(
            "xPos" to {Minecraft.getInstance().player!!.x},
            "yPos" to {Minecraft.getInstance().player!!.y},
            "zPos" to {Minecraft.getInstance().player!!.z},
            "tickDelta" to {Minecraft.getInstance().frameTime.toDouble()},
            "dayLight" to {1 - (1 + ((Minecraft.getInstance().level!!.getStarBrightness(Minecraft.getInstance().frameTime) - 0.5) * 2))},
            "rainGradient" to {1.0 - Minecraft.getInstance().level!!.getRainLevel(Minecraft.getInstance().frameTime)},
            "isSubmerged" to {if (Minecraft.getInstance().player!!.isInWater) 1.0 else 0.0},
            "getTotalTime" to {Minecraft.getInstance().level!!.gameTime.toDouble()},
            "getDayTime" to {Minecraft.getInstance().level!!.dayTime() - (floor(Minecraft.getInstance().level!!.dayTime.toDouble() / 24000) * 24000)},
            "starAlpha" to {Minecraft.getInstance().level!!.getStarBrightness(Minecraft.getInstance().frameTime).toDouble()},
            "skyAngle" to {Minecraft.getInstance().level!!.getTimeOfDay(Minecraft.getInstance().frameTime) * 360.0},
            "pitch" to {Minecraft.getInstance().player!!.getViewYRot(Minecraft.getInstance().frameTime).toDouble()},
            "yaw" to {Minecraft.getInstance().player!!.getViewXRot(Minecraft.getInstance().frameTime).toDouble()},
            "isLeftClicking" to {if (Minecraft.getInstance().mouseHandler.isLeftPressed) 1.0 else 0.0},
            "isRightClicking" to {if (Minecraft.getInstance().mouseHandler.isRightPressed) 1.0 else 0.0},
            "viewDistance" to {Minecraft.getInstance().options.effectiveRenderDistance.toDouble()},
            "moonPhase" to {Minecraft.getInstance().level!!.moonPhase.toDouble()},
            "skyDarken" to {Minecraft.getInstance().level!!.skyDarken.toDouble()},
            "lightningFlashTime" to {Minecraft.getInstance().level!!.getThunderLevel(Minecraft.getInstance().frameTime).toDouble()},
            "thunderGradient" to {Minecraft.getInstance().level!!.getThunderLevel(Minecraft.getInstance().frameTime).toDouble()},
            "skyLightLevel" to {Minecraft.getInstance().level!!.getBrightness(LightLayer.SKY, Minecraft.getInstance().player!!.blockPosition()).toDouble()},
            "blockLightLevel" to {Minecraft.getInstance().level!!.getBrightness(LightLayer.BLOCK, Minecraft.getInstance().player!!.blockPosition()).toDouble()},
            "twilightAlpha" to {
                val g = Mth.cos(Minecraft.getInstance().level!!.getTimeOfDay(Minecraft.getInstance().frameTime) * 6.2831855f) - 0.0f
                if (g >= -0.4f && g <= 0.4f) (1.0f - (1.0f - Mth.sin(((g + 0.0f) / 0.4f * 0.5f + 0.5f) * 3.1415927f)) * 0.99f).toDouble()
                    .pow(2.0)
                else 0.0
            }
        )
    ),
    FunctionList(
        hashMapOf(

        )
    )
)