package moe.ally.nameless;

import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class SpikeBlock extends Block {

    public SpikeBlock(Settings settings) {
        super(settings);
    }

    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) {
            entity.damage(DamageSource.GENERIC, 1.0F);
            if (((LivingEntity) entity).isDead()) {
                System.out.println("asdf");
            }
        }

        super.onSteppedOn(world, pos, entity);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return Stream.of(
                Block.createCuboidShape(7, 12, 7, 9, 14, 9),
                Block.createCuboidShape(6, 10, 6, 10, 12, 10),
                Block.createCuboidShape(5, 8, 5, 11, 10, 11),
                Block.createCuboidShape(4, 6, 4, 12, 8, 12),
                Block.createCuboidShape(3, 4, 3, 13, 6, 13),
                Block.createCuboidShape(2, 2, 2, 14, 4, 14),
                Block.createCuboidShape(1, 0, 1, 15, 2, 15)
        ).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR);}).get();
    }
}
