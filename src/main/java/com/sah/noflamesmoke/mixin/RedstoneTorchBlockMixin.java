package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {

        if (!level.isClientSide()) return;
        if (!state.getValue(RedstoneTorchBlock.LIT)) return;

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        boolean allow = ConfigManager.allowSmokeVisible(cfg.redstone_torch);

        // 🟢 vanilla
        if (allow) return;

        // 🔥 blokujemy redstone particle
        ci.cancel();
    }

    private static void add(ParticleEngine pm, ParticleOptions effect, double x, double y, double z) {
        pm.createParticle(effect, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}