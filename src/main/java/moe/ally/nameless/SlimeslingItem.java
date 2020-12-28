package moe.ally.nameless;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SlimeslingItem extends BowItem {

    private double speed = 1;

    public SlimeslingItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Exit condition 1: server-side, not being used by a player, or player is not on ground
        if (!world.isClient || !(user instanceof PlayerEntity) || !user.isOnGround()) return;

        // Exit condition 2: no block is targeted
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        if (hit == null || hit.getType() != Type.BLOCK) return;

        // Get force
        float pullProgress = getPullProgress(getMaxUseTime(stack) - remainingUseTicks);
        float force = pullProgress;
        force = (float) (Math.pow(force, 2) + (force * 2)) / 4f;
        force *= 4f;
        if (force > 6f) force = 6f;
        force *= speed;

        // Apply force
        PlayerEntity playerEntity = (PlayerEntity) user;
        Vec3d vec = playerEntity.getRotationVec(0).normalize();
        playerEntity.addVelocity(vec.x * -force, vec.y * (-force / 3), vec.z * -force);

        playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(new TranslatableText("item.nameless.slimesling.tooltip"));
    }
}
