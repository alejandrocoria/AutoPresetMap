package games.alejandrocoria.autopresetmap.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SimpleButton extends Button {
    private static final int TEXT_COLOR = 0xff999999;
    private static final int TEXT_COLOR_HIGHLIGHT = 0xffffffff;

    private final Font font;
    private SimpleLabel label;

    public SimpleButton(Font font, int x, int y, int width, Component text, Button.OnPress pressedAction) {
        super(x, y, width, 16, text, pressedAction);
        this.font = font;
        this.label = new SimpleLabel(font, x + width / 2, y + 5, SimpleLabel.Align.CENTER, text, TEXT_COLOR);
    }

    @Override
    public void setMessage(Component text) {
        this.label = new SimpleLabel(font, x + width / 2, y + 5, SimpleLabel.Align.CENTER, text, TEXT_COLOR);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (isHovered) {
            label.setColor(TEXT_COLOR_HIGHLIGHT);
        } else {
            label.setColor(TEXT_COLOR);
        }

        hLine(matrixStack, x, x + width, y, 0xff777777);
        hLine(matrixStack, x, x + width, y + 16, 0xff777777);
        vLine(matrixStack, x, y, y + 16, 0xff777777);
        vLine(matrixStack, x + width, y, y + 16, 0xff777777);

        label.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
