package com.sah.noflamesmoke;

import net.fabricmc.api.ClientModInitializer;

public class NoFlameSmokeMod implements ClientModInitializer {
    public static final String MOD_ID = "noflamesmoke";

    @Override
    public void onInitializeClient() {
        // na razie nic — logika render/particle pójdzie w mixinach później
    }
}