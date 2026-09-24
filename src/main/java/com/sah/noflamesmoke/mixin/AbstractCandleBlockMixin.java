package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCandleBlock.class)
public abstract class AbstractCandleBlockMixin {

    @Shadow protected abstract Iterable<Vec3> getParticleOffsets(BlockState state);

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$separateCandleParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide() || !AbstractCandleBlock.isLit(state)) return;

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        final boolean showFlame = ConfigManager.allowFlameVisible(cfg.candles);
        final boolean showSmoke = ConfigManager.allowSmokeVisible(cfg.candles);

        // 🟢 jeśli wszystko dozwolone → zostaw vanilla
        if (showFlame && showSmoke) {
            return;
        }

        // 🔥 tylko gdy coś zmieniamy → anulujemy vanilla
        ci.cancel();

        // jeśli oba wyłączone → nic nie rysujemy
        if (!showFlame && !showSmoke) return;

        ParticleEngine pm = Minecraft.getInstance().particleEngine;

        for (Vec3 off : this.getParticleOffsets(state)) {
            double x = pos.getX() + off.x;
            double y = pos.getY() + off.y;
            double z = pos.getZ() + off.z;

            float chance = random.nextFloat();

            if (showSmoke && chance < 0.3F) {
                pm.createParticle(ParticleTypes.SMOKE, x, y + 0.01, z, 0.0, 0.008, 0.0);
            }

            if (showFlame) {
                pm.createParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }
}