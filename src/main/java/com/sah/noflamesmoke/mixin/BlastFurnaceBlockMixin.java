package com.sah.noflamesmoke.mixin;

import com.sah.noflamesmoke.config.ConfigManager;
import com.sah.noflamesmoke.config.NFSConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlastFurnaceBlock.class)

public class BlastFurnaceBlockMixin {

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void nfs$splitParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {

        if (!level.isClientSide()) return;
        if (!state.getOptionalValue(BlockStateProperties.LIT).orElse(false)) return;

        NFSConfig cfg = ConfigManager.get();
        if (cfg == null) return;

        final boolean showSmoke = ConfigManager.allowSmokeVisible(cfg.blast_furnace);

        // 🔥 ZAWSZE anulujemy vanilla
        ci.cancel();

        // ❌ jeśli wyłączony → nic nie robimy
        if (!showSmoke) return;

        // 🟢 jeśli włączony → ODTWARZAMY vanilla ręcznie

        double x = pos.getX() + 0.5;
        double y = pos.getY();
        double z = pos.getZ() + 0.5;

        if (random.nextDouble() < 0.1) {
            level.playLocalSound(x, y, z,
                    net.minecraft.sounds.SoundEvents.BLASTFURNACE_FIRE_CRACKLE,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    1.0F, 1.0F, false);
        }

        Direction direction = state.getValue(BlastFurnaceBlock.FACING);
        Direction.Axis axis = direction.getAxis();

        double ss = random.nextDouble() * 0.6 - 0.3;
        double dx = axis == Direction.Axis.X ? direction.getStepX() * 0.52 : ss;
        double dy = random.nextDouble() * 9.0 / 16.0;
        double dz = axis == Direction.Axis.Z ? direction.getStepZ() * 0.52 : ss;

        level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
    }
}