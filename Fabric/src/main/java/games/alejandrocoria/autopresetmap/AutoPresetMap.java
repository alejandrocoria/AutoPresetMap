package games.alejandrocoria.autopresetmap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Environment(EnvType.CLIENT)
public class AutoPresetMap implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> CommonClass.worldInit());
        ClientTickEvents.END_CLIENT_TICK.register(client -> CommonClass.tick());
    }
}
