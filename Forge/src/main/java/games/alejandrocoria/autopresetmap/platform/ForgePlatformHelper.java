package games.alejandrocoria.autopresetmap.platform;

import games.alejandrocoria.autopresetmap.platform.services.IPlatformHelper;
import journeymap.client.data.WorldData;
import journeymap.client.io.FileHandler;
import journeymap.client.ui.GuiHooks;
import journeymap.client.ui.UIManager;
import journeymap.client.waypoint.WaypointStore;
import journeymap.common.helper.DimensionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public List<ResourceLocation> getDimensions() {
        List<ResourceLocation> dimensions = new ArrayList<>();
        List<WorldData.DimensionProvider> dimensionProviders = WorldData.getDimensionProviders(WaypointStore.INSTANCE.getLoadedDimensions());

        for (WorldData.DimensionProvider dimension : dimensionProviders) {
            dimensions.add(DimensionHelper.getDimResource(dimension.getDimensionId()));
        }

        return dimensions;
    }

    @Override
    public void popScreenLayer() {
        GuiHooks.popGuiLayer();
    }

    @Override
    public File getJMWorldDir(Minecraft minecraft) {
        return FileHandler.getJMWorldDir(minecraft);
    }

    @Override
    public void switchMiniMapPreset(int preset) {
        UIManager.INSTANCE.switchMiniMapPreset(preset);
    }
}
