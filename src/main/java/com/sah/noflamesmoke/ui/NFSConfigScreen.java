package com.sah.noflamesmoke.ui;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.client.gui.Click;

import java.util.ArrayList;
import java.util.List;

public class NFSConfigScreen extends Screen {
    private final Screen parent;

    // pracujemy na kopii; zapis dopiero przy Save & Quit
    private NFSConfig cfgLocal;

    // vanilla separator (resource packi podmienią same)
    private static final Identifier SEP_TEX = Identifier.of("minecraft", "textures/gui/header_separator.png");
    private static final int SEP_W = 32, SEP_H = 2, TEX_W = 32, TEX_H = 2;

    // scroller – obsłuż oba warianty ścieżki (widget / widgets)
    private static final Identifier SCROLLER_TEX_WIDGET =
            Identifier.of("minecraft", "textures/gui/sprites/widget/scroller.png");
    private static final Identifier SCROLLER_TEX_WIDGETS =
            Identifier.of("minecraft", "textures/gui/sprites/widgets/scroller.png");
    // rozmiar źródłowy vanilla: 6x27 (capy 1 px)
    private static final int SCROLLER_W = 6;
    private static final int SCROLLER_H_SRC = 27;

    // top / bottom menu
    private ButtonWidget btnAllFlames, btnAllSmoke, btnApplyAll, btnRestore;
    private ButtonWidget btnCancel, btnSave;

    // lista + scroll
    private final List<Row> rows = new ArrayList<>();
    private int listStartY;
    private int listEndY;
    private int listX;
    private int scrollY = 0;       // aktualny scroll (int)
    private int minScrollY = 0;    // minimalny scroll (≤ 0)

    // płynne przewijanie
    private double targetScroll = 0.0;
    private double smoothScroll = 0.0;

    // drag paska
    private boolean draggingScrollbar = false;
    private int scrollbarX;

    private static class Row {
        final String key;
        final Text label;
        int baseY;
        int labelY;
        ButtonWidget flameBtn, smokeBtn;
        boolean available = true;
        Row(String key) { this.key = key; this.label = Text.literal(pretty(key)); }
    }

    public NFSConfigScreen(Screen parent) {
        super(Text.literal("No Flame & Smoke"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.cfgLocal = copyOf(ConfigManager.get());

        int x = this.width / 2 - 170;
        this.listX = x;

        // ——— układ bez splasha ———
        int topY1 = 40;
        int topY2 = topY1 + 24;
        int headerBottom = topY2 + 28;
        int footerHeight = 28;

        // ——— zakres listy (między headerem a footerem) ———
        this.listStartY = headerBottom + 6;
        this.listEndY   = this.height - footerHeight - 6;

        // scrollbar
        this.scrollbarX = this.width - 8 - SCROLLER_W;

        // ——— wiersze listy ———
        rows.clear();
        rows.add(new Row("torch"));
        rows.add(new Row("wall_torch"));
        rows.add(new Row("redstone_torch"));
        rows.add(new Row("wall_redstone_torch"));
        rows.add(new Row("soul_torch"));
        rows.add(new Row("wall_soul_torch"));
        rows.add(new Row("candles"));
        rows.add(new Row("furnace"));
        rows.add(new Row("smoker"));
        rows.add(new Row("blast_furnace"));
        rows.add(new Row("copper_torch"));        // przyszłościowo
        rows.add(new Row("wall_copper_torch"));   // przyszłościowo

        rows.forEach(r -> r.available = isKeyAvailableHint(r.key));

        int y = this.listStartY;
        for (Row r : rows) {
            r.baseY = y;
            r.labelY = y;

            r.flameBtn = addDrawableChild(ButtonWidget.builder(
                    Text.literal("Flame: " + onOff(getToggle(r.key).flame)), b -> {
                        NFSConfig.Toggle t = getToggle(r.key);
                        t.flame = !t.flame;
                        r.flameBtn.setMessage(Text.literal("Flame: " + onOff(t.flame)));
                    }).dimensions(x + 220, y, 80, 20).build());

            r.smokeBtn = addDrawableChild(ButtonWidget.builder(
                    Text.literal("Smoke: " + onOff(getToggle(r.key).smoke)), b -> {
                        NFSConfig.Toggle t = getToggle(r.key);
                        t.smoke = !t.smoke;
                        r.smokeBtn.setMessage(Text.literal("Smoke: " + onOff(t.smoke)));
                    }).dimensions(x + 300, y, 80, 20).build());

            if (!r.available) {
                r.flameBtn.active = false;
                r.smokeBtn.active = false;
            }
            y += 22;
        }

        // zakres scrolla
        int lastBottom = (rows.isEmpty() ? listStartY : rows.get(rows.size() - 1).baseY + 20);
        this.minScrollY  = Math.min(0, listEndY - lastBottom);
        this.targetScroll = 0.0;
        this.smoothScroll = 0.0;
        this.scrollY      = 0;
        applyScroll();

        // ——— TOP ———
        btnAllFlames = addDrawableChild(ButtonWidget.builder(
                Text.literal("All Flames: " + onOff(cfgLocal.disableAllFlames)), b -> {
                    cfgLocal.disableAllFlames = !cfgLocal.disableAllFlames;
                    btnAllFlames.setMessage(Text.literal("All Flames: " + onOff(cfgLocal.disableAllFlames)));
                }).dimensions(x, topY1, 160, 20).build());

        btnAllSmoke = addDrawableChild(ButtonWidget.builder(
                Text.literal("All Smoke: " + onOff(cfgLocal.disableAllSmoke)), b -> {
                    cfgLocal.disableAllSmoke = !cfgLocal.disableAllSmoke;
                    btnAllSmoke.setMessage(Text.literal("All Smoke: " + onOff(cfgLocal.disableAllSmoke)));
                }).dimensions(x + 180, topY1, 160, 20).build());

        btnApplyAll = addDrawableChild(ButtonWidget.builder(Text.literal("Apply to all"), b -> {
            applyGlobalToPerBlockLocal();
            syncUIFromConfig();
        }).dimensions(x, topY2, 110, 20).build());

        btnRestore = addDrawableChild(ButtonWidget.builder(Text.literal("Restore vanilla"), b -> {
            restoreVanillaLocal();
            syncUIFromConfig();
        }).dimensions(x + 120, topY2, 140, 20).build());

        // ——— BOTTOM ———
        int bottomY = this.height - 24;
        btnCancel = addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> {
            this.client.setScreen(parent); // porzuć zmiany
        }).dimensions(x, bottomY, 160, 20).build());

        btnSave = addDrawableChild(ButtonWidget.builder(Text.literal("Save & Quit"), b -> {
            ConfigManager.save(cfgLocal); // zapis na dysk
            this.client.setScreen(parent);
        }).dimensions(x + 180, bottomY, 160, 20).build());

        syncUIFromConfig();
    }

    /** przesuń elementy i wyłącz poza oknem listy */
    private void applyScroll() {
        for (Row r : rows) {
            int y = r.baseY + scrollY;
            r.labelY = y;

            r.flameBtn.setY(y);
            r.smokeBtn.setY(y);

            boolean visible = (y >= listStartY) && (y <= listEndY - 20);

            r.flameBtn.visible = visible;
            r.smokeBtn.visible = visible;

            r.flameBtn.active = visible && r.available;
            r.smokeBtn.active = visible && r.available;
        }
    }

    // —— SCROLL INPUT ——

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horiz, double vert) {
        if (minScrollY < 0) {
            double step = (vert > 0 ? 24.0 : -24.0);
            setTargetScroll(this.targetScroll + step);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horiz, vert);
    }

        @Override
    public boolean mouseClicked(Click click, boolean outside) {
        double mouseX = click.x();
        double mouseY = click.y();
        int button = click.button();

        if (minScrollY < 0 && isInsideScrollbar(mouseX, mouseY)) {
            this.draggingScrollbar = true;
            setTargetScroll(scrollFromThumb(mouseY));
            return true;
        }
        return super.mouseClicked(click, outside);
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        double mouseY = click.y();

        if (draggingScrollbar) {
            setTargetScroll(scrollFromThumb(mouseY));
            return true;
        }
        return super.mouseDragged(click, dx, dy);
    }

    @Override
    public boolean mouseReleased(Click click) {
        this.draggingScrollbar = false;
        return super.mouseReleased(click);
    }


    private boolean isInsideScrollbar(double mx, double my) {
        return mx >= scrollbarX && mx <= scrollbarX + SCROLLER_W
                && my >= listStartY && my <= listEndY;
    }

    private double scrollFromThumb(double mouseY) {
        int visible = listEndY - listStartY;

        int lastBottom = (rows.isEmpty() ? listStartY : rows.get(rows.size() - 1).baseY + 20);
        int content = lastBottom - listStartY;

        if (content <= visible) return 0.0;

        int thumbH = Math.max(20, (int)((double)visible * visible / (double)content));
        int track  = Math.max(1, visible - thumbH);
        double norm = (mouseY - listStartY - thumbH * 0.5) / track; // 0..1
        norm = clamp(norm, 0.0, 1.0);

        double range = 0.0 - (double)minScrollY;   // > 0
        double desired = -norm * range;            // 0..-range
        return clamp(desired, (double)minScrollY, 0.0);
    }

    private void setTargetScroll(double desired) {
        desired = clamp(desired, (double)minScrollY, 0.0);
        this.targetScroll = desired;
    }

    // —— RENDER ——

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {


        // tytuł (czysty tekst)
        ctx.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFFFF);

        // separatory: header i footer (vanilla tekstura)
        int headerSepY = this.listStartY - SEP_H;
        for (int x = 0; x < this.width; x += SEP_W) {
            int drawW = Math.min(SEP_W, this.width - x);
            ctx.drawTexture(RenderPipelines.GUI_TEXTURED, SEP_TEX, x, headerSepY,
                    0f, 0f, drawW, SEP_H, TEX_W, TEX_H);
        }

        int footerSepY = this.listEndY;
        for (int x = 0; x < this.width; x += SEP_W) {
            int drawW = Math.min(SEP_W, this.width - x);
            ctx.drawTexture(RenderPipelines.GUI_TEXTURED, SEP_TEX, x, footerSepY,
                    0f, 0f, drawW, SEP_H, TEX_W, TEX_H);
        }

        // widgety (przyciski) nad separatorami
        super.render(ctx, mouseX, mouseY, delta);

        // płynny scroll (lerp)
        if (minScrollY < 0) {
            double diff = (targetScroll - smoothScroll);
            if (Math.abs(diff) > 0.1) smoothScroll += diff * 0.25;
            else smoothScroll = targetScroll;

            int newY = (int)Math.round(smoothScroll);
            if (newY != scrollY) { scrollY = newY; applyScroll(); }
        }

        // etykiety lewej kolumny – czysty tekst (scissor = obszar listy)
        ctx.enableScissor(this.listX, this.listStartY, this.listX + 200, this.listEndY);
        for (Row r : rows) {
            if (r.labelY < listStartY || r.labelY > listEndY - 20) continue;
            int ty = r.labelY + (20 - this.textRenderer.fontHeight) / 2;
            ctx.drawTextWithShadow(this.textRenderer, r.label, this.listX + 6, ty, 0xFFFFFFFF);
        }
        ctx.disableScissor();

        // scrollbar (vanilla scroller.png; długość zależna od zawartości)
        if (minScrollY < 0) {
            int visible = listEndY - listStartY;
            int lastBottom = (rows.isEmpty() ? listStartY : rows.get(rows.size() - 1).baseY + 20);
            int content = lastBottom - listStartY;

            int thumbH = Math.max(20, (int)((double)visible * visible / (double)content));
            int track  = Math.max(1, visible - thumbH);

            double range = 0.0 - (double)minScrollY;
            double norm  = (-smoothScroll) / range; // 0..1
            norm = clamp(norm, 0.0, 1.0);
            int thumbY = listStartY + (int)Math.round(norm * track);

            Identifier scroller = pickScrollerTex(); // sprites/widget/scroller.png lub fallback
            if (scroller != null) {
                // top cap (1 px)
                ctx.drawTexture(RenderPipelines.GUI_TEXTURED, scroller,
                        scrollbarX, thumbY, 0f, 0f, SCROLLER_W, 1,
                        SCROLLER_W, SCROLLER_H_SRC);

                // middle (tiling 1 px – bez rozmycia przy wydłużaniu)
                int midHeight = Math.max(0, thumbH - 2);
                for (int i = 0; i < midHeight; i++) {
                    ctx.drawTexture(RenderPipelines.GUI_TEXTURED, scroller,
                            scrollbarX, thumbY + 1 + i, 0f, 1f, SCROLLER_W, 1,
                            SCROLLER_W, SCROLLER_H_SRC);
                }

                // bottom cap (1 px)
                ctx.drawTexture(RenderPipelines.GUI_TEXTURED, scroller,
                        scrollbarX, thumbY + thumbH - 1, 0f, SCROLLER_H_SRC - 1, SCROLLER_W, 1,
                        SCROLLER_W, SCROLLER_H_SRC);
            } else {
                // Fallback bez tekstury
                int outer = 0xFFB0B0B0;
                int inner = 0xFF404040;
                ctx.fill(scrollbarX, thumbY, scrollbarX + SCROLLER_W, thumbY + thumbH, outer);
                ctx.fill(scrollbarX + 1, thumbY + 1, scrollbarX + SCROLLER_W - 1, thumbY + thumbH - 1, inner);
            }
        }
    }

    // —— helpers ——

    private static boolean hasResource(Identifier id) {
        ResourceManager rm = MinecraftClient.getInstance().getResourceManager();
        return rm.getResource(id).isPresent();
    }

    private static Identifier pickScrollerTex() {
        if (hasResource(SCROLLER_TEX_WIDGET))  return SCROLLER_TEX_WIDGET;   // np. Twoje RP
        if (hasResource(SCROLLER_TEX_WIDGETS)) return SCROLLER_TEX_WIDGETS;  // inne RP / snapshoty
        return null; // brak -> fallback
    }

    private static String onOff(boolean disabled) { return disabled ? "OFF" : "ON"; }

    private static String pretty(String k) { return k.replace('_', ' '); }

    private static double clamp(double v, double min, double max) { return Math.max(min, Math.min(max, v)); }

    private boolean isKeyAvailableHint(String key) {
        if ("copper_torch".equals(key)) {
            for (Identifier id : Registries.BLOCK.getIds()) {
                if (id.getPath().contains("copper_torch")) return true;
            }
            return false;
        }
        if ("wall_copper_torch".equals(key)) {
            for (Identifier id : Registries.BLOCK.getIds()) {
                String p = id.getPath();
                if (p.contains("copper_wall_torch") || p.contains("wall_copper_torch")) return true;
            }
            return false;
        }
        return true;
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
            case "blast_furnace": return cfgLocal.blast_furnace;
            case "copper_torch":
                if (cfgLocal.copper_torch == null) cfgLocal.copper_torch = new NFSConfig.Toggle();
                return cfgLocal.copper_torch;
            case "wall_copper_torch":
                if (cfgLocal.wall_copper_torch == null) cfgLocal.wall_copper_torch = new NFSConfig.Toggle();
                return cfgLocal.wall_copper_torch;
            default:
                return cfgLocal.torch;
        }
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
        cfgLocal.disableAllSmoke  = false;
        for (NFSConfig.Toggle t : allTogglesOf(cfgLocal)) {
            t.flame = false;
            t.smoke = false;
        }
    }

    private static List<NFSConfig.Toggle> allTogglesOf(NFSConfig c) {
        List<NFSConfig.Toggle> list = new ArrayList<>();
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
        if (t == null) return new NFSConfig.Toggle();
        NFSConfig.Toggle n = new NFSConfig.Toggle();
        n.flame = t.flame;
        n.smoke = t.smoke;
        return n;
    }

    private void syncUIFromConfig() {
        if (btnAllFlames != null)
            btnAllFlames.setMessage(Text.literal("All Flames: " + onOff(cfgLocal.disableAllFlames)));
        if (btnAllSmoke != null)
            btnAllSmoke.setMessage(Text.literal("All Smoke: " + onOff(cfgLocal.disableAllSmoke)));

        for (Row r : rows) {
            NFSConfig.Toggle t = getToggle(r.key);
            if (r.flameBtn != null)
                r.flameBtn.setMessage(Text.literal("Flame: " + onOff(t.flame)));
            if (r.smokeBtn != null)
                r.smokeBtn.setMessage(Text.literal("Smoke: " + onOff(t.smoke)));

            if (r.flameBtn != null) r.flameBtn.active = r.flameBtn.visible && r.available;
            if (r.smokeBtn != null) r.smokeBtn.active = r.smokeBtn.visible && r.available;
        }
    }
}