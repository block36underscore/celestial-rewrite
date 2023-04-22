package fishcute.celestial.fabric;

import fishcute.celestial.Celestial;
import net.fabricmc.api.ClientModInitializer;

public class CelestialFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Celestial.init();
    }
}