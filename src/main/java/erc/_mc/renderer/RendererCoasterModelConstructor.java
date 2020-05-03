package erc._mc.renderer;

import erc._mc.tileentity.TileEntityCoasterModelConstructor;
import mochisystems._core._Core;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RendererCoasterModelConstructor extends TileEntitySpecialRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(_Core.MODID,"textures/limitline.png");

    @Override
    public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
    {
        TileEntityCoasterModelConstructor T = (TileEntityCoasterModelConstructor)t;
        Tessellator tessellator = Tessellator.instance;
        this.bindTexture(TEXTURE);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

        T.render(tessellator); // render limitframe

        this.bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();
        GL11.glTranslated(-3.5, 1.5, 0);
        T.renderer.RenderRail();
        GL11.glPopMatrix();

        if(T.blockModel != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(-3.5, 1.5, 3);
            GL11.glTranslated(T.modelOffset.x, T.modelOffset.y, T.modelOffset.z);
            GL11.glRotated(T.modelRotate.x, 1, 0, 0);
            GL11.glRotated(T.modelRotate.y, 0, 1, 0);
            GL11.glRotated(T.modelRotate.z, 0, 0, 1);
            GL11.glScalef(T.modelScale, T.modelScale, T.modelScale);

            // モデルに保存されてるoffsetとかの値はここでは無視するので以下
            GL11.glScalef(1f/T.registeredModelScale, 1f/T.registeredModelScale, 1f/T.registeredModelScale);
            GL11.glRotated(-T.registeredModelRotate.z, 0, 0, 1);
            GL11.glRotated(-T.registeredModelRotate.y, 0, 1, 0);
            GL11.glRotated(-T.registeredModelRotate.x, 1, 0, 0);
            GL11.glTranslated(-T.registeredModelOffset.x, -T.registeredModelOffset.y, -T.registeredModelOffset.z);

            T.blockModel.RenderModel(0, f);
            GL11.glPopMatrix();
        }


        GL11.glEnable(GL11.GL_CULL_FACE); // �J�����OON
        GL11.glPopMatrix();
    }
}
