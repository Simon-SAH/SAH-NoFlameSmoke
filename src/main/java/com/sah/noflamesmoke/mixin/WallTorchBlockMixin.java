package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallTorchBlock.class)
public class WallTorchBlockMixin {

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nfs$randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isClient()) return;

        boolean isSoul = state.isOf(Blocks.SOUL_WALL_TORCH);
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        boolean isCopper = path.contains("copper_wall_torch") || path.contains("wall_copper_torch");

        NFSConfig cfg = ConfigManager.get();
        NFSConfig.Toggle t = isSoul
                ? cfg.wall_soul_torch
                : (isCopper && cfg.wall_copper_torch != null ? cfg.wall_copper_torch : cfg.wall_torch);

        boolean allowFlame = !t.flame;
        boolean allowSmoke = !t.smoke;

        if (allowFlame && allowSmoke) return;

        ci.cancel();

        ParticleManager pm = MinecraftClient.getInstance().particleManager;

        // prawidłowe pozycje dla wall torch (vanilla offsets)
        Direction facing = state.get(WallTorchBlock.FACING);

        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.7D;
        double cz = pos.getZ() + 0.5D;

        double offXZ = 0.27D;
        double offY  = 0.22D;

        double x = cx - offXZ * facing.getOffsetX();
        double y = cy + offY;
        double z = cz - offXZ * facing.getOffsetZ();

        if (allowSmoke) pm.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);

        ParticleEffect flame = isSoul ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
        if (allowFlame) pm.addParticle(flame, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}