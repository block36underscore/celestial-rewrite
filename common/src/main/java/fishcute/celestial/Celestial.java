package fishcute.celestial;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;

public class Celestial {
    public static final KeyMapping RELOAD_SKY = new KeyMapping("key.celestial.reload_sky",
            InputConstants.KEY_F10,
            "key.categories.misc");

    public static final KeyMapping DEBUG_INFO = new KeyMapping("key.celestial.debug_sky",
            InputConstants.KEY_F9,
            "key.categories.misc");

    public static final String MOD_ID = "celestial";

    public static void init() {

        KeyMappingRegistry.register(RELOAD_SKY);
        KeyMappingRegistry.register(DEBUG_INFO);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (RELOAD_SKY.consumeClick()) {
                Loader.reloadCelestial();
            }
            while (DEBUG_INFO.consumeClick()) {
                Util.sendMessage(Util.debugInfo(), false);
            }
        });



        CelestialModuleKt.getCELESTIAL_MODULE().printVars();

    }
}