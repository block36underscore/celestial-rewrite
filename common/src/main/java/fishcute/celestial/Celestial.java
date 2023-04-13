package fishcute.celestial;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public class Celestial {
    public static KeyMapping reloadSky = new KeyMapping("key.reload_sky",
            InputConstants.KEY_F10,
            "key.categories.misc");

    public static final String MOD_ID = "celestial";

    public static void init() {

        KeyMappingRegistry.register(reloadSky);

        ClientTickEvent.CLIENT_POST.register(minecraft -> {
            while (reloadSky.consumeClick()) {
                if (minecraft.player != null) minecraft.player.displayClientMessage(Component.literal("click"), true);
            }
        });


    }
}