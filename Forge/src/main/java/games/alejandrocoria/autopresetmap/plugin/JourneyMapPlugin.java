package games.alejandrocoria.autopresetmap.plugin;

import games.alejandrocoria.autopresetmap.Constants;
import games.alejandrocoria.autopresetmap.gui.screens.ConfigScreen;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ThemeButtonDisplay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import journeymap.client.ui.GuiHooks;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
public class JourneyMapPlugin implements IClientPlugin {
    @Override
    public void initialize(final IClientAPI jmAPI) {
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent clientEvent) {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFullscreenAddonButton(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        ThemeButtonDisplay buttonDisplay = event.getThemeButtonDisplay();
        buttonDisplay.addThemeButton(I18n.get("autopresetmap.jm_config_button"), "autopresetmap",
                b -> GuiHooks.pushGuiLayer(new ConfigScreen()));
    }
}
