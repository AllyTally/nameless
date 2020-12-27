package moe.ally.nameless;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class Nameless implements ModInitializer {
	public static ArrayList<TimePos> TIABBlocks = new ArrayList<TimePos>();
	public static final TimeInABottleItem TIME_IN_A_BOTTLE = new TimeInABottleItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("nameless", "time_in_a_bottle"), TIME_IN_A_BOTTLE);
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (int i = TIABBlocks.size() - 1; i >= 0; i--) {
				TimePos timePos = TIABBlocks.get(i);
				BlockEntity tileEntity = timePos.world.getBlockEntity(timePos.pos);
				if (tileEntity != null) {
					Tickable tickable = (Tickable) tileEntity;
					for (int j = 0; j < timePos.speed; j++) {
						tickable.tick();
					}
				} else {
					TIABBlocks.remove(i);
				}
			}
		});
	}
}