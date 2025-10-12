package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneTorchBlock.class)
public class RedstoneTorchBlockMixin {

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nfs$randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isClient()) return;

        NFSConfig.Toggle t = ConfigManager.get().redstone_torch;
        boolean allowFlame = !t.flame;
        boolean allowSmoke = !t.smoke;

        if (allowFlame && allowSmoke) return; // nic nie wyłączamy → vanilla

        ci.cancel();

        ParticleManager pm = MinecraftClient.getInstance().particleManager;

        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.7D;
        double z = pos.getZ() + 0.5D;

        if (allowSmoke) add(pm, ParticleTypes.SMOKE, x, y, z);
        if (allowFlame) add(pm, ParticleTypes.FLAME, x, y, z);
    }

    private static void add(ParticleManager pm, ParticleEffect effect, double x, double y, double z) {
        pm.addParticle(effect, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}