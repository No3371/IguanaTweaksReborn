package insane96mcp.iguanatweaksreborn.modules;

import insane96mcp.iguanatweaksreborn.setup.ModConfig;
import insane96mcp.iguanatweaksreborn.utils.MCUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class MiningModule {
    public static void ProcessHardness(PlayerEvent.BreakSpeed event) {
        processGlobalHardness(event);
        processSingleHardness(event);
    }

    public static void processGlobalHardness(PlayerEvent.BreakSpeed event) {
        if (!ModConfig.Modules.hardness)
            return;
        if (ModConfig.Mining.hardnessMultiplier == 1.0d && ModConfig.Mining.dimensionHardnessMultiplier.size() == 0)
            return;
        World world = event.getPlayer().world;
        //Dimension dimension = world.getDimensionKey().getRegistryName();
        ResourceLocation dimensionId = world.getDimensionKey().getLocation();
        if (dimensionId == null)
            dimensionId = MCUtils.AnyRL;
        BlockState blockState = world.getBlockState(event.getPos());
        Block block = blockState.getBlock();
        double multiplier = 1d / getBlockGlobalHardness(block, dimensionId);
        if (multiplier == 1d)
            return;
        event.setNewSpeed((float) (event.getNewSpeed() * multiplier));
    }

    public static void processSingleHardness(PlayerEvent.BreakSpeed event) {
        if (!ModConfig.Modules.hardness)
            return;
        if (ModConfig.Mining.customHardness.size() == 0)
            return;
        World world = event.getPlayer().world;
        //Dimension dimension = world.getDimensionKey().getRegistryName();
        ResourceLocation dimensionId = world.getDimensionKey().getLocation();

        BlockPos pos = event.getPos();
        BlockState blockState = world.getBlockState(pos);

        Block block = blockState.getBlock();
        double hardness = getBlockSingleHardness(block, dimensionId);
        if (hardness == -1d)
            return;
        event.setNewSpeed(event.getNewSpeed() * (float) getRatio(hardness, blockState, world, pos));
    }

    private static double getRatio(double newHardness, BlockState state, World world, BlockPos pos) {
        return state.getBlockHardness(world, pos) / newHardness;
    }

    /**
     * Returns -1 when no changes must be made, else will return a divider for the block breaking speed (aka multiplier for block hardness)
     */
    public static double getBlockGlobalHardness(Block block, ResourceLocation dimensionId) {
        for (ModConfig.Mining.BlockHardness blockHardness : ModConfig.Mining.customHardness)
            if (MCUtils.isInTagOrBlock(blockHardness, block, dimensionId))
                return 1d;
        boolean isInWhitelist = false;
        for (ModConfig.IdTagMatcher blacklistEntry : ModConfig.Mining.hardnessBlacklist) {
            if (!ModConfig.Mining.blacklistAsWhitelist) {
                if (MCUtils.isInTagOrBlock(blacklistEntry, block, dimensionId))
                    return 1d;
            }
            else {
                if (MCUtils.isInTagOrBlock(blacklistEntry, block, dimensionId)) {
                    isInWhitelist = true;
                    break;
                }
            }
        }
        if (!isInWhitelist && ModConfig.Mining.blacklistAsWhitelist)
            return 1d;
        double multiplier = ModConfig.Mining.hardnessMultiplier;
        for (ModConfig.Mining.DimensionHardnessMultiplier dimensionHardnessMultiplier : ModConfig.Mining.dimensionHardnessMultiplier) {
            if (dimensionId.equals(dimensionHardnessMultiplier.dimension)) {
                multiplier = dimensionHardnessMultiplier.multiplier;
                break;
            }
        }
        return multiplier;
    }

    /**
     * Returns -1 when the block has no custom hardness, the hardness otherwise
     */
    public static double getBlockSingleHardness(Block block, ResourceLocation dimensionId) {
        for (ModConfig.Mining.BlockHardness blockHardness : ModConfig.Mining.customHardness) {
            if (MCUtils.isInTagOrBlock(blockHardness, block, dimensionId)) {
                return blockHardness.hardness;
            }
        }
        return -1d;
    }
}
