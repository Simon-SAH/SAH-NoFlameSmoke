package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallTorchBlock.class)
public class WallTorchBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$randomDisplayTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (!level.isClientSide()) return;

        boolean isSoul = state.is(Blocks.SOUL_WALL_TORCH);
        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        boolean isCopper = state.is(Blocks.COPPER_WALL_TORCH);

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        NFSConfig.Toggle t = isSoul
                ? cfg.wall_soul_torch
                : (isCopper && cfg.wall_copper_torch != null ? cfg.wall_copper_torch : cfg.wall_torch);

        boolean allowFlame = !t.flame;
        boolean allowSmoke = !t.smoke;

        if (allowFlame && allowSmoke) return;

        ci.cancel();

        ParticleEngine pm = Minecraft.getInstance().particleEngine;

        Direction facing = state.getValue(WallTorchBlock.FACING);

        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.7D;
        double cz = pos.getZ() + 0.5D;

        double offXZ = 0.27D;
        double offY  = 0.22D;

        double x = cx - offXZ * facing.getStepX();
        double y = cy + offY;
        double z = cz - offXZ * facing.getStepZ();

        if (allowSmoke) {
            pm.createParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        }

        ParticleOptions flame;

        if (isSoul) {
            flame = ParticleTypes.SOUL_FIRE_FLAME;
        } else if (isCopper) {
            flame = ParticleTypes.COPPER_FIRE_FLAME;
        } else {
            flame = ParticleTypes.FLAME;
        }
        if (allowFlame) {
            pm.createParticle(flame, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}