package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallRedstoneTorchBlock.class)
public class WallRedstoneTorchBlockMixin {

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nfs$randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isClient()) return;

        NFSConfig.Toggle t = ConfigManager.get().wall_redstone_torch;
        boolean allowFlame = !t.flame;
        boolean allowSmoke = !t.smoke;

        if (allowFlame && allowSmoke) return;

        ci.cancel();

        ParticleManager pm = MinecraftClient.getInstance().particleManager;

        Direction facing = state.get(WallRedstoneTorchBlock.FACING);

        double cx = pos.getX() + 0.5D;
        double cy = pos.getY() + 0.7D;
        double cz = pos.getZ() + 0.5D;

        double offXZ = 0.27D;
        double offY  = 0.22D;

        double x = cx - offXZ * facing.getOffsetX(); // ważne: minus
        double y = cy + offY;
        double z = cz - offXZ * facing.getOffsetZ();

        if (allowSmoke) add(pm, ParticleTypes.SMOKE, x, y, z);
        if (allowFlame) add(pm, ParticleTypes.FLAME, x, y, z);
    }

    private static void add(ParticleManager pm, ParticleEffect effect, double x, double y, double z) {
        pm.addParticle(effect, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}