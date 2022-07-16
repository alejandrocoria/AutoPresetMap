package games.alejandrocoria.autopresetmap.plugin;

import games.alejandrocoria.autopresetmap.Constants;
import games.alejandrocoria.autopresetmap.gui.screens.ConfigScreen;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.ThemeButtonDisplay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;
import journeymap.client.ui.ScreenLayerManager;
import net.minecraft.client.resources.language.I18n;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class JourneyMapPlugin implements IClientPlugin {
    @Override
    public void initialize(final IClientAPI jmAPI) {
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(JourneyMapPlugin::onFullscreenAddonButton);
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {
    }

    private static void onFullscreenAddonButton(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        ThemeButtonDisplay buttonDisplay = event.getThemeButtonDisplay();
        buttonDisplay.addThemeButton(I18n.get("autopresetmap.jm_config_button"), "autopresetmap",
                b -> ScreenLayerManager.pushLayer(new ConfigScreen()));
    }
}
