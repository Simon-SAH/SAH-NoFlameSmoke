package com.sah.noflamesmoke.config;

public class NFSConfig {
    public boolean disableAllFlames = false;
    public boolean disableAllSmoke  = false;

    public Toggle torch                = new Toggle();
    public Toggle wall_torch           = new Toggle();
    public Toggle redstone_torch       = new Toggle();
    public Toggle wall_redstone_torch  = new Toggle();
    public Toggle soul_torch           = new Toggle();
    public Toggle wall_soul_torch      = new Toggle();
    public Toggle candles              = new Toggle();
    public Toggle copper_torch         = new Toggle();
    public Toggle wall_copper_torch    = new Toggle();
    public Toggle furnace              = new Toggle();
    public Toggle smoker               = new Toggle();
    public Toggle blast_furnace        = new Toggle();

    public static class Toggle {
        public boolean flame = false;
        public boolean smoke = false;
    }
}
