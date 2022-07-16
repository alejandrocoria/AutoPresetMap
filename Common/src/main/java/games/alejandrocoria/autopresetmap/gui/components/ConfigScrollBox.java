package games.alejandrocoria.autopresetmap.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import games.alejandrocoria.autopresetmap.CommonClass;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

@ParametersAreNonnullByDefault
public class ConfigScrollBox extends AbstractWidget {
    public static final int WIDTH = ConfigScrollElement.WIDTH;

    private final Font font;
    private int scrollStart = 0;
    private final int scrollHeight;
    private int scrollBarPos = 0;
    private int scrollBarHeight = 0;
    private boolean scrollBarHovered = false;
    private boolean scrollBarGrabbed = false;
    private int scrollBarGrabbedYPos = 0;
    private final List<ConfigScrollElement> elements;
    private final OnChange responder;

    public ConfigScrollBox(Font font, int x, int y, int height, OnChange responder) {
        super(x, y, WIDTH, max(height, ConfigScrollElement.HEIGHT), TextComponent.EMPTY);
        this.font = font;
        elements = new ArrayList<>();
        this.x = x;
        this.y = y;
        scrollHeight = this.height / ConfigScrollElement.HEIGHT;
        this.height = scrollHeight * ConfigScrollElement.HEIGHT;
        this.responder = responder;
    }

    public void addElement(ResourceLocation dimension, CommonClass.Action action, @Nullable LocalPlayer player) {
        int yPos = y + elements.size() * ConfigScrollElement.HEIGHT;
        ConfigScrollElement element = new ConfigScrollElement(font, x, yPos, dimension, action, player, responder);
        elements.add(element);
        scrollBarGrabbed = false;
        updateScrollWindow();
        updateScrollBar();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (active && visible) {
            if ((isHovered || scrollBarHovered) && !scrollBarGrabbed) {
                boolean elementScrolled = false;
                for (ConfigScrollElement element : elements) {
                    elementScrolled |= element.mouseScrolled(mouseX, mouseY, delta);
                }

                if (!elementScrolled) {
                    int amount = (int) -delta;
                    if (amount < 0 && scrollStart == 0) {
                        return false;
                    } else if (amount > 0 && scrollStart + scrollHeight >= elements.size()) {
                        return false;
                    }

                    scrollStart += amount;
                    updateScrollWindow();
                    updateScrollBar();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (ConfigScrollElement element : elements) {
            element.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        if (scrollBarHeight > 0) {
            scrollBarHovered = mouseX >= x + width + 5 && mouseY >= y && mouseX < x + width + 15 && mouseY < y + height;

            int barColor = 0xff777777;
            if (scrollBarGrabbed) {
                barColor = 0xff666666;
            } else if (scrollBarHovered) {
                barColor = 0xffaaaaaa;
            }

            fill(matrixStack, x + width + 5, y, x + width + 15, y + height, 0x1affffff);
            fill(matrixStack, x + width + 5, y + scrollBarPos, x + width + 15, y + scrollBarPos + scrollBarHeight, barColor);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && visible && button == 0) {
            if (scrollBarHeight > 0 && mouseX >= x + width + 5 && mouseY >= y && mouseX < x + width + 15 && mouseY < y + height) {
                if (mouseY < y + scrollBarPos) {
                    mouseScrolled(mouseX, mouseY, 1);
                } else if (mouseY > y + scrollBarPos + scrollBarHeight) {
                    mouseScrolled(mouseX, mouseY, -1);
                } else {
                    scrollBarGrabbed = true;
                    scrollBarGrabbedYPos = (int) mouseY - y - scrollBarPos;
                }

                return true;
            }

            if (isHovered && !scrollBarGrabbed) {
                boolean result = false;
                for (ConfigScrollElement element : elements) {
                    result |= element.mouseClicked(mouseX, mouseY, button);
                }

                return result;
            }
        }

        return false;
    }

    // Custom mouseReleased to be called from the Screen.
    public void mouseReleased() {
        if (visible && scrollBarHeight > 0 && scrollBarGrabbed) {
            scrollBarGrabbed = false;
            updateScrollBar();
        }
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        if (scrollBarHeight > 0 && scrollBarGrabbed) {
            int delta = (int) mouseY - y - scrollBarPos - scrollBarGrabbedYPos;

            if (delta == 0) {
                return;
            }

            scrollBarPos += delta;
            if (scrollBarPos < 0) {
                scrollBarPos = 0;
            } else if (scrollBarPos + scrollBarHeight > height) {
                scrollBarPos = height - scrollBarHeight;
            }

            int newScrollStart = Math.round(((float) scrollBarPos) / height * elements.size());

            if (newScrollStart != scrollStart) {
                scrollStart = newScrollStart;
                updateScrollWindow();
            }
        }
    }

    private void updateScrollWindow() {
        if (elements.size() <= scrollHeight) {
            scrollStart = 0;
        } else {
            int bottomExtra = elements.size() - (scrollStart + scrollHeight);
            if (bottomExtra < 0) {
                scrollStart += bottomExtra;
            }

            if (scrollStart < 0) {
                scrollStart = 0;
            }
        }

        for (int i = 0; i < elements.size(); ++i) {
            if (i < scrollStart || i >= scrollStart + scrollHeight) {
                elements.get(i).visible = false;
            } else {
                elements.get(i).visible = true;
                elements.get(i).setY(y + (i - scrollStart) * ConfigScrollElement.HEIGHT);
            }
        }
    }

    private void updateScrollBar() {
        if (elements.size() <= scrollHeight) {
            scrollBarHeight = 0;
            scrollBarHovered = false;
            scrollBarGrabbed = false;
            return;
        }

        scrollBarHeight = Math.round(((float) scrollHeight) / elements.size() * height);
        scrollBarPos = Math.round(((float) scrollStart) / elements.size() * height);
        if (scrollBarPos + scrollBarHeight > height) {
            scrollBarPos = height - scrollBarHeight;
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput n) {
    }

    public interface OnChange {
        void onChange(ResourceLocation dimension, CommonClass.Action action);
    }

    @ParametersAreNonnullByDefault
    private static class ConfigScrollElement extends AbstractWidget {
        public static final int WIDTH = 364;
        public static final int HEIGHT = 24;

        private final SimpleLabel dimensionName;
        private final SimpleLabel dimensionResource;
        private final OptionButton actionButton;
        private final LocalPlayer player;

        public ConfigScrollElement(Font font, int x, int y, ResourceLocation dimension, CommonClass.Action action, @Nullable LocalPlayer player, OnChange responder) {
            super(x, y, WIDTH, HEIGHT, new TextComponent(dimension.toString()));

            this.player = player;

            String name = dimension.getPath().replace("_", " ");
            name = WordUtils.capitalize(name);
            dimensionName = new SimpleLabel(font, x + 24, y + 3, SimpleLabel.Align.LEFT, new TextComponent(name), 0xffffffff);
            dimensionResource = new SimpleLabel(font, x + 24, y + 13, SimpleLabel.Align.LEFT, new TextComponent(dimension.toString()), 0xff606060);

            actionButton = new OptionButton(font, x + width - 106, y + 6, 100,
                    button -> responder.onChange(dimension, CommonClass.Action.values()[((OptionButton) button).getSelected()]));
            actionButton.addOption(new TranslatableComponent("autopresetmap.config.default"));
            actionButton.addOption(new TranslatableComponent("autopresetmap.config.nothing"));
            actionButton.addOption(new TranslatableComponent("autopresetmap.config.preset1"));
            actionButton.addOption(new TranslatableComponent("autopresetmap.config.preset2"));
            actionButton.setSelected(action.ordinal());
        }

        public void setY(int y) {
            this.y = y;

            dimensionName.y = y + 3;
            dimensionResource.y = y + 13;
            actionButton.y = y + 6;
        }

        public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (visible) {
                isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

                if (isHovered) {
                    fill(matrixStack, x + (player == null ? 20 : 0), y, x + width, y + height, 0xa0222222);
                }

                dimensionName.render(matrixStack, mouseX, mouseY, partialTicks);
                dimensionResource.render(matrixStack, mouseX, mouseY, partialTicks);
                actionButton.render(matrixStack, mouseX, mouseY, partialTicks);

                if (player != null) {
                    boolean upsideDown = LivingEntityRenderer.isEntityUpsideDown(player);
                    int texY = 8 + (upsideDown ? 8 : 0);
                    int texHeight = 8 * (upsideDown ? -1 : 1);

                    RenderSystem.setShaderColor(1.f, 1.f, 1.f, 1.f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShaderTexture(0, player.getSkinTextureLocation());

                    GuiComponent.blit(matrixStack, x + 4, y + 4, 16, 16, 8.f, (float) texY, 8, texHeight, 64, 64);
                    if (player.isModelPartShown(PlayerModelPart.HAT)) {
                        GuiComponent.blit(matrixStack, x + 4, y + 4, 16, 16, 40.f, (float) texY, 8, texHeight, 64, 64);
                    }
                }
            } else {
                isHovered = false;
            }
        }

        @Override
        public void updateNarration(NarrationElementOutput n) {
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
            if (active && visible) {
                return actionButton.mouseScrolled(mouseX, mouseY, delta);
            }

            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (active && visible) {
                return actionButton.mouseClicked(mouseX, mouseY, button);
            }

            return false;
        }
    }
}
