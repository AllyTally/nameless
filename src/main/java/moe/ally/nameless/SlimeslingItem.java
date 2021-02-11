package moe.ally.nameless;

import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.*;

public class SlimeslingItem extends BowItem {

    public SlimeslingItem(Settings settings) {
        super(settings);
    }

    private float reachDistance = 4;

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // Exit condition 1: running on client, not being used by a player
        if (world.isClient || !(user instanceof PlayerEntity) /*|| !user.isOnGround()*/) return;

        // Get force
        float force = getPullProgress(getMaxUseTime(stack) - remainingUseTicks);
        force = (float) (Math.pow(force, 2) + (force * 2)) / 4f;
        force *= 4f;
        if (force > 6f) force = 6f;

        // Target an entity
        Vec3d pos = user.getCameraPosVec(0.0F);
        Vec3d ray = pos.add(user.getRotationVector().multiply(reachDistance));

        EntityHitResult entityHitResult = net.minecraft.entity.projectile.ProjectileUtil.getEntityCollision(world, user, pos, ray, user.getBoundingBox().expand(reachDistance), entity -> true);

        // For Mobs
        if (entityHitResult != null && entityHitResult.getEntity() instanceof LivingEntity) {
            // Apply force
            Entity mob = (LivingEntity) entityHitResult.getEntity();
            Vec3d vec = user.getRotationVec(0).normalize();

            // Set velocity
            mob.addVelocity(vec.x * force, vec.y * (force / 3), vec.z * force);
            Packet packet = new EntityVelocityUpdateS2CPacket(mob.getEntityId(), mob.getVelocity());
            PlayerStream.all(world.getServer()).forEach(serverPlayerEntity -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(serverPlayerEntity, packet));
        // For Players
        } else {
            // Target a block
            BlockHitResult blockHitResult = world.raycast(new RaycastContext(pos, ray, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, user));

            if (blockHitResult.getType() == Type.BLOCK) {
                // Apply force
                PlayerEntity playerEntity = (PlayerEntity) user;
                Vec3d vec = playerEntity.getRotationVec(0).normalize();
                playerEntity.addVelocity(vec.x * -force, vec.y * (-force / 3), vec.z * -force);
                Packet packet = new EntityVelocityUpdateS2CPacket(playerEntity.getEntityId(), playerEntity.getVelocity());
                PlayerStream.all(world.getServer()).forEach(serverPlayerEntity -> ServerSidePacketRegistry.INSTANCE.sendToPlayer(serverPlayerEntity, packet));
            } else
                return;
        }

        // The good stuff
        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1F, 1F);
        ((PlayerEntity)user).incrementStat(Stats.USED.getOrCreateStat(this));
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient)
            reachDistance = MinecraftClient.getInstance().interactionManager.getReachDistance();
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack itemStack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        tooltip.add(new TranslatableText("item.nameless.slimesling.tooltip1"));
        tooltip.add(new TranslatableText("item.nameless.slimesling.tooltip2"));
    }
}
