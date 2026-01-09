package com.sah.noflamesmoke.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Zarządzanie konfiguracją NFS (No Flame & Smoke).
 *
 * W pliku JSON trzymamy semantykę "disable":
 *  - disableAllFlames == true  -> globalnie wyłącz płomienie
 *  - toggle.flame == true      -> wyłącz płomień dla danego bloku
 *
 * UI pokazuje stan *widoczności* ("ON" = efekt widać), czyli odwraca te flagi.
 */
public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "noflamesmoke.json";
    private static NFSConfig CONFIG;

    public static NFSConfig get() {
        if (CONFIG == null) CONFIG = load();
        return CONFIG;
    }

    public static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    public static synchronized void save() {
        // zapisuje aktualny CONFIG
        try {
            Path path = configPath();
            Files.createDirectories(path.getParent());
            try (Writer w = new OutputStreamWriter(new FileOutputStream(path.toFile()), StandardCharsets.UTF_8)) {
                GSON.toJson(CONFIG, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void save(NFSConfig c) {
        // ustala CONFIG i zapisuje
        CONFIG = c;
        save();
    }

    private static NFSConfig load() {
        Path path = configPath();
        if (Files.exists(path)) {
            try (Reader r = new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8)) {
                NFSConfig c = GSON.fromJson(r, NFSConfig.class);
                return (c != null) ? c : new NFSConfig();
            } catch (IOException ignored) {}
        }
        return new NFSConfig();
    }

    /** Reset do vanilli i zapis. */
    public static void restoreVanilla() {
        CONFIG = new NFSConfig();
        save();
    }

    /** Masowe zastosowanie globalnych przełączników na per-blok. */
    public static void applyGlobalToPerBlock() {
        NFSConfig c = get();
        boolean allFlames = c.disableAllFlames;
        boolean allSmoke  = c.disableAllSmoke;

        // torches
        apply(c.torch, allFlames, allSmoke);
        apply(c.wall_torch, allFlames, allSmoke);
        apply(c.redstone_torch, allFlames, allSmoke);
        apply(c.wall_redstone_torch, allFlames, allSmoke);
        apply(c.soul_torch, allFlames, allSmoke);
        apply(c.wall_soul_torch, allFlames, allSmoke);
        // opcjonalne miedziane (jeśli pole istnieje w Twoim NFSConfig)
        apply(c.copper_torch, allFlames, allSmoke);
        apply(c.wall_copper_torch, allFlames, allSmoke);

        // candles
        apply(c.candles, allFlames, allSmoke);

        // furnaces
        apply(c.furnace, allFlames, allSmoke);
        apply(c.smoker, allFlames, allSmoke);
        apply(c.blast_furnace, allFlames, allSmoke);

        save();
    }

    /** Ustawia parę flag z null-safem. */
    private static void apply(NFSConfig.Toggle t, boolean flameDisable, boolean smokeDisable) {
        if (t == null) return;
        t.flame = flameDisable;
        t.smoke = smokeDisable;
    }

    // ------------------------------------------------------------
    // Helpers do użycia w mixinach/UI: true = efekt MA BYĆ widoczny
    // ------------------------------------------------------------
    public static boolean allowFlameVisible(NFSConfig.Toggle t) {
        NFSConfig c = get();
        // widzimy płomień gdy nie ma globalnego wyłączenia i lokalnie też NIE wyłączono
        return c != null && !c.disableAllFlames && t != null && !t.flame;
    }

    public static boolean allowSmokeVisible(NFSConfig.Toggle t) {
        NFSConfig c = get();
        return c != null && !c.disableAllSmoke && t != null && !t.smoke;
    }
}