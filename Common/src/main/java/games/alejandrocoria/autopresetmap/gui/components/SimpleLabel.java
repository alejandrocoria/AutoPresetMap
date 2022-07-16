package games.alejandrocoria.autopresetmap.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class SimpleLabel extends AbstractWidget {
    public enum Align {
        LEFT, CENTER, RIGHT
    }

    private final Font font;
    private int color;
    private final Align align;
    private List<String> texts;
    private List<Integer> widths;
    private Point topLeft;
    private Point bottomRight;

    public SimpleLabel(Font font, int x, int y, Align align, Component text, int color) {
        super(x, y, 0, 0, text);
        this.font = font;
        this.color = color;
        this.align = align;

        setText(text);
    }

    public void setText(Component text) {
        texts = new ArrayList<>();
        widths = new ArrayList<>();

        for (String t : text.getString().split("\\R")) {
            texts.add(t);
            widths.add(font.width(t));
        }

        topLeft = new Point();
        bottomRight = new Point();
        bottomRight.y = texts.size() * 12;

        if (align == Align.LEFT) {
            for (int i = 0; i < texts.size(); ++i) {
                int width = widths.get(i);
                if (width > bottomRight.x) {
                    bottomRight.x = width;
                }
            }
        } else if (align == Align.CENTER) {
            for (int i = 0; i < texts.size(); ++i) {
                int halfWidth = widths.get(i) / 2;
                if (-halfWidth < topLeft.x) {
                    topLeft.x = -halfWidth;
                }
                if (halfWidth > bottomRight.x) {
                    bottomRight.x = halfWidth;
                }
            }
        } else {
            for (int i = 0; i < texts.size(); ++i) {
                int width = widths.get(i);
                if (-width < topLeft.x) {
                    topLeft.x = -width;
                }
            }
        }
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (align == Align.LEFT) {
            for (int i = 0; i < texts.size(); ++i) {
                font.draw(matrixStack, texts.get(i), x, y + i * 12, color);
            }
        } else if (align == Align.CENTER) {
            for (int i = 0; i < texts.size(); ++i) {
                font.draw(matrixStack, texts.get(i), x - (float) (widths.get(i) - 1) / 2, y + i * 12, color);
            }
        } else {
            for (int i = 0; i < texts.size(); ++i) {
                font.draw(matrixStack, texts.get(i), x - widths.get(i), y + i * 12, color);
            }
        }

        isHovered = mouseX >= topLeft.x + x && mouseY >= topLeft.y + y && mouseX < bottomRight.x + x && mouseY < bottomRight.y + y;
    }

    @Override
    public void updateNarration(NarrationElementOutput n) {
    }

    private static class Point {
        public int x = 0;
        public int y = 0;
    }
}
