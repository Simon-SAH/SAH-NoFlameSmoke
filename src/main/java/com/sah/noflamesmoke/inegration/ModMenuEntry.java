package com.sah.noflamesmoke.integration;

import com.sah.noflamesmoke.ui.NFSConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new SafeLauncher(parent);
    }

    static class SafeLauncher extends Screen {
        private final Screen parent;
        private Throwable error;

        SafeLauncher(Screen parent) {
            super(Text.literal("No Flame & Smoke"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            try {
                this.client.setScreen(new NFSConfigScreen(parent));
            } catch (Throwable t) {
                this.error = t;
                t.printStackTrace(); // trafi do run/logs/latest.log
                int x = this.width / 2 - 40, y = this.height / 2 + 20;
                this.addDrawableChild(ButtonWidget.builder(Text.literal("Back"),
                        b -> this.client.setScreen(parent)).dimensions(x, y, 80, 20).build());
            }
        }

        @Override
        public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
            this.renderBackground(ctx, mouseX, mouseY, delta);
            super.render(ctx, mouseX, mouseY, delta);
            if (error != null) {
                ctx.drawCenteredTextWithShadow(this.textRenderer,
                        Text.literal("Config screen failed: " + error.getClass().getSimpleName()),
                        this.width / 2, this.height / 2 - 10, 0xFF5555);
            }
        }
    }
}