package moe.ally.nameless.mixin;

import moe.ally.nameless.Nameless;
import moe.ally.nameless.SlimeslingItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // For slime boots
    private boolean updateVelocityOnce = false;
    private Vec3d nextUpdateVelocity;

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo info) {
        // Entity launched by player using slimesling
        if (!((Object) this instanceof PlayerEntity)) {
            LivingEntity self = (LivingEntity) ((Object) this);
            if (SlimeslingItem.slimeslingEntityVelocities.containsKey(self)) {
                setVelocity((Vec3d) SlimeslingItem.slimeslingEntityVelocities.get(self));
                SlimeslingItem.slimeslingEntityVelocities.remove(self);
            }
        }

        // Entity is wearing slime boots
        if (updateVelocityOnce) {
            updateVelocityOnce = false;
            setVelocity(nextUpdateVelocity);
        }
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void nameless_handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> info) {
        if (this.getEquippedStack(EquipmentSlot.FEET).getItem() != Nameless.SLIME_BOOTS) return;
        if (this.isSneaking() || fallDistance <= 2f || ((Object) this instanceof PlayerEntity && !this.world.isClient()) || (!((Object) this instanceof PlayerEntity) && this.world.isClient())) return;

        // Player movement
        Vec3d currentVelocity = this.getVelocity();
        this.setVelocity(currentVelocity.x / 0.95f, currentVelocity.y * -0.9, currentVelocity.z / 0.95f);
        this.setOnGround(false);

        updateVelocityOnce = true;
        nextUpdateVelocity = getVelocity();

        // Sound effect
        world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1f, 1f, true);

        info.setReturnValue(true);
    }
}
