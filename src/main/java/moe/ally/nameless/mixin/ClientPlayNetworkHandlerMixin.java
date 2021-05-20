package moe.ally.nameless.mixin;

import moe.ally.nameless.GlassItemFrameEntity;
import moe.ally.nameless.Nameless;
import moe.ally.nameless.TickerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow
    private ClientWorld world;

    @Inject(
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    ) // thank you parzivail
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, double x, double y, double z, EntityType<?> type) {
        Entity entity = null;
        if (type == Nameless.GLASS_ITEM_FRAME_ENTITY) {
            entity = GlassItemFrameEntity.summon(world, new BlockPos(new Vec3d(x, y, z)), Direction.byId(packet.getEntityData()));

            if (entity != null) {
                int entityId = packet.getId();
                ((GlassItemFrameEntity) entity).setFacing(((GlassItemFrameEntity) entity).getHorizontalFacing());
                entity.setVelocity(Vec3d.ZERO);
                entity.updatePosition(x, y, z);
                entity.updateTrackedPosition(x, y, z);
                entity.pitch = (float) (packet.getPitch() * 360) / 256f;
                entity.yaw = (float) (packet.getYaw() * 360) / 256f;
                entity.setEntityId(entityId);
                entity.setUuid(packet.getUuid());
                this.world.addEntity(entityId, entity);
                ci.cancel();
            }
        } else if (type == Nameless.TICKER) {
            entity = TickerEntity.summon(world, new BlockPos(new Vec3d(x, y, z)), Direction.byId(packet.getEntityData()));

            if (entity != null) {
                int entityId = packet.getId();
                entity.setVelocity(Vec3d.ZERO);
                entity.updatePosition(x, y, z);
                entity.updateTrackedPosition(x, y, z);
                entity.setEntityId(entityId);
                entity.setUuid(packet.getUuid());
                this.world.addEntity(entityId, entity);
                ci.cancel();
            }
        }
    }
}