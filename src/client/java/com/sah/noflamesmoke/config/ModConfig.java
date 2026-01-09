package com.sah.noflamesmoke.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "noflamesmoke")
public class ModConfig implements ConfigData {

    @ConfigEntry.Category("master")
    @ConfigEntry.Gui.Tooltip
    public boolean disableAll = false; // wyłącza wszystko jednym kliknięciem

    @ConfigEntry.Category("particles")
    @ConfigEntry.Gui.Tooltip
    public boolean disableFlame = true;

    @ConfigEntry.Category("particles")
    @ConfigEntry.Gui.Tooltip
    public boolean disableSmoke = true;

    @ConfigEntry.Category("scope")
    @ConfigEntry.Gui.Tooltip
    public boolean onlySelectedBlocks = true; // jeśli false – wycisza globalnie

    @ConfigEntry.Category("blocks")
    public boolean affectTorches = true;           // Torch / WallTorch (+ redstone)

    @ConfigEntry.Category("blocks")
    public boolean affectSoulVariants = true;      // soul torch / campfire

    @ConfigEntry.Category("blocks")
    public boolean affectCampfires = true;         // ogniska

    @ConfigEntry.Category("blocks")
    public boolean affectCandles = true;           // świece

    @ConfigEntry.Category("blocks")
    public boolean affectFurnaces = true;          // piec / wędzarka / hutniczy (gdy LIT)

    @ConfigEntry.Category("blocks")
    public boolean affectLanterns = false;         // latarnie (opcjonalnie)
}
