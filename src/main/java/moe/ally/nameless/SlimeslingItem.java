package moe.ally.nameless;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class SlimeslingItem extends BowItem {

    //public static Hashtable slimeslingEntityVelocities = new Hashtable();

    public SlimeslingItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Exit condition 1: not being used by a player, or player is not on ground
        if (!(user instanceof PlayerEntity) || !user.isOnGround()) return;

        // Exit condition 2: no block is targeted
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() == Type.MISS) return;
        if (hit.getType() == Type.ENTITY && !(((EntityHitResult) hit).getEntity() instanceof LivingEntity)) return;

        // Get force
        float force = getPullProgress(getMaxUseTime(stack) - remainingUseTicks);
        force = (float) (Math.pow(force, 2) + (force * 2)) / 4f;
        force *= 4f;
        if (force > 6f) force = 6f;

        // For Players
        if (hit.getType() == Type.BLOCK && world.isClient) {
            // Apply force
            PlayerEntity playerEntity = (PlayerEntity) user;
            Vec3d vec = playerEntity.getRotationVec(0).normalize();
            playerEntity.addVelocity(vec.x * -force, vec.y * (-force / 3), vec.z * -force);

            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        // For Entities
        } else if (hit.getType() == Type.ENTITY && !world.isClient) {
            // Apply force
            LivingEntity mob = (LivingEntity) ((EntityHitResult) hit).getEntity();
            Vec3d vec = user.getRotationVec(0).normalize();

            // Set velocity
            mob.addVelocity(vec.x * force, vec.y * (force / 3), vec.z * force);
            ((LivingEntityAccess) mob).setNextVelocity(mob.getVelocity());
        }

        // The good stuff
        if (!world.isClient)
            world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1F, 1F);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(new TranslatableText("item.nameless.slimesling.tooltip1"));
        tooltip.add(new TranslatableText("item.nameless.slimesling.tooltip2"));
    }
}
