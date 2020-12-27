package moe.ally.nameless;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimePos {
    public BlockPos pos;
    public World world;
    public int speed;

    public TimePos(BlockPos pos, World world, int speed) {
        this.pos = pos;
        this.world = world;
        this.speed = speed;
    }
}
