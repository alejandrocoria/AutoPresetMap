package games.alejandrocoria.autopresetmap.platform;

import games.alejandrocoria.autopresetmap.platform.services.IPlatformHelper;
import journeymap.client.data.WorldData;
import journeymap.client.io.FileHandler;
import journeymap.client.ui.ScreenLayerManager;
import journeymap.client.ui.UIManager;
import journeymap.client.waypoint.WaypointStore;
import journeymap.common.helper.DimensionHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
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
        ScreenLayerManager.popLayer();
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
