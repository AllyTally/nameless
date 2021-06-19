package moe.ally.nameless.mixin;

import moe.ally.nameless.LivingEntityAccess;
import moe.ally.nameless.Nameless;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
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
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    // General use
    public boolean isPlayer() {
        return (Object) this instanceof PlayerEntity;
    }

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // For velocity updating (bouncing with slime boots)
    private boolean updateVelocityOnce = false;
    private Vec3d nextUpdateVelocity;

    public void setNextVelocity(Vec3d next) {
        updateVelocityOnce = true;
        nextUpdateVelocity = next;
    }

    // For spikes
    private boolean alwaysDropXp = false;

    public void setAlwaysDropXp(boolean value) {
        alwaysDropXp = value;
    }

    @Inject(at = @At("HEAD"), method = "shouldAlwaysDropXp", cancellable = true)
    void nameless_shouldAlwaysDropXp(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(alwaysDropXp);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void nameless_tick(CallbackInfo info) {
        if (updateVelocityOnce) {
            boolean isClient = this.world.isClient();
            boolean isPlayer = isPlayer();
            if (isPlayer || (!isPlayer && !isClient)) {
                setVelocity(nextUpdateVelocity);
                updateVelocityOnce = false;
            }
        }
    }

    // Slime boots bounce
    public boolean bounce(float fallDistance) {
        if (this.getEquippedStack(EquipmentSlot.FEET).getItem() != Nameless.SLIME_BOOTS) return false;
        if (this.isSneaking() || fallDistance <= 2f || (isPlayer() && !this.world.isClient()) || (!isPlayer() && this.world.isClient())) return false;

        // Player movement
        Vec3d currentVelocity = this.getVelocity();
        this.setVelocity(currentVelocity.x / 0.95f, currentVelocity.y * -0.9, currentVelocity.z / 0.95f);
        this.setOnGround(false);

        setNextVelocity(getVelocity());

        // Sound effect
        world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SLIME_SQUISH, SoundCategory.PLAYERS, 1f, 1f, true);

        return true;
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void nameless_handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> info) {
        if (bounce(fallDistance)) {
            info.setReturnValue(true);
            info.cancel();
        }
    }
}
