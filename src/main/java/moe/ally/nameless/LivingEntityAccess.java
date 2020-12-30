package moe.ally.nameless;

import net.minecraft.util.math.Vec3d;

public interface LivingEntityAccess {
    void setAlwaysDropXp(boolean value);

    void setNextVelocity(Vec3d next);

    boolean bounce(float fallDistance);
}
