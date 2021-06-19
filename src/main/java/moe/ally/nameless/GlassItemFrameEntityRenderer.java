package moe.ally.nameless;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class GlassItemFrameEntityRenderer extends ItemFrameEntityRenderer {
    private static final Identifier ID = new Identifier("nameless", "textures/block/glass_item_frame.png");
    //private final GlassItemFrameModel model = new GlassItemFrameModel();
    private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("nameless:glass_item_frame");

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final ItemRenderer itemRenderer;

    public GlassItemFrameEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ItemFrameEntity itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        Direction direction = itemFrameEntity.getHorizontalFacing();
        Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
        matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
        matrixStack.translate((double)direction.getOffsetX() * 0.46875D, (double)direction.getOffsetY() * 0.46875D, (double)direction.getOffsetZ() * 0.46875D);
        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(itemFrameEntity.getPitch()));
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - itemFrameEntity.getYaw()));

        ItemStack itemStack = itemFrameEntity.getHeldItemStack();

        if (itemStack.isEmpty()) {
            BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
            BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
            ModelIdentifier modelIdentifier = NORMAL_FRAME;
            matrixStack.push();
            matrixStack.translate(-0.5D, -0.5D, -0.5D);
            blockRenderManager.getModelRenderer().render(matrixStack.peek(), vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()), (BlockState)null, bakedModelManager.getModel(modelIdentifier), 1.0F, 1.0F, 1.0F, i, OverlayTexture.DEFAULT_UV);
            matrixStack.pop();
        }

        if (!itemStack.isEmpty()) {
            boolean bl2 = itemStack.isOf(Items.FILLED_MAP);
            matrixStack.translate(0.0D, 0.0D, 0.5D);

            int j = bl2 ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)j * 360.0F / 8.0F));
            if (bl2) {
                matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F));
                matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
                matrixStack.translate(-64.0D, -64.0D, 0.0D);
                Integer integer = FilledMapItem.getMapId(itemStack);
                MapState mapState = FilledMapItem.getMapState(integer, itemFrameEntity.world);
                matrixStack.translate(0.0D, 0.0D, -1.0D);
                if (mapState != null) {
                    this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, integer, mapState, true, i);
                }
            } else {
                matrixStack.scale(0.5F, 0.5F, 0.5F);
                this.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.FIXED, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, itemFrameEntity.getId());
            }
        }

        matrixStack.pop();
    }

    public Identifier getTexture(ItemFrameEntity entity) {
        return ID;
    }
}
