package moe.ally.nameless;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Nameless implements ModInitializer {
	public static final TimeInABottleItem TIME_IN_A_BOTTLE = new TimeInABottleItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	public static final MobTakerItem MOB_TAKER = new MobTakerItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	public static final SlimeslingItem SLIMESLING = new SlimeslingItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1));
	public static final SpikeBlock SPIKE_BLOCK = new SpikeBlock(Block.Settings.of(Material.STONE).strength(4.0f));
	public static final ArmorMaterial slimeBootsMaterial = new SlimeBootsMaterial();
	public static final Item SLIME_BOOTS = new SlimeBootsItem(slimeBootsMaterial, EquipmentSlot.FEET, new Item.Settings().group(ItemGroup.COMBAT));

	public static final EntityType<TickerEntity> TICKER = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier("nameless", "ticker"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, TickerEntity::new).dimensions(EntityDimensions.fixed(0.25F, 0.25F)).build()
	);

	public static final Identifier SPAWN_PACKET = new Identifier("nameless", "spawn/nonliving/generic");

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier("nameless", "time_in_a_bottle"), TIME_IN_A_BOTTLE);
		Registry.register(Registry.ITEM, new Identifier("nameless", "mob_taker"), MOB_TAKER);
		Registry.register(Registry.ITEM, new Identifier("nameless", "slimesling"), SLIMESLING);
		Registry.register(Registry.ITEM, new Identifier("nameless", "slime_boots"), SLIME_BOOTS);
		Registry.register(Registry.BLOCK, new Identifier("nameless", "spike_block"), SPIKE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("nameless", "spike_block"), new BlockItem(SPIKE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
	}
}

