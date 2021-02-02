package moe.ally.nameless;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GlassItemFrameEntityRenderer extends ItemFrameEntityRenderer {
    private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("glass_item_frame");
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ItemRenderer itemRenderer;

    public GlassItemFrameEntityRenderer(EntityRenderDispatcher dispatcher, ItemRenderer itemRenderer) {
        super(dispatcher, itemRenderer);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ItemFrameEntity itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        //super.render(itemFrameEntity, f, g, matrixStack, vertexConsumerProvider, i);
        matrixStack.push();
        Direction direction = itemFrameEntity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        matrixStack.translate((double)direction.getOffsetX() * 0.46875D, (double)direction.getOffsetY() * 0.46875D, (double)direction.getOffsetZ() * 0.46875D);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(itemFrameEntity.pitch));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - itemFrameEntity.yaw));

        ItemStack itemStack = itemFrameEntity.getHeldItemStack();

        if (itemStack.isEmpty() || itemStack.getItem() == Items.FILLED_MAP) {
            BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
            BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
            ModelIdentifier modelIdentifier = NORMAL_FRAME;
            matrixStack.push();
            matrixStack.translate(-0.5D, -0.5D, -0.5D);
            blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()), null, bakedModelManager.getModel(modelIdentifier), 1.0F, 1.0F, 1.0F, i, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        }

        if (!itemStack.isEmpty()) {
            boolean bl2 = itemStack.getItem() == Items.FILLED_MAP;
            matrixStack.translate(0.0D, 0.0D, 0.5D);

            int j = bl2 ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)j * 360.0F / 8.0F));
            if (bl2) {
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
                matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
                matrixStack.translate(-64.0D, -64.0D, 0.0D);
                MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, itemFrameEntity.world);
                matrixStack.translate(0.0D, 0.0D, -1.0D);
                if (mapState != null) {
                    this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, mapState, true, i);
                }
            } else {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.FIXED, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider);
            }
        }

        matrixStack.pop();
    }
}
