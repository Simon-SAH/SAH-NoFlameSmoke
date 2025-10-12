package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceBlock.class)
public class FurnaceBlockMixin {

    // ← korekty wysokości/dymu; jak będzie jeszcze za wysoko/za nisko, zmień Y_BASE lub Y_JITTER
    private static final double FRONT = 0.52D;        // wysunięcie od frontu (wanilla)
    private static final double SIDE_RANGE = 0.6D;    // rozrzut w poprzek (wanilla)
    private static final double Y_BASE = 0.12D;       // ↓ było 0.40 — obniżamy
    private static final double Y_JITTER = 0.12D;     // ↓ było 0.20 — mniejszy rozrzut
    private static final double SMOKE_OVER = 0.01D;   // dym trochę nad płomieniem

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nfs$splitParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isClient()) return;
        if (!state.getOrEmpty(Properties.LIT).orElse(false)) return;

        // zastępujemy wanillę, żeby rozdzielać flame/smoke wg configu
        ci.cancel();

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        final boolean showFlame = ConfigManager.allowFlameVisible(cfg.furnace);
        final boolean showSmoke = ConfigManager.allowSmokeVisible(cfg.furnace);
        if (!showFlame && !showSmoke) return;

        ParticleManager pm = MinecraftClient.getInstance().particleManager;

        Direction facing = state.get(FurnaceBlock.FACING);
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        double y  = pos.getY() + Y_BASE + random.nextDouble() * Y_JITTER;
        double side = random.nextDouble() * SIDE_RANGE - SIDE_RANGE / 2.0;

        double x = cx, z = cz;
        switch (facing) {
            case WEST  -> { x = cx - FRONT; z = cz + side; }
            case EAST  -> { x = cx + FRONT; z = cz + side; }
            case NORTH -> { x = cx + side;  z = cz - FRONT; }
            case SOUTH -> { x = cx + side;  z = cz + FRONT; }
        }

        if (showFlame) pm.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        if (showSmoke) pm.addParticle(ParticleTypes.SMOKE, x, y + SMOKE_OVER, z, 0, 0.01, 0);
    }
}