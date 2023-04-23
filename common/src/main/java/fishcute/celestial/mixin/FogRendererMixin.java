package fishcute.celestial.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.AtmosphereData;
import fishcute.celestial.CelestialSky;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "setupFog", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static boolean setupFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().getEnvironment().useSimpleFog())
            return CelestialSky.getDimensionRenderInfo().getEnvironment().getHasThickFog();
        return thickFog;
    }
    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void setupFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().getEnvironment().useSimpleFog()) {
            AtmosphereData data = CelestialSky.getDimensionRenderInfo().getEnvironment();
            RenderSystem.setShaderFogStart(data.getFogStart().invoke().floatValue());
            RenderSystem.setShaderFogEnd(data.getFogEnd().invoke().floatValue());
        }
    }


    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    @Inject(method = "levelFogColor", at = @At("RETURN"))
    private static void setupColor(CallbackInfo ci) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().getEnvironment().getFogColor().getIgnoreSkyEffects()) {
            fogRed = CelestialSky.getDimensionRenderInfo().getEnvironment().getFogColor().getStoredColor().getRed() / 255.0F;
            fogGreen = CelestialSky.getDimensionRenderInfo().getEnvironment().getFogColor().getStoredColor().getGreen() / 255.0F;
            fogBlue = CelestialSky.getDimensionRenderInfo().getEnvironment().getFogColor().getStoredColor().getBlue() / 255.0F;
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0);
        }
    }
}