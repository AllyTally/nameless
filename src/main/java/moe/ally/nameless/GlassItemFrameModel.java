package moe.ally.nameless;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class GlassItemFrameModel extends EntityModel<GlassItemFrameEntity> {
    private final ModelPart model;

    public GlassItemFrameModel() {
        textureWidth = 16;
        textureHeight = 16;

        model = new ModelPart(this, 6, 4);

        model.addCuboid(-6, -6, 7, 1, 12, 1);
        model.addCuboid(5, -6, 7, 1, 12, 1);
        model.addCuboid(-5, 5, 7, 10, 1, 1);
        model.addCuboid(-5, -6f, 7, 10, 1, 1);
    }

    public void setRotationAngle(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.pitch = x;
        modelRenderer.yaw = y;
        modelRenderer.roll = z;
    }

    @Override
    public void setAngles(GlassItemFrameEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {}

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        model.render(matrices, vertices, light, overlay);
    }
}
