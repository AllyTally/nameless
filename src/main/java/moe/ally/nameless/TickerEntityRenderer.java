package moe.ally.nameless;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class TickerEntityRenderer extends EntityRenderer<TickerEntity> {
    public TickerEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }
    @Override
    public Identifier getTexture(TickerEntity entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
    @Override
    public void render(TickerEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        ItemStack tiabStack = new ItemStack(Nameless.TIME_IN_A_BOTTLE);
        for (Direction direction : Direction.values()) {
            if (direction == direction.UP || direction == direction.DOWN) continue;
            matrices.push();

            // center in block
            matrices.translate(direction.getOffsetX()/1.95f, (direction.getOffsetY()/1.95f + 0.5), direction.getOffsetZ()/1.95f);

            // align to block sides
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(direction.asRotation()));

            MinecraftClient.getInstance().getItemRenderer().renderItem(tiabStack, ModelTransformation.Mode.GROUND, 242, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
            matrices.pop();
        }
    }
}
