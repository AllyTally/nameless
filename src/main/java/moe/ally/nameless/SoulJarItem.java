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
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SoulJarItem extends Item {

    public SoulJarItem(Settings settings) {
        super(settings);
    }

    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        ItemStack newStack = captureEntity(stack,user,entity);
        if (newStack != null) {
            user.setStackInHand(hand, newStack);
            return ActionResult.success(user.world.isClient);
        }
        return ActionResult.FAIL;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockEntity tileEntity = world.getBlockEntity(pos);
        ItemStack stack = context.getStack();

        if (spawnEntity(pos,context.getSide(),world,stack)) {
            return ActionResult.success(true);
        }
        return ActionResult.PASS;

    }

    public ItemStack captureEntity(ItemStack stack, @Nullable PlayerEntity user, LivingEntity entity) {
        CompoundTag tag = stack.getOrCreateTag();
        if (entity == null || stack == null || !entity.isAlive() || tag.contains("entity") || entity instanceof PlayerEntity) {
            return null;
        }
        if (user != null && user.world.isClient) return null;


        entity.stopRiding();
        entity.removeAllPassengers();
        CompoundTag entityData = new CompoundTag();
        if (!entity.saveToTag(entityData)) return null;

        tag.put("entity", entityData);
        tag.putString("entityName", entity.getDisplayName().getString());


        entity.getEntityWorld().playSound(null, entity.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.BLOCKS, 1.0F, 1.0F);
        entity.remove();
        return stack;
    }


    public boolean spawnEntity(BlockPos pos, Direction direction, World world, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("entity")) return false;
        if (world.isClient) return false;

        BlockPos offset_pos = pos.offset(direction);
        CompoundTag compoundTag = (CompoundTag) tag.get("entity").copy();
        tag.remove("entity");
        tag.remove("entityName");
        compoundTag.remove("Passengers");
        compoundTag.remove("Leash");
        compoundTag.remove("UUID");
        compoundTag.remove("Motion");
        compoundTag.remove("OnGround");
        compoundTag.remove("FallDistance");


        Entity entity = EntityType.loadEntityWithPassengers(compoundTag, world, (entityx) -> {
            return entityx;
        });

        entity.updatePosition(offset_pos.getX() + 0.5D,offset_pos.getY(),offset_pos.getZ() + 0.5D);
        world.spawnEntity(entity);

        world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (itemStack.getOrCreateTag().contains("entity")) {
            tooltip.add(new TranslatableText("item.nameless.soul_jar.tooltip", tag.getString("entityName")));
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getOrCreateTag().contains("entity");
    }
}
