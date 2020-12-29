package moe.ally.nameless;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Nameless implements ModInitializer {
	public static final TimeInABottleItem TIME_IN_A_BOTTLE = new TimeInABottleItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	public static final SlimeslingItem SLIMESLING = new SlimeslingItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1));
	public static final EntityType<TickerEntity> TICKER = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("nameless", "ticker"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, TickerEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build()
	);

	public static final Identifier SPAWN_PACKET = new Identifier("nameless", "spawn/nonliving/generic");

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("nameless", "time_in_a_bottle"), TIME_IN_A_BOTTLE);
		Registry.register(Registry.ITEM, new Identifier("nameless", "slimesling"), SLIMESLING);
	}
}

