package erc._mc._1_7_10.renderer;

import erc._mc._1_7_10._core.ERC_Core;
import mochisystems.math.Vec3d;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import net.minecraft.client.renderer.texture.TextureMap;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.manager.AutoRailConnectionManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class RenderTileEntityRailBase extends TileEntitySpecialRenderer{
	
//	private static final ResourceLocation TEXTURE  ;
	private static final ResourceLocation TEXTUREguiarraw = new ResourceLocation(ERC_Core.MODID,"textures/gui/ringarraw.png");
	//new ResourceLocation(", "textures/blocks/pink.png");

	@Override
	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f)
	{
		renderTileEntityAt((TileEntityRail)t,x,y,z,f);
	}

	public void renderTileEntityAt(TileEntityRail tile, double x, double y, double z, float f)
	{
		Tessellator tessellator = Tessellator.instance;
		this.bindTexture(TextureMap.locationBlocksTexture);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glPushMatrix();
		GL11.glTranslated(x+0.5, y+0.5, z+0.5);
		tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);

		tile.Render(tessellator);
		
    	if(tile == AutoRailConnectionManager.clickedTileForGUI){
    		DrawRotaArrow(tessellator, tile);
    	}

      	GL11.glEnable(GL11.GL_CULL_FACE);
      	GL11.glPopMatrix();
	}
	
	@SuppressWarnings("unused")
	private void DrawArrow(Tessellator tess, Vec3d vec)
	{
      	tess.startDrawing(GL11.GL_TRIANGLES);
      	tess.addVertexWithUV(0.2d, 0d, 0.2d, 0.0d, 0.0d);
      	tess.addVertexWithUV(vec.x*3d, vec.y*3d, vec.z*3d, 0.0d, 0.0d);
      	tess.addVertexWithUV(-0.2d, 0d, -0.2d, 0.0d, 0.0d);
      	tess.draw();
	}
	
	// GUI�\�����̉�]���`��p
	public void DrawRotaArrow(Tessellator tess, TileEntityRail tile)
	{
		this.bindTexture(TEXTUREguiarraw);
      	Vec3d d = tile.getRail().GetBasePoint().Dir();
		Vec3d u = tile.getRail().GetBasePoint().Up();
      	Vec3d p = tile.getRail().GetBasePoint().Side();
      	
      	d = d.normalize();
      	u = u.normalize();
      	p = p.normalize();
      	
      	float s = 2.0f; // s
      	
      	// yaw axis
      	GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
	    tess.addVertexWithUV(( d.x+p.x)*s, ( d.y+p.y)*s, ( d.z+p.z)*s, 0.0d, 0.0d);
		tess.addVertexWithUV(( d.x-p.x)*s, ( d.y-p.y)*s, ( d.z-p.z)*s, 1.0d, 0.0d);
		tess.addVertexWithUV((-d.x+p.x)*s, (-d.y+p.y)*s, (-d.z+p.z)*s, 0.0d, 1.0d);
		tess.addVertexWithUV((-d.x-p.x)*s, (-d.y-p.y)*s, (-d.z-p.z)*s, 1.0d, 1.0d);
		tess.draw();
		// pitch axis
		s = 1.5f;
		GL11.glColor4f(0.0F, 1.0F, 0.0F, 1.0F);  
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);
	    tess.addVertexWithUV(( u.x+d.x)*s, ( u.y+d.y)*s, ( u.z+d.z)*s, 0.0d, 0.0d);
		tess.addVertexWithUV(( u.x-d.x)*s, ( u.y-d.y)*s, ( u.z-d.z)*s, 1.0d, 0.0d);
		tess.addVertexWithUV((-u.x+d.x)*s, (-u.y+d.y)*s, (-u.z+d.z)*s, 0.0d, 1.0d);
		tess.addVertexWithUV((-u.x-d.x)*s, (-u.y-d.y)*s, (-u.z-d.z)*s, 1.0d, 1.0d);
		tess.draw();
		// roll axis
		s = 1.0f;
		GL11.glColor4f(0.0F, 0.0F, 1.0F, 1.0F);                       
		tess.startDrawing(GL11.GL_TRIANGLE_STRIP);              
	    tess.addVertexWithUV(( u.x+p.x)*s, ( u.y+p.y)*s, ( u.z+p.z)*s, 0.0d, 0.0d);
		tess.addVertexWithUV(( u.x-p.x)*s, ( u.y-p.y)*s, ( u.z-p.z)*s, 1.0d, 0.0d);
		tess.addVertexWithUV((-u.x+p.x)*s, (-u.y+p.y)*s, (-u.z+p.z)*s, 0.0d, 1.0d);
		tess.addVertexWithUV((-u.x-p.x)*s, (-u.y-p.y)*s, (-u.z-p.z)*s, 1.0d, 1.0d);
		tess.draw();                            
	}

}
