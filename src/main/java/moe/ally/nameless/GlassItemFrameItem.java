package moe.ally.nameless;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class GlassItemFrameItem extends Item {
    public GlassItemFrameItem(Item.Settings settings) {
        super(settings);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos blockPos2 = blockPos.offset(direction);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();

        if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
            return ActionResult.FAIL;
        } else {
            World world = context.getWorld();
            GlassItemFrameEntity abstractDecorationEntity3 = GlassItemFrameEntity.summon(world, blockPos2, direction);

            NbtCompound compoundTag = itemStack.getTag();
            if (compoundTag != null)
                EntityType.loadFromEntityNbt(world, playerEntity, (Entity)abstractDecorationEntity3, compoundTag);

            if (((AbstractDecorationEntity)abstractDecorationEntity3).canStayAttached()) {
                if (!world.isClient) {
                    ((AbstractDecorationEntity)abstractDecorationEntity3).onPlace();
                    world.spawnEntity((Entity)abstractDecorationEntity3);
                }

                itemStack.decrement(1);
                return ActionResult.success(world.isClient);
            } else {
                return ActionResult.CONSUME;
            }
        }
    }

    protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !player.world.isOutOfHeightLimit(pos) && player.canPlaceOn(pos, side, stack);
    }
}
