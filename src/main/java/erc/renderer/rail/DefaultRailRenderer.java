package erc.renderer.rail;

import akka.routing.ConsistentHashingRoutingLogic;
import mochisystems.math.Vec3d;
import erc.rail.IRail;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class DefaultRailRenderer extends RailRenderer {

	private int hash;

	public class Surface{
		Vec3d v1;
		Vec3d v2;
		Vec3d normal;
		Surface(Vec3d v1, Vec3d v2, Vec3d n){this.v1 = v1; this.v2 = v2; this.normal = n;}
	}

	public int GetHash()
	{
		return hash;
	}

	private float mu, xu, mv, xv;

	public DefaultRailRenderer(IRail rail)
	{
		this(rail, Blocks.iron_block);
	}
	public DefaultRailRenderer(IRail rail, Block textureSource)
    {
        super(rail);
        IIcon icon = textureSource.getIcon(1, 0);
        mu =icon.getMinU();
        xu =icon.getMaxU();
        mv =icon.getMinV();
        xv =icon.getMaxV();
    }

    @Override
    public void SetDirty()
    {
        int oldHash = hash;
        int newHash = 0;
		newHash = newHash * 31 + 'x';
		newHash = newHash * 31 + rail.GetController().x();
		newHash = newHash * 31 + 'y';
		newHash = newHash * 31 + rail.GetController().y();
		newHash = newHash * 31 + 'z';
		newHash = newHash * 31 + rail.GetController().z();
        if(newHash != oldHash) DeleteBuffer();
        hash = newHash;
        super.SetDirty();
    }

	@Override
	protected void DrawRailModel()
	{
        double t1 = 0.4 + 0.1;
        double t2 = 0.4 - 0.1;

        Surface[] surface = new Surface[]{
			new Surface(new Vec3d(t1, 0, 0), new Vec3d(t2, 0, 0), new Vec3d(0, 1,0)),
			new Surface(new Vec3d(-t2, 0, 0), new Vec3d(-t1, 0, 0), new Vec3d(0, 1, 0)),
		};

		int pointNum = rail.GetPointNum();

        Tessellator.instance.startDrawing(GL11.GL_QUADS);

		for(int s = 0; s < surface.length; s++) {

			Vec3d pos1 = new Vec3d();
			Vec3d pos2 = new Vec3d();
			Vec3d normal = new Vec3d();
			// base 0
			t = 0;
			pos1.CopyFrom(surface[s].v1);
			pos2.CopyFrom(surface[s].v2);
			normal.CopyFrom(surface[s].normal);

			double lengthPer1RailPart = 1 / pointNum;

			for (int i = 1; i < pointNum; ++i) {
				// base n
				RegisterVertex(pos1, normal, mu, mv);
				RegisterVertex(pos2, normal, xu, mv);

				// next n+1
				t = rail.GetPointList()[i];
				pos1.CopyFrom(surface[s].v1);
				pos2.CopyFrom(surface[s].v2);
				pos1.y += i * lengthPer1RailPart;
				pos2.y += i * lengthPer1RailPart;
				RegisterVertex(pos2, normal, xu, xv);
				RegisterVertex(pos1, normal, mu, xv);
			}
		}
		Tessellator.instance.draw();
    }

}