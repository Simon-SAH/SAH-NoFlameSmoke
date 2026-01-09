package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractCandleBlock.class)
public abstract class AbstractCandleBlockMixin {

    @Shadow protected abstract Iterable<Vec3d> getParticleOffsets(BlockState state);

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nfs$separateCandleParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isClient() || !AbstractCandleBlock.isLitCandle(state)) return;

        // wyłączamy vanilla – rysujemy selektywnie (flame/smoke osobno)
        ci.cancel();

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        final boolean showFlame = ConfigManager.allowFlameVisible(cfg.candles);
        final boolean showSmoke = ConfigManager.allowSmokeVisible(cfg.candles);
        if (!showFlame && !showSmoke) return;

        ParticleManager pm = MinecraftClient.getInstance().particleManager;

        // WANILLA: pozycja = pos + offset (offsety nie są względem środka!)
        for (Vec3d off : this.getParticleOffsets(state)) {
            double x = pos.getX() + off.x;
            double y = pos.getY() + off.y;
            double z = pos.getZ() + off.z;

            if (showFlame) pm.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.0, 0.0);
            if (showSmoke) pm.addParticle(ParticleTypes.SMOKE,      x, y + 0.01, z, 0.0, 0.008, 0.0);
        }
    }
}