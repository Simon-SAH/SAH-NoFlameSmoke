package com.sah.noflamesmoke.ui;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


public class NFSConfigScreen extends Screen {

    private NFSListWidget list;

    private net.minecraft.client.gui.components.Button restoreButton;
    private net.minecraft.client.gui.components.Button saveButton;
    private net.minecraft.client.gui.components.Button cancelButton;

    private net.minecraft.client.gui.components.Button allFlameButton;
    private net.minecraft.client.gui.components.Button allSmokeButton;

    private final Screen parent;
    private NFSConfig cfgLocal;

    private static String enabledDisabled(boolean enabled) {return enabled ? "ON" : "OFF";}

    public NFSConfigScreen(Screen parent) {
        super(Component.literal("No Flame & Smoke"));
        this.parent = parent;

    }

    @Override
    public void extractRenderState(
            net.minecraft.client.gui.GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(
                this.font,
                this.title,
                this.width / 2,
                8,
                0xFFFFFFFF
        );
    }


    @Override
    protected void init() {

        this.cfgLocal = copyOf(ConfigManager.get());

        int centerX = this.width / 2;

        this.allFlameButton = this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(
                        Component.literal("All Flames: " +enabledDisabled(!cfgLocal.disableAllFlames)),
                        b -> {

                            cfgLocal.disableAllFlames =
                                    !cfgLocal.disableAllFlames;

                            applyGlobalToPerBlockLocal();

                            ConfigManager.save(cfgLocal);

                            this.rebuildWidgets();
                        }
                ).bounds(
                        centerX - 160,
                        28,
                        150,
                        20
                ).build()
        );

        this.allSmokeButton = this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(
                        Component.literal("All Smoke: " +enabledDisabled(!cfgLocal.disableAllSmoke)),
                        b -> {

                            cfgLocal.disableAllSmoke =
                                    !cfgLocal.disableAllSmoke;

                            applyGlobalToPerBlockLocal();

                            ConfigManager.save(cfgLocal);

                            this.rebuildWidgets();
                        }
                ).bounds(
                        centerX + 10,
                        28,
                        150,
                        20
                ).build()
        );


        int listWidth = 760;

        int listLeft = this.width / 2 - listWidth / 2;

        int listTop = 58;

        int listHeight = this.height - 100;

        this.list = new NFSListWidget(
                this.minecraft,
                listWidth,
                listHeight,
                listTop,
                24
        );

        this.list.updateSizeAndPosition(
                listWidth,
                listHeight,
                listLeft,
                listTop
        );

        this.addRenderableWidget(this.list);

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "torch",
                        "Torch",
                        getToggle("torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "wall_torch",
                        "Wall torch",
                        getToggle("wall_torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "redstone_torch",
                        "Redstone torch",
                        getToggle("redstone_torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "wall_redstone_torch",
                        "Wall redstone torch",
                        getToggle("wall_redstone_torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "soul_torch",
                        "Soul torch",
                        getToggle("soul_torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "wall_soul_torch",
                        "Wall soul torch",
                        getToggle("wall_soul_torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "candles",
                        "Candles",
                        getToggle("candles")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "furnace",
                        "Furnace",
                        getToggle("furnace")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "smoker",
                        "Smoker",
                        getToggle("smoker")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "blast_furnace",
                        "Blast furnace",
                        getToggle("blast_furnace")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "copper_torch",
                        "Copper torch",
                        getToggle("copper_torch")
                )
        );

        this.list.addEntry(
                new NFSListWidget.EntryData(
                        "wall_copper_torch",
                        "Wall copper torch",
                        getToggle("wall_copper_torch")
                )
        );

        this.restoreButton = this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(
                        Component.literal("Restore Vanilla"),
                        b -> {

                            restoreVanillaLocal();

                            ConfigManager.save(cfgLocal);

                            this.rebuildWidgets();
                        }
                ).bounds(
                        centerX - 235,
                        this.height - 30,
                        150,
                        20
                ).build()
        );
        this.cancelButton = this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(
                        Component.literal("Cancel"),
                        b -> this.onClose()
                ).bounds(
                        centerX - 75,
                        this.height - 30,
                        150,
                        20
                ).build()
        );

        this.saveButton = this.addRenderableWidget(
                net.minecraft.client.gui.components.Button.builder(
                        Component.literal("Save & Quit"),
                        b -> {

                            ConfigManager.save(cfgLocal);

                            this.onClose();
                        }
                ).bounds(
                        centerX + 85,
                        this.height - 30,
                        150,
                        20
                ).build()
        );


    }
    private NFSConfig.Toggle getToggle(String key) {
        switch (key) {
            case "torch": return cfgLocal.torch;
            case "wall_torch": return cfgLocal.wall_torch;
            case "redstone_torch": return cfgLocal.redstone_torch;
            case "wall_redstone_torch": return cfgLocal.wall_redstone_torch;
            case "soul_torch": return cfgLocal.soul_torch;
            case "wall_soul_torch": return cfgLocal.wall_soul_torch;
            case "candles": return cfgLocal.candles;
            case "furnace": return cfgLocal.furnace;
            case "smoker": return cfgLocal.smoker;

            case "blast_furnace":
                if (cfgLocal.blast_furnace == null)
                    cfgLocal.blast_furnace = new NFSConfig.Toggle();
                return cfgLocal.blast_furnace;

            case "copper_torch":
                if (cfgLocal.copper_torch == null)
                    cfgLocal.copper_torch = new NFSConfig.Toggle();
                return cfgLocal.copper_torch;

            case "wall_copper_torch":
                if (cfgLocal.wall_copper_torch == null)
                    cfgLocal.wall_copper_torch = new NFSConfig.Toggle();
                return cfgLocal.wall_copper_torch;

            default:
                return cfgLocal.torch;
        }
    }

    private static NFSConfig copyOf(NFSConfig src) {

        NFSConfig d = new NFSConfig();

        d.disableAllFlames = src.disableAllFlames;
        d.disableAllSmoke  = src.disableAllSmoke;

        d.torch = copyT(src.torch);
        d.wall_torch = copyT(src.wall_torch);
        d.redstone_torch = copyT(src.redstone_torch);
        d.wall_redstone_torch = copyT(src.wall_redstone_torch);
        d.soul_torch = copyT(src.soul_torch);
        d.wall_soul_torch = copyT(src.wall_soul_torch);
        d.candles = copyT(src.candles);
        d.furnace = copyT(src.furnace);
        d.smoker = copyT(src.smoker);
        d.blast_furnace = copyT(src.blast_furnace);
        d.copper_torch = copyT(src.copper_torch);
        d.wall_copper_torch = copyT(src.wall_copper_torch);

        return d;
    }

    private static NFSConfig.Toggle copyT(NFSConfig.Toggle t) {

        if (t == null)
            return new NFSConfig.Toggle();

        NFSConfig.Toggle n = new NFSConfig.Toggle();

        n.flame = t.flame;
        n.smoke = t.smoke;

        return n;
    }
    private void applyGlobalToPerBlockLocal() {

        boolean dFlame = cfgLocal.disableAllFlames;
        boolean dSmoke = cfgLocal.disableAllSmoke;

        for (NFSConfig.Toggle t : allTogglesOf(cfgLocal)) {
            t.flame = dFlame;
            t.smoke = dSmoke;
        }
    }

    private void restoreVanillaLocal() {

        cfgLocal.disableAllFlames = false;
        cfgLocal.disableAllSmoke = false;

        for (NFSConfig.Toggle t : allTogglesOf(cfgLocal)) {
            t.flame = false;
            t.smoke = false;
        }
    }

    private static java.util.List<NFSConfig.Toggle> allTogglesOf(NFSConfig c) {

        java.util.List<NFSConfig.Toggle> list = new java.util.ArrayList<>();

        if (c.torch != null) list.add(c.torch);
        if (c.wall_torch != null) list.add(c.wall_torch);
        if (c.redstone_torch != null) list.add(c.redstone_torch);
        if (c.wall_redstone_torch != null) list.add(c.wall_redstone_torch);
        if (c.soul_torch != null) list.add(c.soul_torch);
        if (c.wall_soul_torch != null) list.add(c.wall_soul_torch);
        if (c.candles != null) list.add(c.candles);
        if (c.furnace != null) list.add(c.furnace);
        if (c.smoker != null) list.add(c.smoker);
        if (c.blast_furnace != null) list.add(c.blast_furnace);
        if (c.copper_torch != null) list.add(c.copper_torch);
        if (c.wall_copper_torch != null) list.add(c.wall_copper_torch);

        return list;
    }


}
