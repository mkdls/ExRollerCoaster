package erc.model;

import mochisystems.blockcopier.DefBlockModel;
import mochisystems.math.Vec3d;
import mochisystems.util.IModel;
import mochisystems.util.IModelController;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

public class CoasterBlockModel extends DefBlockModel {

    public CoasterBlockModel(IModelController controller) {
        super(controller);
    }

    private float modelScale;
    private final Vec3d modelOffset = new Vec3d(0, 0, 0);
    private final Vec3d modelRotate = new Vec3d(0, 0, 0);

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        modelScale = partNbtOnConstruct.getFloat("scale");
        modelOffset.ReadFromNBT("offset", partNbtOnConstruct);
//        modelRotate.ReadFromNBT("rotate", partNbtOnConstruct);
        modelRotate.x = partNbtOnConstruct.getFloat("pitch");
        modelRotate.y = partNbtOnConstruct.getFloat("yaw");
        modelRotate.z = partNbtOnConstruct.getFloat("rot");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
    }

    @Override
    public void RenderModel(int pass, float partialTick) {
        GL11.glPushMatrix();
        GL11.glTranslated(modelOffset.x, modelOffset.y, modelOffset.z);
        GL11.glRotated(modelRotate.x, 1, 0, 0);
        GL11.glRotated(modelRotate.y, 0, 1, 0);
        GL11.glRotated(modelRotate.z, 0, 0, 1);
        GL11.glScalef(modelScale, modelScale, modelScale);
        super.RenderModel(pass, partialTick);
        GL11.glPopMatrix();
    }

    public boolean HasChild(){return false;}
    public IModel[] GetChildren(){return null;}
    public String GetName(){return "";}
    public void SetActive(boolean active){}
    public void SetOffset(float x, float y, float z){}
}
