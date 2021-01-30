package moe.ally.nameless.mixin;

import moe.ally.nameless.LivingEntityAccess;
import moe.ally.nameless.Nameless;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {

    @Shadow
    public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void nameless_handleFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Boolean> info) {
        if (this.getEquippedStack(EquipmentSlot.FEET).getItem() == Nameless.SLIME_BOOTS)
            info.setReturnValue(((LivingEntityAccess) this).bounce(fallDistance));
    }
}
