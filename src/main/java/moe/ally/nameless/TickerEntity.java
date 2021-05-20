package moe.ally.nameless;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TickerEntity extends Entity {

    private static final TrackedData<Integer> SPEED = DataTracker.registerData(TickerEntity.class, TrackedDataHandlerRegistry.INTEGER);

    public TickerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public static TickerEntity summon(World world, BlockPos pos, Direction direction) {
        return new TickerEntity(Nameless.TICKER, world);
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos currentBlockPos = new BlockPos(getX(), getY(), getZ());
        BlockEntity blockEntity = getEntityWorld().getBlockEntity(currentBlockPos);

        if(!world.isClient()) {
            if (!(blockEntity instanceof Tickable)) {
                // This isn't a tickable block!!
                destroyTicker();
                return;
            }

            for(int i = 0; i < Math.pow(getSpeed(),2); i++) {
                ((Tickable) blockEntity).tick();
            }

            if (age >= 600) {
                destroyTicker();
                return;
            }

        }
    }

    public void destroyTicker() {
        BlockPos currentBlockPos = new BlockPos(getX(), getY(), getZ());
        getEntityWorld().playSound(null, currentBlockPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1);
        kill();
    }

    public void setSpeed(int speed){
        if (!world.isClient) {
            getDataTracker().set(SPEED, speed);
        }
    }

    public int getSpeed(){
        return getDataTracker().get(SPEED);
    }

    @Override
    protected void initDataTracker() {
        getDataTracker().startTracking(SPEED, 1);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("Speed")) {
            setSpeed(tag.getInt("Speed"));
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.putInt("Speed", getSpeed());
    }

    /*@Override
    public Packet<?> createSpawnPacket() {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(getEntityId());
        buf.writeUuid(getUuid());
        buf.writeIdentifier(Registry.ENTITY_TYPE.getId(getType()));
        buf.writeDouble(getX());
        buf.writeDouble(getY());
        buf.writeDouble(getZ());
        buf.writeByte(MathHelper.floor(pitch * 256.0F / 360.0F));
        buf.writeByte(MathHelper.floor(yaw * 256.0F / 360.0F));
        buf.writeShort((int) (MathHelper.clamp(getVelocity().getX(), -3.9D, 3.9D) * 8000.0D));
        buf.writeShort((int) (MathHelper.clamp(getVelocity().getY(), -3.9D, 3.9D) * 8000.0D));
        buf.writeShort((int) (MathHelper.clamp(getVelocity().getZ(), -3.9D, 3.9D) * 8000.0D));

        return ServerSidePacketRegistry.INSTANCE.toPacket(Nameless.SPAWN_PACKET, buf);
    }*/
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, getEntityId());
    }
}
