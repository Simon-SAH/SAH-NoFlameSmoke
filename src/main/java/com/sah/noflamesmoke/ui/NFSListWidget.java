package com.sah.noflamesmoke.ui;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;


import java.util.List;

public class NFSListWidget extends ContainerObjectSelectionList<NFSListWidget.Entry> {

    public NFSListWidget(
            Minecraft minecraft,
            int width,
            int height,
            int y,
            int itemHeight
    ) {
        super(minecraft, width, height, y, itemHeight);
    }

    public void addEntry(EntryData data) {this.addEntry(new Entry(data), 24);
    }

    @Override
    public int getRowWidth() {
        return 500;
    }

    @Override
    protected int scrollBarX() {
        return this.getX() + 570;
    }

    public static class EntryData {

        public final String key;
        public final String label;

        public final NFSConfig.Toggle toggle;

        public EntryData(
                String key,
                String label,
                NFSConfig.Toggle toggle
        ) {
            this.key = key;
            this.label = label;
            this.toggle = toggle;
        }
    }

    public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        private final EntryData data;
        private final net.minecraft.client.gui.components.Button flameButton;
        private final net.minecraft.client.gui.components.Button smokeButton;

        private static String enabledDisabled(boolean disabled) {return disabled ? "OFF" : "ON";}

        public Entry(EntryData data) {

            this.data = data;

            this.flameButton =
                    net.minecraft.client.gui.components.Button.builder(
                            Component.literal("Flame: " + enabledDisabled(data.toggle.flame)),
                            b -> {

                                data.toggle.flame = !data.toggle.flame;

                                b.setMessage(
                                        Component.literal("Flame: " + enabledDisabled(data.toggle.flame))
                                );
                            }
                    ).bounds(0, 0, 90, 20).build();

            this.smokeButton =
                    net.minecraft.client.gui.components.Button.builder(
                            Component.literal("Smoke: " + enabledDisabled(data.toggle.smoke)),
                            b -> {

                                data.toggle.smoke = !data.toggle.smoke;

                                b.setMessage(
                                        Component.literal("Smoke: " + enabledDisabled(data.toggle.smoke))
                                );
                            }
                    ).bounds(0, 0, 90, 20).build();
        }

        @Override
        public void extractContent(
                final GuiGraphicsExtractor graphics,
                final int mouseX,
                final int mouseY,
                final boolean hovered,
                final float a
        ) {

            int panelCenter = this.getWidth() / 2;

            int labelColumn = panelCenter - 180;

            int flameColumn = panelCenter + 1;

            int smokeColumn = panelCenter + 92;

            graphics.text(
                    Minecraft.getInstance().font,
                    Component.literal(data.label).getVisualOrderText(),
                    labelColumn,
                    this.getContentY() + 6,
                    0xFFFFFFFF,
                    true
            );


            this.flameButton.setPosition(
                    flameColumn,
                    this.getContentY()
            );

            this.smokeButton.setPosition(
                    smokeColumn,
                    this.getContentY()
            );

            this.flameButton.extractRenderState(
                    graphics,
                    mouseX,
                    mouseY,
                    a
            );

            this.smokeButton.extractRenderState(
                    graphics,
                    mouseX,
                    mouseY,
                    a
            );
        }

        public List<? extends NarratableEntry> narratables() {
            return java.util.List.of(
                    this.flameButton,
                    this.smokeButton
            );
        }
        @Override
        public java.util.List<? extends net.minecraft.client.gui.components.events.GuiEventListener> children() {
            return java.util.List.of(
                    this.flameButton,
                    this.smokeButton
            );
        }
    }

}