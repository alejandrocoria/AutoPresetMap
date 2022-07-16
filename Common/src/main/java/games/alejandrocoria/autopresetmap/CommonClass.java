package games.alejandrocoria.autopresetmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import games.alejandrocoria.autopresetmap.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.commons.io.FileUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
public class CommonClass {
    public enum Action {DEFAULT, NOTHING, PRESET1, PRESET2}

    public enum DefaultAction {
        NOTHING(Action.NOTHING),
        PRESET1(Action.PRESET1),
        PRESET2(Action.PRESET2);

        private final Action action;
        DefaultAction(Action action) {
            this.action = action;
        }
        public Action toAction() {
            return action;
        }
    }

    private static DefaultAction defaultAction = DefaultAction.NOTHING;
    private static final Map<ResourceLocation, Action> actions = new HashMap<>();
    private static final List<ResourceLocation> dimensions = new ArrayList<>();

    private static final Gson GSON = (new GsonBuilder()).setVersion(1.0).setPrettyPrinting().create();

    private static Level lastLevel = null;

    public static void worldInit() {
        loadActions();
    }

    public static void tick() {
        Level currentLevel = Minecraft.getInstance().level;
        if (currentLevel != null && currentLevel != lastLevel) {
            lastLevel = currentLevel;
            changedDimension(currentLevel.dimension().location());
        }
    }

    private static void changedDimension(ResourceLocation dimension) {
        Action action = actions.getOrDefault(dimension, Action.DEFAULT);
        if (action == Action.DEFAULT) {
            action = defaultAction.toAction();
        }

        switch (action) {
            case PRESET1:
                Services.PLATFORM.switchMiniMapPreset(1);
                break;
            case PRESET2:
                Services.PLATFORM.switchMiniMapPreset(2);
                break;
        }
    }

    private static void loadActions() {
        Constants.LOG.info("Loading actions...");
        actions.clear();
        dimensions.clear();

        for (ResourceLocation dimension : Services.PLATFORM.getDimensions()) {
            actions.put(dimension, Action.DEFAULT);
        }

        boolean needSave = false;
        File f = new File(Services.PLATFORM.getJMWorldDir(Minecraft.getInstance()), "autopresetmap.config");
        if (f.exists()) {
            try {
                String string = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                JsonObject root = GSON.fromJson(string, JsonObject.class);
                try {
                    defaultAction = DefaultAction.valueOf(root.get("defaultAction").getAsString());
                } catch (Exception e) {
                    Constants.LOG.error(e.getMessage(), e);
                    needSave = true;
                }

                JsonObject actionsObject = root.getAsJsonObject("actions");
                for (String key : actionsObject.keySet()) {
                    try {
                        Action action = Action.valueOf(actionsObject.get(key).getAsString());
                        actions.put(new ResourceLocation(key), action);
                    } catch (Exception e) {
                        Constants.LOG.error(e.getMessage(), e);
                        needSave = true;
                    }
                }
            } catch (Exception e) {
                Constants.LOG.error(e.getMessage(), e);
                needSave = true;
            }
        } else {
            needSave = true;
        }

        if (needSave) {
            saveActions();
        }

        dimensions.addAll(actions.keySet());
        sortDimensions();

        Constants.LOG.info("Actions loaded.");
    }

    private static void saveActions() {
        Constants.LOG.info("Saving actions...");

        JsonObject root = new JsonObject();
        root.add("defaultAction", GSON.toJsonTree(defaultAction));
        root.add("actions", GSON.toJsonTree(actions));
        String string = GSON.toJson(root);

        try {
            File f = new File(Services.PLATFORM.getJMWorldDir(Minecraft.getInstance()), "autopresetmap.config");
            FileUtils.writeStringToFile(f, string, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Constants.LOG.error(e.getMessage(), e);
        }

        Constants.LOG.info("Actions saved.");
    }

    private static void sortDimensions() {
        dimensions.sort((d1, d2) -> {
            if (d1.equals(d2)) {
                return 0;
            } else if (d1.getNamespace().equals("minecraft") && d2.getNamespace().equals("minecraft")) {
                if (d1.getPath().equals("the_nether") && d2.getPath().equals("the_end")) {
                    return -1;
                } else if (d1.getPath().equals("the_end") && d2.getPath().equals("the_nether")) {
                    return 1;
                }
                return d1.getPath().compareTo(d2.getPath());
            } else if (d1.getNamespace().equals("minecraft")) {
                return -1;
            } else if (d2.getNamespace().equals("minecraft")) {
                return 1;
            } else {
                int c = d1.getNamespace().compareTo(d2.getNamespace());
                if (c == 0) {
                    c = d1.getPath().compareTo(d2.getPath());
                }
                return c;
            }
        });
    }

    public static ResourceLocation getCurrentDimension() {
        ResourceLocation currentDimension = Minecraft.getInstance().player.clientLevel.dimension().location();
        if (actions.putIfAbsent(currentDimension, Action.DEFAULT) == null) {
            dimensions.add(currentDimension);
            sortDimensions();
        }

        return currentDimension;
    }

    public static void updateMiniMapCurrentDimension() {
        changedDimension(Minecraft.getInstance().player.clientLevel.dimension().location());
    }

    public static DefaultAction getDefaultAction() {
        return defaultAction;
    }

    public static List<ResourceLocation> getDimensions() {
        return dimensions;
    }

    public static Action getAction(ResourceLocation dimension) {
        return actions.getOrDefault(dimension, Action.DEFAULT);
    }

    public static void setDefaultAction(DefaultAction defaultAction) {
        CommonClass.defaultAction = defaultAction;
        saveActions();
    }

    public static void setAction(ResourceLocation dimension, Action action) {
        actions.put(dimension, action);
        saveActions();
    }
}
