package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FurnaceBlock.class)
public class FurnaceBlockMixin {

    private static final double FRONT = 0.52D;
    private static final double SIDE_RANGE = 0.6D;
    private static final double Y_BASE = 0.12D;
    private static final double Y_JITTER = 0.12D;
    private static final double SMOKE_OVER = 0.01D;

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$splitParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide()) return;
        if (!state.getOptionalValue(BlockStateProperties.LIT).orElse(false)) return;

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        final boolean showFlame = ConfigManager.allowFlameVisible(cfg.furnace);
        final boolean showSmoke = ConfigManager.allowSmokeVisible(cfg.furnace);

        // 🟢 jeśli oba włączone → zostaw vanilla
        if (showFlame && showSmoke) return;

        // 🔥 przejmujemy kontrolę
        ci.cancel();

        // ❌ oba wyłączone → nic nie rysujemy
        if (!showFlame && !showSmoke) return;

        ParticleEngine pm = Minecraft.getInstance().particleEngine;

        Direction facing = state.getValue(FurnaceBlock.FACING);
        double cx = pos.getX() + 0.5;
        double cz = pos.getZ() + 0.5;
        double y  = pos.getY() + 0.12 + random.nextDouble() * 0.12;
        double side = random.nextDouble() * 0.6 - 0.3;

        double x = cx, z = cz;
        switch (facing) {
            case WEST  -> { x = cx - 0.52; z = cz + side; }
            case EAST  -> { x = cx + 0.52; z = cz + side; }
            case NORTH -> { x = cx + side; z = cz - 0.52; }
            case SOUTH -> { x = cx + side; z = cz + 0.52; }
        }

        // 🔥 płomień
        if (showFlame) {
            pm.createParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }

        // 💨 dym (losowy jak w vanilla)
        if (showSmoke && random.nextFloat() < 0.3F) {
            pm.createParticle(ParticleTypes.SMOKE, x, y + 0.01, z, 0, 0.01, 0);
        }
    }
}