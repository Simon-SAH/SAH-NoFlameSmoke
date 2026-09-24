package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.block.RedstoneTorchBlock;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {


    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$randomDisplayTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide()) return;
        if (state.getBlock() instanceof RedstoneTorchBlock) return;

        boolean isSoul = state.is(Blocks.SOUL_TORCH);
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        boolean isCopper = state.is(Blocks.COPPER_TORCH);

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        NFSConfig.Toggle t = isSoul
                ? cfg.soul_torch
                : (isCopper && cfg.copper_torch != null ? cfg.copper_torch : cfg.torch);

        boolean allowFlame = !t.flame;
        boolean allowSmoke = !t.smoke;

        if (allowFlame && allowSmoke) return; // nic nie wyłączamy → vanilla

        ci.cancel(); // sami narysujemy dozwolone cząstki

        ParticleEngine pm = Minecraft.getInstance().particleEngine;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.7D;
        double z = pos.getZ() + 0.5D;

        if (allowSmoke) {
            pm.createParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        }

        ParticleOptions flame;

        if (isSoul) {
            flame = ParticleTypes.SOUL_FIRE_FLAME;
        } else if (isCopper) {
            flame = ParticleTypes.COPPER_FIRE_FLAME; // ✔ właściwy particle
        } else {
            flame = ParticleTypes.FLAME;
        }
        if (allowFlame) {
            pm.createParticle(flame, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}