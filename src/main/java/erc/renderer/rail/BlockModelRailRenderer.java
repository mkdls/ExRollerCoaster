package erc.renderer.rail;

import erc._mc.tileentity.TileEntityRailModelConstructor;
import erc.rail.IRail;
import mochisystems.blockcopier.BlocksRenderer;
import mochisystems.blockcopier.MTYBlockAccess;
import mochisystems.math.Vec3d;
import mochisystems.util.HashMaker;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class BlockModelRailRenderer extends RailRenderer implements IRailRenderer{

    // temp
    World world;
    int x, y, z;
    NBTTagCompound nbt;

    private BlocksRendererForRail[] renderers;
//    private BlocksReplicator[] replicators;
    private MTYBlockAccess blockAccess;
    private final TessellatorAdapter adapter = new TessellatorAdapter();
    private float widthRatio;
    private float heightRatio;
    private float lengthRatio = 1f;
    private int copyNum;
    private int side;

    public BlockModelRailRenderer(IRail rail, World world, NBTTagCompound nbt, int x, int y, int z)
    {
        super(rail);
        this.nbt = nbt;
        this.world = world;
        this.x = x; this.y = y; this.z = z;
        blockAccess = new MTYBlockAccess(world);
        widthRatio =  nbt.getFloat("widthratio");
        heightRatio =  nbt.getFloat("heightratio");
        copyNum = nbt.getInteger("copynum");
        side = nbt.getByte("constructorside");
        blockAccess.constructFromTag(nbt, x, y, z, false, this::initReplicators);
        UpdateRailData();
    }

    private void initReplicators()
    {
        lengthRatio = 1f / copyNum;
//        replicators = new BlocksReplicator[copyNum];
        renderers = new BlocksRendererForRail[copyNum];
        for(int i = 0; i < copyNum; ++i)
        {
            renderers[i] = new BlocksRendererForRail(blockAccess, rail, i);
            renderers[i].CompileRenderer();

//            replicators[i] = new BlocksReplicator(world, blockAccess, renderers[i]);
//            replicators[i].setWorld(world);
//            replicators[i].setCorePosition(x, y, z);
        }
    }

    @Override
    public int GetHash()
    {
        return 0;
    }

    @Override
    public void SetDirty()
    {
        UpdateRailData();
        initReplicators();
        for(BlocksRendererForRail renderer : renderers) renderer.SetDirty();
        super.SetDirty();
    }

    @Override
    public void DeleteBuffer()
    {
        for(BlocksRendererForRail renderer : renderers) renderer.delete();
//        for(BlocksReplicator replicator : replicators)replicator.invalidate();
    }

    int currentRenderIdx;
    @Override
    public void RenderRail()
    {
        currentRenderIdx = 0;
        adapter.original = Tessellator.instance;
        SetOriginalTessallator(adapter);
        for(BlocksRendererForRail renderer : renderers)
        {
            GL11.glPushMatrix();
            renderer.render();
            currentRenderIdx++;
            GL11.glPopMatrix();
        }
        SetOriginalTessallator(adapter.original);
    }

    private void SetOriginalTessallator(Tessellator org)
    {
        // asmで実装する
        // Tessallator.instance = org;
    }

    @Override
    protected void DrawRailModel()
    {
        // nothing
    }


    public static class BlocksRendererForRail extends BlocksRenderer {

        IRail rail;
        int idx;

        public BlocksRendererForRail(MTYBlockAccess ba, IRail rail, int idx) {
            super(ba);
            this.rail = rail;
            this.idx = idx;
        }

        @Override
        protected int CalcBlocksHash(int pass, int start, int end, int widthX, int widthY, int widthZ)
        {
            HashMaker hasher = new HashMaker(super.CalcBlocksHash(pass, start, end, widthX, widthY, widthZ));
            hasher.Append('x');
            hasher.Append( rail.GetController().x());
            hasher.Append('y');
            hasher.Append(rail.GetController().y());
            hasher.Append('z');
            hasher.Append(rail.GetController().z());
            hasher.Append(idx);
            return hasher.GetHash();
        }
    }


    private class TessellatorAdapter extends Tessellator{
        public Tessellator original;
        Vec3d pos = new Vec3d();
        Vec3d normal = new Vec3d();
        @Override
        public void setNormal(float x, float y, float z)
        {
            normal.SetFrom(x, y, z);
        }

        @Override
        public void addVertexWithUV(double x, double y, double z, double u, double v)
        {
            int length = 1;
            pos.SetFrom(x, y, z);
            pos.x -= blockAccess.originalCorePosX + 0.5f;
            pos.y -= blockAccess.originalCorePosY + 0.5f;
            pos.z -= blockAccess.originalCorePosZ + 0.5f;
            double temp = 0;
            switch(side)
            {
                case 0 : temp = pos.z; pos.z = pos.y; pos.y = -temp; length = (blockAccess.getSize(1)-2); break;
                case 1 : temp = pos.z; pos.z = -pos.y; pos.y = temp; length = (blockAccess.getSize(1)-2); break;
                case 2 : length = (blockAccess.getSize(2)-2); break;
                case 3 : pos.x = -pos.x; pos.z = -pos.z; length = (blockAccess.getSize(2)-2); break;
                case 4 : temp = pos.z; pos.z = pos.x; pos.x = -temp; length = (blockAccess.getSize(0)-2); break;
                case 5 : temp = pos.z; pos.z = -pos.x; pos.x = temp; length = (blockAccess.getSize(0)-2); break;
            }
            pos.z += 0.5f;
            pos.x *= widthRatio;
            pos.y *= heightRatio;
            pos.z += currentRenderIdx * length;
            pos.z *= lengthRatio;

            double t = (pos.z >= 0) ? rail.FixParameter(pos.z / length) : (pos.z / length);
            TransformVertex(t, pos, normal);
            original.setNormal((float)renderNormal.x, (float)renderNormal.y, (float)renderNormal.z);
            renderPos.x += blockAccess.originalCorePosX + 0.5;
            renderPos.y += blockAccess.originalCorePosY + 0.5;
            renderPos.z += blockAccess.originalCorePosZ + 0.5;
            original.addVertexWithUV(renderPos.x, renderPos.y, renderPos.z, u, v);
        }

        @Override public int draw(){return original.draw();}
        @Override public TesselatorVertexState getVertexState(float x, float y, float z){return original.getVertexState(x, y, z);}
        @Override public void setVertexState(TesselatorVertexState state){original.setVertexState(state);}
        @Override public void startDrawingQuads(){original.startDrawingQuads();}
        @Override public void startDrawing(int i){original.startDrawing(i);}
        @Override public void setTextureUV(double u, double v){original.setTextureUV(u, v);}
        @Override public void setBrightness(int b){original.setBrightness(b);}
        @Override public void setColorOpaque_F(float r, float g, float b){original.setColorOpaque_F(r,b,g);}
        @Override public void setColorRGBA_F(float r, float g, float b, float a){original.setColorRGBA_F(r,g,b,a);}
        @Override public void setColorOpaque(int r, int g, int b){original.setColorOpaque(r,g,b);}
        @Override public void setColorRGBA(int r, int g, int b, int a){original.setColorRGBA(r,g,b,a);}
        @Override public void func_154352_a(byte a, byte b, byte c){original.func_154352_a(a,b,c);}
        @Override public void addVertex(double x, double y, double z){original.addVertex(x, y, z);}
        @Override public void setColorOpaque_I(int c){original.setColorOpaque_I(c);}
        @Override public void setColorRGBA_I(int c, int a){original.setColorRGBA_I(c, a);}
        @Override public void disableColor()
        {
            original.disableColor();
        }
        @Override public void setTranslation(double x, double y, double z){original.setTranslation(x, y, z);}
        @Override public void addTranslation(float x, float y, float z){original.addTranslation(x, y, z);}
    }
}
