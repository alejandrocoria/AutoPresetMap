package games.alejandrocoria.autopresetmap;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import journeymap.client.ui.UIManager;
import journeymap.client.ui.minimap.MiniMap;
import journeymap.common.Journeymap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = AutoPresetMap.MODID, version = AutoPresetMap.VERSION, dependencies = "required-after:journeymap@1.12.2-5.5.5", clientSideOnly = true)
public class AutoPresetMap {
    public static final String MODID = "autopresetmap";
    public static final String VERSION = "@VERSION@";
    public static Logger LOGGER;

    @Mod.Instance(AutoPresetMap.MODID)
    public static AutoPresetMap instance;



    public enum Action {
        NOTHING, PRESET1, PRESET2,
    }

    public TreeMap<Integer, Action> dimensionsAction;
    KeyBinding changeActionKey;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);

        dimensionsAction = new TreeMap<>();

        LOGGER.info("AutoPresetMap preInit done");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        changeActionKey = new KeyBinding("key.autopresetmap.change_action", KeyConflictContext.IN_GAME, Keyboard.KEY_O,
                "key.autopresetmap.category");
        ClientRegistry.registerKeyBinding(changeActionKey);

        LOGGER.info("AutoPresetMap init done");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        configToMap();
        LOGGER.info("AutoPresetMap postInit done");
    }

    @SubscribeEvent
    public void onConfigChangedEvent(OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            ConfigManager.sync(MODID, Type.INSTANCE);

            configToMap();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onEvent(KeyInputEvent event) {
        if (changeActionKey.isPressed()) {
            int dimension = Minecraft.getMinecraft().world.provider.getDimension();
            String dimensionName = Minecraft.getMinecraft().world.provider.getDimensionType().getName();
            Action action = dimensionsAction.get(dimension);

            if (action == null) {
                action = Action.NOTHING;
            } else if (action == Action.NOTHING) {
                action = Action.PRESET1;
            } else if (action == Action.PRESET1) {
                action = Action.PRESET2;
            } else if (action == Action.PRESET2) {
                action = null;
            }

            if (action == null) {
                dimensionsAction.remove(dimension);
            } else {
                dimensionsAction.put(dimension, action);
            }


            String message = I18n.format("chat.autopresetmap.change_action", TextFormatting.RESET, TextFormatting.YELLOW,
                    TextFormatting.GRAY, dimension, dimensionName, actionToString(action));

            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));

            mapToConfig();
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvent(PlayerChangedDimensionEvent event) {
        if (Minecraft.getMinecraft().player.getEntityId() == event.player.getEntityId()) {
            Action action = dimensionsAction.get(event.toDim);
            if (action == null) {
                changePreset(ConfigData.defaultAction);
            } else {
                changePreset(action);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void changePreset(Action action) {
        if (action == Action.PRESET1) {
            changePreset(1);
        } else if (action == Action.PRESET2) {
            changePreset(2);
        }
    }

    @SideOnly(Side.CLIENT)
    public void changePreset(int newPreset) {
        if (newPreset == 1) {
            if (Journeymap.getClient().getMiniMapProperties2().isActive()) {
                UIManager.INSTANCE.switchMiniMapPreset();
                MiniMap.state().requireRefresh();
            }
        } else if (newPreset == 2) {
            if (Journeymap.getClient().getMiniMapProperties1().isActive()) {
                UIManager.INSTANCE.switchMiniMapPreset();
                MiniMap.state().requireRefresh();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void configToMap() {
        dimensionsAction.clear();

        for (int d : ConfigData.dims_nothing) {
            dimensionsAction.put(d, Action.NOTHING);
        }

        for (int d : ConfigData.dims_preset_1) {
            dimensionsAction.put(d, Action.PRESET1);
        }

        for (int d : ConfigData.dims_preset_2) {
            dimensionsAction.put(d, Action.PRESET2);
        }
    }

    @SideOnly(Side.CLIENT)
    private void mapToConfig() {
        ArrayList<Integer> dims_nothing = new ArrayList<Integer>();
        ArrayList<Integer> dims_preset_1 = new ArrayList<Integer>();
        ArrayList<Integer> dims_preset_2 = new ArrayList<Integer>();

        for (Map.Entry<Integer, Action> entry : dimensionsAction.entrySet()) {
            if (entry.getValue() == Action.NOTHING) {
                dims_nothing.add(entry.getKey());
            } else if (entry.getValue() == Action.PRESET1) {
                dims_preset_1.add(entry.getKey());
            } else if (entry.getValue() == Action.PRESET2) {
                dims_preset_2.add(entry.getKey());
            }
        }

        ConfigData.dims_nothing = convertIntegers(dims_nothing);
        ConfigData.dims_preset_1 = convertIntegers(dims_preset_1);
        ConfigData.dims_preset_2 = convertIntegers(dims_preset_2);
        ConfigManager.sync(MODID, Type.INSTANCE);
    }

    private static String actionToString(Action action) {
        if (action == Action.NOTHING) {
            return new String("Do Nothing");
        } else if (action == Action.PRESET1) {
            return new String("Set Preset 1");
        } else if (action == Action.PRESET2) {
            return new String("Set Preset 2");
        }

        return String.format("Default: %s", actionToString(ConfigData.defaultAction));
    }



    @Config(modid = MODID)
    public static class ConfigData {
        @Comment({ "Default action that is taken when entering a dimension that is not in any of the lists." })
        public static Action defaultAction = Action.NOTHING;

        @Comment({ "Dimension IDs where the minimap preset is not changed." })
        public static int[] dims_nothing = {};

        @Comment({ "Dimension IDs where preset 1 of the minimap is set." })
        public static int[] dims_preset_1 = {};

        @Comment({ "Dimension IDs where preset 2 of the minimap is set." })
        public static int[] dims_preset_2 = {};
    }


    public static int[] convertIntegers(ArrayList<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
}
