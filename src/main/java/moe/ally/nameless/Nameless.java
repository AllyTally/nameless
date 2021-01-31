package moe.ally.nameless;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.Material;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public class Nameless implements ModInitializer {
	public static final TimeInABottleItem TIME_IN_A_BOTTLE = new TimeInABottleItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
	public static final SoulJarItem SOUL_JAR = new SoulJarItem(new FabricItemSettings().group(ItemGroup.MISC).maxCount(1));
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
		Registry.register(Registry.ITEM, new Identifier("nameless", "soul_jar"), SOUL_JAR);
		Registry.register(Registry.ITEM, new Identifier("nameless", "slimesling"), SLIMESLING);
		Registry.register(Registry.ITEM, new Identifier("nameless", "slime_boots"), SLIME_BOOTS);
		Registry.register(Registry.BLOCK, new Identifier("nameless", "spike_block"), SPIKE_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("nameless", "spike_block"), new BlockItem(SPIKE_BLOCK, new Item.Settings().group(ItemGroup.MISC)));

		DispenserBlock.registerBehavior(SOUL_JAR, new ItemDispenserBehavior() {
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				Direction direction = (Direction)pointer.getBlockState().get(DispenserBlock.FACING);
				BlockPos pos = pointer.getBlockPos();
				World world = pointer.getWorld();
				if (!stack.getOrCreateTag().contains("entity")) {
					BlockPos blockPos = pointer.getBlockPos().offset((Direction)pointer.getBlockState().get(DispenserBlock.FACING));
					List<Entity> list = pointer.getWorld().getEntitiesByClass(Entity.class, new Box(blockPos), (Entityx) -> {
						return Entityx.isAlive();
					});
					Iterator var5 = list.iterator();

					Entity entity;
					ItemStack newStack;
					do {
						if (!var5.hasNext()) {
							return super.dispenseSilently(pointer, stack);
						}

						entity = (Entity)var5.next();
						newStack = ((SoulJarItem)stack.getItem()).captureEntity(stack,null,(LivingEntity) entity);
					} while(newStack == null);

					//stack.decrement(1);
					return newStack;
				}
				((SoulJarItem) stack.getItem()).spawnEntity(pos,direction,world,stack);
				return stack;
			}
		});


	}
}

