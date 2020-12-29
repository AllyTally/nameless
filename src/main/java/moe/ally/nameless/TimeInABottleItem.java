package moe.ally.nameless;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class TimeInABottleItem extends Item {

    public TimeInABottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockEntity tileEntity = world.getBlockEntity(pos);
        ItemStack stack = context.getStack();

        if (world.isClient) return ActionResult.PASS;

        if (tileEntity == null) return ActionResult.PASS;
        if (!(tileEntity instanceof Tickable)) return ActionResult.PASS;

        CompoundTag tag = stack.getOrCreateTag();
        int timeStored = tag.getInt("timeStored");

        Optional<TickerEntity> tickersInBlock = world.getNonSpectatingEntities(TickerEntity.class, new Box(pos).shrink(0.2, 0.2, 0.2)).stream().findFirst();

        if(tickersInBlock.isPresent()) {
            TickerEntity ticker = tickersInBlock.get();
            int speed = ticker.getSpeed();
            if (speed == 6) return ActionResult.success(false);
            if (timeStored < 600 * (speed + 1)) {
                return ActionResult.PASS;
            }
            speed++;
            ticker.setSpeed(speed);
            tag.putInt("timeStored", timeStored - (600 * speed));
            switch (speed) {
                case 2:
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.793701F);
                    break;
                case 3:
                case 6:
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.890899F);
                    break;
                case 4:
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.059463F);
                    break;
                case 5:
                    world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.943874F);
                    break;
            }
            return ActionResult.success(true);
        }

        if (timeStored < 600) {
            return ActionResult.PASS;
        }
        tag.putInt("timeStored", timeStored - 600);

        Nameless.TICKER.spawn((ServerWorld) world, null, null, null, pos, SpawnReason.TRIGGERED, false, false);

        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.749154F);
        return ActionResult.success(true);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) return;
        if (!(entity instanceof PlayerEntity)) return;

        if (world.getTime() % 20 == 0) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.getInt("timeStored") < 622080000) {
                tag.putInt("timeStored", tag.getInt("timeStored") + 20);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        CompoundTag tag = itemStack.getOrCreateTag();
        int storedSeconds = tag.getInt("timeStored") / 20;

        int hours = storedSeconds / 3600;
        int minutes = (storedSeconds % 3600) / 60;
        int seconds = storedSeconds % 60;
        tooltip.add(new TranslatableText("item.nameless.time_in_a_bottle.tooltip", hours, minutes, seconds));
    }
}
