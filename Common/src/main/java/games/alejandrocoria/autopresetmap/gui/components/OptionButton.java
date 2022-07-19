package games.alejandrocoria.autopresetmap.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class OptionButton extends Button {
    private static final int COLOR = 0xdddddd;
    private static final int HIGHLIGHTED_COLOR = 0xffffffff;

    private final Font font;
    private final List<Component> options;
    private int selected = 0;

    public OptionButton(Font font, int x, int y, int width, Button.OnPress pressedAction) {
        super(x, y, width, 12, Component.empty(), pressedAction);
        this.font = font;
        options = new ArrayList<>();
    }

    public void addOption(Component text) {
        options.add(text);
    }

    public void setSelected(int selected) {
        if (selected < 0) {
            selected = 0;
        } else if (selected >= options.size()) {
            selected = options.size() - 1;
        }

        this.selected = selected;
    }

    public int getSelected() {
        return selected;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (visible && isHovered) {
            if (delta > 0) {
                ++selected;
                if (selected >= options.size()) {
                    selected = 0;
                }
            } else {
                --selected;
                if (selected < 0) {
                    selected = options.size() - 1;
                }
            }

            playDownSound(Minecraft.getInstance().getSoundManager());
            onPress();
            return true;
        }

        return false;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        int c = COLOR;
        if (!active) {
            c = 0xff777777;
        } else if (isHovered) {
            c = HIGHLIGHTED_COLOR;
        }

        fill(matrixStack, x - 1, y - 1, x + width + 1, y + height + 1, 0xffa0a0a0);
        fill(matrixStack, x, y, x + width, y + height, 0xff000000);

        font.draw(matrixStack, options.get(selected), x + 4, y + 2, c);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        ++selected;
        if (selected >= options.size()) {
            selected = 0;
        }

        onPress();
    }
}
