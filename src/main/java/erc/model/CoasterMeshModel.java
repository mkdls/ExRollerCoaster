package erc.model;

import mochisystems.util.IModel;
import mochisystems._mc._1_7_10.bufferedrenderer.IBufferedRenderer;
import mochisystems.util.HashMaker;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelCustom;

public class CoasterMeshModel implements IModel {

    public class MeshBuffer extends IBufferedRenderer {

        private IModelCustom modelCoaster;
        private ResourceLocation TextureResource;
        private int hash;

        @SuppressWarnings("unused")
        private MeshBuffer(){}

        public MeshBuffer(IModelCustom Obj, ResourceLocation Tex, String id)
        {
            modelCoaster = Obj;
            TextureResource = Tex;
            hash = new HashMaker().Append(id).GetHash();
        }

        @Override
        public int GetHash() {
            return hash;
        }

        @Override
        protected void Draw() {
            modelCoaster.renderAll();
        }

        @Override
        public void PreRender()
        {
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(TextureResource);
        }
    }

    MeshBuffer meshBuffer;

    public CoasterMeshModel(IModelCustom Obj, ResourceLocation Tex, String id)
    {
        meshBuffer = new MeshBuffer(Obj, Tex, id);
        meshBuffer.SetDirty();
    }

    @Override
    public boolean IsLock() {
        return false;
    }

    @Override
    public void Reset() {

    }

    @Override
    public void Update() {

    }

    @Override
    public void Unload() {

    }

    @Override
    public void Invalidate() {
        if(meshBuffer != null) meshBuffer.DeleteBuffer();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {

    }

    @Override
    public void setRSPower(int power) {

    }

    @Override
    public void SetWorld(World world) {

    }

    @Override
    public void RenderModel(int pass, float partialTick) {
        if(pass==0) meshBuffer.Render();
    }

    @Override
    public boolean HasChild(){return false;}

    @Override
    public IModel[] GetChildren(){return null;}

    @Override
    public String GetName(){return "";}

    @Override
    public void SetActive(boolean active){}

    @Override
    public void SetOffset(float x, float y, float z){}

}