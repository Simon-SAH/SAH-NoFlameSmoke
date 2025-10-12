package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TorchBlock.class)
public class TorchBlockMixin {

    @Shadow @Final
    protected SimpleParticleType particle; // vanilla flame dla tej instancji

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void nfs$randomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isClient()) return;

        // rozróżnienie typów pochodni
        boolean isSoul = state.isOf(Blocks.SOUL_TORCH);
        String path = Registries.BLOCK.getId(state.getBlock()).getPath();
        boolean isCopper = path.contains("copper_torch");

        NFSConfig cfg = ConfigManager.get();
        NFSConfig.Toggle t = isSoul
                ? cfg.soul_torch
                : (isCopper && cfg.copper_torch != null ? cfg.copper_torch : cfg.torch);

        boolean allowFlame = !t.flame;
        boolean allowSmoke = !t.smoke;

        if (allowFlame && allowSmoke) return; // nic nie wyłączamy → vanilla

        ci.cancel(); // sami narysujemy dozwolone cząstki

        ParticleManager pm = MinecraftClient.getInstance().particleManager;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.7D;
        double z = pos.getZ() + 0.5D;

        if (allowSmoke) pm.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);

        ParticleEffect flame = isSoul ? ParticleTypes.SOUL_FIRE_FLAME : this.particle;
        if (allowFlame) pm.addParticle(flame, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}