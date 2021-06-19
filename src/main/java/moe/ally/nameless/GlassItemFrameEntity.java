package moe.ally.nameless;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class GlassItemFrameEntity extends ItemFrameEntity {
    private float itemDropChance = 1.0F;
    private boolean fixed;

    public GlassItemFrameEntity(EntityType<? extends ItemFrameEntity> entityType, World world) {
        super(entityType, world);
    }

    public static GlassItemFrameEntity summon(World world, BlockPos pos, Direction direction) {
        GlassItemFrameEntity newOne = new GlassItemFrameEntity(Nameless.GLASS_ITEM_FRAME_ENTITY, world);
        newOne.attachmentPos = pos;
        newOne.setFacing(direction);
        return newOne;
    }

    public void setFacing(Direction facing) {
        Validate.notNull(facing);
        this.facing = facing;
        if (facing.getAxis().isHorizontal()) {
            this.setPitch(0.0F);
            this.setYaw((float)(this.facing.getHorizontal() * 90));
        } else {
            this.setPitch((float)(-90 * facing.getDirection().offset()));
            this.setYaw(0.0F);
        }

        this.prevPitch = this.getPitch();
        this.prevYaw = this.getYaw();
        this.updateAttachmentPosition();
    }

    protected void updateAttachmentPosition() {
        if (this.facing != null) {
            double d = 0.46875D;
            double e = (double)this.attachmentPos.getX() + 0.5D - (double)this.facing.getOffsetX() * 0.46875D;
            double f = (double)this.attachmentPos.getY() + 0.5D - (double)this.facing.getOffsetY() * 0.46875D;
            double g = (double)this.attachmentPos.getZ() + 0.5D - (double)this.facing.getOffsetZ() * 0.46875D;
            this.setPos(e, f, g);
            double h = (double)this.getWidthPixels();
            double i = (double)this.getHeightPixels();
            double j = (double)this.getWidthPixels();
            Direction.Axis axis = this.facing.getAxis();
            switch(axis) {
                case X:
                    h = 1.0D;
                    break;
                case Y:
                    i = 1.0D;
                    break;
                case Z:
                    j = 1.0D;
            }

            h /= 32.0D;
            i /= 32.0D;
            j /= 32.0D;
            this.setBoundingBox(new Box(e - h, f - i, g - j, e + h, f + i, g + j));
        }
    }

    public void onBreak(@Nullable Entity entity) {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
        this.dropHeldStack(entity, true);
    }

    private void dropHeldStack(@Nullable Entity entity, boolean alwaysDrop) {
        if (!this.fixed) {
            ItemStack itemStack = this.getHeldItemStack();
            this.setHeldItemStack(ItemStack.EMPTY);
            if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                if (entity == null) {
                    this.removeFromFrame(itemStack);
                }

            } else {
                if (entity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity)entity;
                    if (playerEntity.getAbilities().creativeMode) {
                        this.removeFromFrame(itemStack);
                        return;
                    }
                }

                if (alwaysDrop) {
                    this.dropItem(Nameless.GLASS_ITEM_FRAME);
                }

                if (!itemStack.isEmpty()) {
                    itemStack = itemStack.copy();
                    this.removeFromFrame(itemStack);
                    if (this.random.nextFloat() < this.itemDropChance) {
                        this.dropStack(itemStack);
                    }
                }

            }
        }
    }

    private void removeFromFrame(ItemStack map) {
        if (map.getItem() == Items.FILLED_MAP) {
            MapState mapState = FilledMapItem.getOrCreateMapState(map, this.world);
            mapState.removeFrame(this.attachmentPos, this.getId());
            mapState.setDirty(true);
        }

        map.setHolder((Entity)null);
    }

    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), this.facing.getId(), this.getDecorationBlockPos());
    }
}
