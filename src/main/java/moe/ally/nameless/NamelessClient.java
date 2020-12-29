package moe.ally.nameless;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;

@Environment(EnvType.CLIENT)
public class NamelessClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(Nameless.TICKER, (dispatcher, context) -> {
            return new TickerEntityRenderer(dispatcher);
        });
        ClientSidePacketRegistry.INSTANCE.register(Nameless.SPAWN_PACKET, ClientNetworking::spawnNonLivingEntity);
    }
}