package games.alejandrocoria.autopresetmap.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import games.alejandrocoria.autopresetmap.CommonClass;
import games.alejandrocoria.autopresetmap.gui.components.ConfigScrollBox;
import games.alejandrocoria.autopresetmap.gui.components.OptionButton;
import games.alejandrocoria.autopresetmap.gui.components.SimpleButton;
import games.alejandrocoria.autopresetmap.gui.components.SimpleLabel;
import games.alejandrocoria.autopresetmap.platform.Services;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfigScreen extends Screen {
    OptionButton defaultConfigButton;
    ConfigScrollBox scrollBox;
    SimpleButton doneButton;

    public ConfigScreen() {
        super(new TranslatableComponent("autopresetmap.config.title"));
    }

    @Override
    public void init() {
        addRenderableOnly(new SimpleLabel(font, width / 2 - 5, 40, SimpleLabel.Align.RIGHT,
                new TranslatableComponent("autopresetmap.config.default_label"), 0xffffff));

        defaultConfigButton = new OptionButton(font, width / 2 + 5, 38, 100, b -> defaultConfigButtonPressed());
        defaultConfigButton.addOption(new TranslatableComponent("autopresetmap.config.nothing"));
        defaultConfigButton.addOption(new TranslatableComponent("autopresetmap.config.preset1"));
        defaultConfigButton.addOption(new TranslatableComponent("autopresetmap.config.preset2"));
        defaultConfigButton.setSelected(CommonClass.getDefaultAction().ordinal());
        addRenderableWidget(defaultConfigButton);

        scrollBox = new ConfigScrollBox(font, width / 2 - ConfigScrollBox.WIDTH / 2, 70, height - 90, CommonClass::setAction);
        addRenderableWidget(scrollBox);

        ResourceLocation currentDimension = CommonClass.getCurrentDimension();
        for (ResourceLocation dimension : CommonClass.getDimensions()) {
            boolean hasPlayer = currentDimension.equals(dimension);
            scrollBox.addElement(dimension, CommonClass.getAction(dimension), hasPlayer ? minecraft.player : null);
        }

        doneButton = new SimpleButton(font, width / 2 - 50, height - 30, 100,
                new TranslatableComponent("gui.done"), b -> Services.PLATFORM.popScreenLayer());
        addRenderableWidget(doneButton);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        drawCenteredString(matrixStack, font, title, width / 2, 8, 0xffffff);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollBox.mouseReleased();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        CommonClass.updateMiniMapCurrentDimension();
        super.onClose();
    }

    private void defaultConfigButtonPressed() {
        CommonClass.setDefaultAction(CommonClass.DefaultAction.values()[defaultConfigButton.getSelected()]);
    }
}
