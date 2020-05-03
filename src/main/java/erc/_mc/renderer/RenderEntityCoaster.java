package erc._mc.renderer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.coaster.Coaster;
import erc._mc.entity.EntityCoaster;
import mochisystems.math.Mat4;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderEntityCoaster extends Render {

    Quaternion q = new Quaternion();
    Mat4 m = new Mat4();

    public void doRender(EntityCoaster entity, double x, double y, double z, float f, float partialtick)
	{
		if(entity.model == null) return;

		Coaster coaster = entity.GetCoaster();

		GL11.glPushMatrix();
        Quaternion.Lerp(q, coaster.prevAttitude, coaster.attitude, partialtick);
        GL11.glTranslated(x, y, z);
		GL11.glMultMatrix(q.makeMatrixBuffer());

		entity.model.RenderModel(0, partialtick);
		entity.model.RenderModel(1, partialtick);

		GL11.glPopMatrix();

		renderDebugBox(entity, x, y, z);
	}

	private void renderDebugBox(EntityCoaster entity, double x, double y, double z)
	{
//		Entity[] ea = entity.getParts();
//		for(int i=0; i<ea.length; i++)
//		{
//			renderOffsetAABB(ea[i].boundingBox.getOffsetBoundingBox(-entity.posX, -entity.posY, -entity.posZ), x, y, z);
//		}
//		renderOffsetAABB(entity.boundingBox.getOffsetBoundingBox(-entity.posX, -entity.posY, -entity.posZ), x, y, z);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f, float p_76986_9_) {
		doRender((EntityCoaster)entity, x, y, z, f, p_76986_9_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		// TODO Auto-generated method stub
		return null;
	}


}
