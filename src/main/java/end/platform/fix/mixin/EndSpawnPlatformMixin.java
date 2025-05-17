package end.platform.fix.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.feature.EndPlatformFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndPlatformFeature.class)
public class EndSpawnPlatformMixin {
    @Inject(method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Z)V", at = @At("HEAD"), cancellable = true)
    private static void redirectGenerate(ServerWorldAccess world, BlockPos pos, boolean breakBlocks, CallbackInfo ci) {
        BlockPos.Mutable mutable = pos.mutableCopy();

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                for (int k = -1; k < 3; k++) {
                    BlockPos blockPos = mutable.set(pos).move(j, k, i);
                    Block targetBlock = k == -1 ? Blocks.OBSIDIAN : Blocks.AIR;

                    // Skip if it's already the correct block
                    if (world.getBlockState(blockPos).isOf(targetBlock)) continue;

                    // ⛔ Skip replacing water with air
                    if (targetBlock == Blocks.AIR && world.getBlockState(blockPos).isOf(Blocks.WATER)) continue;

                    if (breakBlocks) {
                        world.breakBlock(blockPos, true, null);
                    }

                    world.setBlockState(blockPos, targetBlock.getDefaultState(), Block.NOTIFY_ALL);
                }
            }
        }

        ci.cancel(); // Cancel original method
    }
}