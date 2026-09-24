package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneWallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneWallTorchBlock.class)
public class RedstoneWallTorchBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$animateTick(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {

        if (!level.isClientSide()) return;

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        NFSConfig.Toggle t = cfg.redstone_torch;

        boolean allowSmoke = ConfigManager.allowSmokeVisible(cfg.redstone_torch);

        if (allowSmoke) return;

        ci.cancel();
    }
}