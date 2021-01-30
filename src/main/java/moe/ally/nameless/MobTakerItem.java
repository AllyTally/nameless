package moe.ally.nameless;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class MobTakerItem extends Item {

    public MobTakerItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        CompoundTag tag = stack.getOrCreateTag();
        if (entity == null || stack == null || !entity.isAlive() || tag.contains("entity") || entity instanceof PlayerEntity) {
            return ActionResult.FAIL;
        }
        if (!user.world.isClient) {
            entity.stopRiding();
            entity.removeAllPassengers();
            CompoundTag entityData = new CompoundTag();
            if (!entity.saveToTag(entityData)) return ActionResult.FAIL;

            tag.put("entity", entityData);
            entity.remove();

            user.setStackInHand(hand, stack);
        }
        user.playSound(SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, 1.0F, 1.0F);
        return ActionResult.success(user.world.isClient);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockEntity tileEntity = world.getBlockEntity(pos);
        ItemStack stack = context.getStack();

        if (world.isClient) return ActionResult.PASS;

        BlockPos offset_pos = pos.offset(context.getSide());
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag compoundTag = (CompoundTag) tag.get("entity").copy();
        tag.remove("entity");
        compoundTag.remove("Passengers");
        compoundTag.remove("Leash");
        compoundTag.remove("UUID");
        compoundTag.remove("Motion");
        compoundTag.remove("OnGround");


        Entity entity = EntityType.loadEntityWithPassengers(compoundTag, world, (entityx) -> {
            return entityx;
        });

        entity.updatePosition(offset_pos.getX(),offset_pos.getY(),offset_pos.getZ());
        world.spawnEntity(entity);

        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return ActionResult.success(true);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        if (itemStack.getOrCreateTag().contains("entity")) {
            //tooltip.add("aaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getOrCreateTag().contains("entity");
    }
}
