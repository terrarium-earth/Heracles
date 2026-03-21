package earth.terrarium.olympus.client.elements;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.NotNull;

public class GradientGuiElement extends BaseGuiElement {

    private final int col1;
    private final int col2;
    private final int col3;
    private final int col4;

    public GradientGuiElement(int col1, int col2, int col3, int col4) {
        super(RenderPipelines.GUI);

        this.col1 = col1;
        this.col2 = col2;
        this.col3 = col3;
        this.col4 = col4;
    }

    @Override
    public void buildVertices(@NotNull VertexConsumer consumer) {
        var bounds = this.bounds();
        consumer.addVertexWith2DPose(this.pose(), bounds.left(), bounds.top()).setColor(this.col1);
        consumer.addVertexWith2DPose(this.pose(), bounds.left(), bounds.bottom()).setColor(this.col3);
        consumer.addVertexWith2DPose(this.pose(), bounds.right(), bounds.bottom()).setColor(this.col4);
        consumer.addVertexWith2DPose(this.pose(), bounds.right(), bounds.top()).setColor(this.col2);
    }
}
