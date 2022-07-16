package games.alejandrocoria.autopresetmap;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@Mod(Constants.MOD_ID)
public class AutoPresetMap {
    public AutoPresetMap() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> AutoPresetMap::addListenerClient);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void addListenerClient() {
        MinecraftForge.EVENT_BUS.addListener(AutoPresetMap::clientConnectedToServer);
        MinecraftForge.EVENT_BUS.addListener(AutoPresetMap::clientTickEvent);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientConnectedToServer(ClientPlayerNetworkEvent.LoggedInEvent event) {
        CommonClass.worldInit();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void clientTickEvent(TickEvent.ClientTickEvent event) {
        CommonClass.tick();
    }
}
