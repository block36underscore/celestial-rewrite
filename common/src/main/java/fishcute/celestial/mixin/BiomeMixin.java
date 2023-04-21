package fishcute.celestial.mixin;

import fishcute.celestial.CelestialSky;
import fishcute.celestial.Util;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void getFogColor(CallbackInfoReturnable<Integer> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            if (Util.getGetRealFogColor()) {
                info.setReturnValue(((Biome) (Object) this).getSpecialEffects().getFogColor());
            }
            else
                info.setReturnValue(CelestialSky.getDimensionRenderInfo().getEnvironment().getFogColor().getStoredColor().getRGB());

        }
    }
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void getSkyColor(CallbackInfoReturnable<Integer> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            if (Util.getGetRealSkyColor()) {
                info.setReturnValue(info.getReturnValue());
            }
            else
                info.setReturnValue(CelestialSky.getDimensionRenderInfo().getEnvironment().getSkyColor().getStoredColor().getRGB());
        }
    }
}