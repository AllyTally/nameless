package moe.ally.nameless;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class NamelessClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(Nameless.TICKER, TickerEntityRenderer::new);

        FabricModelPredicateProviderRegistry.register(new Identifier("nameless", "has_entity"), (stack, world, entity, i) ->
            {
                if (stack.getOrCreateTag().contains("entity")) {
                    return 1;
                }

                return 0;
            });
		EntityRendererRegistry.INSTANCE.register(Nameless.GLASS_ITEM_FRAME_ENTITY, GlassItemFrameEntityRenderer::new);
    }
}