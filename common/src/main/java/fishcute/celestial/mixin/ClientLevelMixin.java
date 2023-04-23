package fishcute.celestial.mixin;

import fishcute.celestial.CelestialSky;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "getCloudColor", at = @At("RETURN"), cancellable = true)
    private void getCloudColor(float f, CallbackInfoReturnable<Vec3> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            if (CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getIgnoreSkyEffects()) {
                CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().setInheritColor(new Color(255,  255, 255));
                info.setReturnValue(new Vec3(CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getStoredColor().getRed() / 255.0F, CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getStoredColor().getGreen() / 255.0F, CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getStoredColor().getBlue() / 255.0F));
            }
            else
                CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().setInheritColor(new Color(255, 255, 255));
        }
    }
    @ModifyVariable(method = "getCloudColor", at = @At("STORE"), ordinal = 3)
    private float getRed(float h) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getStoredColor().getRed() / 255f) * h;
        return h;
    }
    @ModifyVariable(method = "getCloudColor", at = @At("STORE"), ordinal = 4)
    private float getGreen(float i) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getStoredColor().getGreen() / 255f) * i;
        return i;
    }
    @ModifyVariable(method = "getCloudColor", at = @At("STORE"), ordinal = 5)
    private float getBlue(float j) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().getEnvironment().getCloudColor().getStoredColor().getBlue() / 255f) * j;
        return j;
    }
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void getSkyColor(Vec3 vec3, float f, CallbackInfoReturnable<Vec3> info) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().getEnvironment().getSkyColor().getIgnoreSkyEffects()) {
            info.setReturnValue(new Vec3(((float) CelestialSky.getDimensionRenderInfo().getEnvironment().getSkyColor().getStoredColor().getRed()) / 255,
                    ((float) CelestialSky.getDimensionRenderInfo().getEnvironment().getSkyColor().getStoredColor().getGreen()) / 255,
                    ((float) CelestialSky.getDimensionRenderInfo().getEnvironment().getSkyColor().getStoredColor().getBlue()) / 255));
        }
    }
}
