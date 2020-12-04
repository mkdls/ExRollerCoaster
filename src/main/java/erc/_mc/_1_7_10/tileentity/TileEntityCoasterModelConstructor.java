package erc._mc._1_7_10.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._mc._1_7_10._core.ERC_Core;
import erc._mc._1_7_10.gui.GUICoasterModelConstructor;
import erc._mc._1_7_10.item.ItemRailModelChanger;
import erc.rail.IRail;
import erc.rail.IRailController;
import erc.rail.Rail;
import erc.renderer.rail.DefaultRailRenderer;
import erc.renderer.rail.ERCBlocksScanner;
import erc.renderer.rail.RailRenderer;
import io.netty.buffer.ByteBuf;
import mochisystems._mc._1_7_10._core.Logger;
import mochisystems._mc._1_7_10.tileentity.TileEntityBlocksScannerBase;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems.blockcopier.*;
import mochisystems.math.Math;
import mochisystems.math.Vec3d;
import mochisystems.util.IModel;
import mochisystems.util.IModelController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.Random;

public class TileEntityCoasterModelConstructor extends TileEntityBlocksScannerBase implements IModelController, IRailController {

    // LimitFrame
    private int LimitFrameLength;
    private int LimitFrameWidth;
    private int LimitFrameHeight;

    private int side;
    private byte progressState = 0; // 0:no craft 1:isCrafting 2:Complete
    private int copyNum = 1;
    private float widthRatio = 1f;
    private float heightRatio = 1f;

    public String modelName = "";

    public GUICoasterModelConstructor gui;

    public IModel blockModel = null;
    public RailRenderer renderer;
    public Rail rail;

    // model value
    public float modelScale = 1;
    public final Vec3d modelOffset = new Vec3d(0, 0, 0);
    public final Vec3d modelRotate = new Vec3d(0, 0, 0);
    public float registeredModelScale = 1;
    public final Vec3d registeredModelOffset = new Vec3d(0, 0, 0);
    public final Vec3d registeredModelRotate = new Vec3d(0, 0, 0);

    public boolean FlagDrawEntity = false;

    public TileEntityCoasterModelConstructor() {
        super();
        LimitFrameLength = 6;
        LimitFrameWidth = 6;
        LimitFrameHeight = 6;
        modelName = "";

        rail = new Rail(this);
        Vec3d dir = new Vec3d(0, 0, 1);
        Vec3d up = new Vec3d(0, 1, 0);
        rail.SetBasePoint(new Vec3d(0, 0, -3), dir, up, 5f);
        rail.SetNextPoint(new Vec3d(0, 0, +3), dir, up, 5f);
        rail.SetPointNum(10);
        rail.ConstructCurve();
        renderer = new DefaultRailRenderer(rail);
        renderer.SetDirty();
    }

    public void SetSide(int side) {
        this.side = side;
    }

    @Override
    protected BlocksScanner InstantiateBlocksCopier() {
        return new ERCBlocksScanner();
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 100000d;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    public void setFrameLength(int add) {
        LimitFrameLength += add;
        LimitFrameLength = Math.Clamp(LimitFrameLength, 1, 32767);
        createVertex();
    }

    @Override
    public void setFrameWidth(int add) {
        LimitFrameWidth += add;
        LimitFrameWidth = Math.Clamp(LimitFrameWidth, 1, 32767);
        createVertex();
    }

    @Override
    public void setFrameHeight(int add) {
        LimitFrameHeight += add;
        LimitFrameHeight = Math.Clamp(LimitFrameHeight, 1, 32767);
        createVertex();
    }

    @Override
    public int getFrameLength(){return LimitFrameLength;}
    @Override
    public int getFrameHeight(){return LimitFrameHeight;}
    @Override
    public int getFrameWidth(){return LimitFrameWidth;}

    @Override
    public void resetFrameLength() {
        LimitFrameLength = 6;
        LimitFrameWidth = 6;
        LimitFrameHeight = 6;
        createVertex();
    }

    private boolean isDirX(int side) {
        return side == 4;
    }

    private boolean isDirNegX(int side) {
        return side == 5;
    }

    private boolean isDirY(int side) {
        return side == 0;
    }

    private boolean isDirNegY(int side) {
        return side == 1;
    }

    private boolean isDirZ(int side) {
        return side == 2;
    }

    private boolean isDirNegZ(int side) {
        return side == 3;
    }

    public void createVertex() {
        int height = LimitFrameHeight;
        int width = LimitFrameWidth;
        int side = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        limitFrame.SetLengths(1, LimitFrameWidth, 0, LimitFrameHeight - 1, 1, LimitFrameLength, isOdd,true);
    }


    public int getLimitFrameLength() {
        return LimitFrameLength;
    }

    public int getLimitFrameWidth() {
        return LimitFrameWidth;
    }

    public int getLimitFrameHeight() {
        return LimitFrameHeight;
    }

    public void toggleFlagDrawEntity() {
        FlagDrawEntity = !FlagDrawEntity;
    }

    private void DeleteModelBuffer() {
        if (blockModel != null) blockModel.Invalidate();
        blockModel = null;
    }

    public void render(Tessellator tess) {
        limitFrame.render(tess);
    }


    private String blockModelNBTKey = "CoasterModelConstructor";

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        loadFromNBT(nbt);
        if (blockModel != null){
            NBTTagCompound modelNbt = (NBTTagCompound) nbt.getTag(blockModelNBTKey);
            blockModel.readFromNBT(modelNbt);
            NBTTagCompound partNbt = (NBTTagCompound) modelNbt.getTag("model");
            registeredModelScale = partNbt.getFloat("scale");
            registeredModelOffset.ReadFromNBT("offset", partNbt);
            registeredModelRotate.SetFrom(
                    partNbt.getFloat("pitch"),
                    partNbt.getFloat("yaw"),
                    partNbt.getFloat("rot")
            );
        }
    }

    private void loadFromNBT(NBTTagCompound nbt) {
        LimitFrameLength = nbt.getInteger("framelength");
        LimitFrameWidth = nbt.getInteger("framewidth");
        LimitFrameHeight = nbt.getInteger("frameheight");
        FlagDrawEntity = nbt.getBoolean("flagdrawentity");
        modelName = nbt.getString("modelname");
        widthRatio = nbt.hasKey("widthratio") ? nbt.getFloat("widthratio") : 1f;
        heightRatio = nbt.hasKey("heightratio") ? nbt.getFloat("heightratio") : 1f;
        copyNum = nbt.hasKey("copynum") ? nbt.getInteger("copynum") : 1;
        progressState = nbt.getByte("progressstate");
        side = nbt.getByte("coreside");

        if (nbt.hasKey("modelscale")) modelScale = nbt.getFloat("modelscale");
        modelOffset.ReadFromNBT("modeloffset", nbt);
        modelRotate.ReadFromNBT("modelrotate", nbt);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        saveToNBT(nbt);
        if (stackSlot != null) {
            if (blockModel == null) blockModel = ((IItemBlockModelHolder) stackSlot.getItem()).GetBlockModel(this);
            nbt.setTag(blockModelNBTKey, stackSlot.getTagCompound());
        }
    }

    private void saveToNBT(NBTTagCompound nbt) {
        nbt.setInteger("framelength", LimitFrameLength);
        nbt.setInteger("framewidth", LimitFrameWidth);
        nbt.setInteger("frameheight", LimitFrameHeight);
        nbt.setBoolean("flagdrawentity", FlagDrawEntity);
        nbt.setString("modelname", modelName);
        nbt.setFloat("widthratio", widthRatio);
        nbt.setFloat("heightratio", heightRatio);
        nbt.setInteger("copynum", copyNum);
        nbt.setByte("progressstate", progressState);
        nbt.setInteger("coreside", side);

        nbt.setFloat("modelscale", modelScale);
        modelOffset.WriteToNBT("modeloffset", nbt);
        modelRotate.WriteToNBT("modelrotate", nbt);
    }

    @Override
	public Packet getDescriptionPacket() {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        this.writeToNBT(nbtTagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTagCompound);
	}
 
	@Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
        createVertex();
    }
	
	private boolean canCreateCore()
	{
		return isExistCore() || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode;
	}

    @Override
    protected boolean isExistCore()
	{
		if(stackSlot ==null)return false;
		return stackSlot.getItem() instanceof ItemRailModelChanger;
	}

    @SideOnly(Side.CLIENT)
    public void startConstructModel()
    {
        if(!canCreateCore())return;
        scanner.StartCopy(this, xCoord, yCoord, zCoord, limitFrame, FlagDrawEntity);
        this.markDirty();
    }

    @SideOnly(Side.CLIENT)
    public float getCookProgress()
    {
        return scanner.GetProgress();
    }

	public boolean isCrafting()
	{
		return progressState == 1;
	}

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote) return;
        scanner.UpdateProgressStatus();
        if(blockModel!=null)blockModel.Update();
        if(stackSlot == null) DeleteModelBuffer();
    }

    @Override
    public void registerExternalParam(NBTTagCompound nbt)
    {
        super.registerExternalParam(nbt);

        int corex = xCoord, corey = yCoord, corez = zCoord;

        // => FerrisPartBase
        nbt.setString("ModelName", modelName.equals("") ? "-NoName-" : modelName);

        // -> FerrisWheel
        nbt.setFloat("wsize", 1f);

        // => BlockReplicator
        nbt.setInteger("copiedPosX", corex); // CTM
        nbt.setInteger("copiedPosY", corey);
        nbt.setInteger("copiedPosZ", corez);
        nbt.setInteger("originlocalx", (limitFrame.lenX()+1)/2);
        nbt.setInteger("originlocaly", (limitFrame.lenY()+1)/2);
        nbt.setInteger("originlocalz", (limitFrame.lenZ()+1)/2);
        nbt.setFloat("widthratio", widthRatio);
        nbt.setFloat("heightratio", heightRatio);
        nbt.setInteger("copynum", 1);

        // => CoasterModel
        nbt.setFloat("scale", modelScale);
        modelOffset.WriteToNBT("offset", nbt);
//        modelRotate.WriteToNBT("rotate", nbt)
        nbt.setFloat("pitch", (float)modelRotate.x);
        nbt.setFloat("yaw", (float)modelRotate.y);
        nbt.setFloat("rot", (float)modelRotate.z);

        registeredModelScale = modelScale;
        registeredModelOffset.CopyFrom(modelOffset);
        registeredModelRotate.CopyFrom(modelRotate);
    }

    @Override
    public ItemStack InstantiateModelItem() {
        return new ItemStack(ERC_Core.itemCoasterModel);
    }



    //////////// IBLockCopyHandler end ///////////

    @Override
    protected void RecieveExtBlockData(NBTTagCompound nbt)
    {
        this.modelName = nbt.getString("ModelName");
    }

    private final Random random = new Random();
    public void SpillItemStack()
    {
        if (stackSlot != null)
        {
            float x = xCoord + this.random.nextFloat() * 0.8F + 0.1F;
            float y = yCoord + this.random.nextFloat() * 0.8F + 0.1F;
            float z = zCoord + this.random.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem;

            entityitem = new EntityItem(worldObj, x, y, z, new ItemStack(stackSlot.getItem(), 1, stackSlot.getItemDamage()));
            float f3 = 0.05F;
            entityitem.motionX = (double)((float)this.random.nextGaussian() * f3);
            entityitem.motionY = (double)((float)this.random.nextGaussian() * f3 + 0.2F);
            entityitem.motionZ = (double)((float)this.random.nextGaussian() * f3);

            if (stackSlot.hasTagCompound())
            {
                entityitem.getEntityItem().setTagCompound((NBTTagCompound)stackSlot.getTagCompound().copy());
            }

            stackSlot.stackSize = 0;
        }
    }

    ////////////// IModelController
    @Override
    public int CorePosX() {
        return xCoord;
    }

    @Override
    public int CorePosY() {
        return yCoord;
    }

    @Override
    public int CorePosZ() {
        return zCoord;
    }

    @Override
    public int CoreSide() {
        return side;
    }

    @Override
    public boolean IsInvalid() {
        return isInvalid();
    }

    @Override
    public boolean IsRemote() {
        return worldObj.isRemote;
    }

    @Override
    public World World() {
        return worldObj;
    }

    @Override
    public void markBlockForUpdate() {
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public IModel GetBlockModel(int x, int y, int z) {
        return null; //TODO?
    }

    @Override
    public IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess) {
        return null;
    }

    ////// for IModel

    @Override
    public void setWorldObj(World world) {
        super.setWorldObj(world);
        if(blockModel!=null)blockModel.SetWorld(world);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        DeleteModelBuffer();
        renderer.DeleteBuffer();
    }

    public void ChangeModel(RailRenderer renderer)
    {
        this.renderer.DeleteBuffer();
        this.renderer = renderer;
        Logger.debugInfo("change model");
        this.renderer.SetDirty();
    }

    @Override
    public void onChunkUnload() {
        if(blockModel!=null)blockModel.Unload();
    }

    @Override
    public void setInventorySlotContents(int slotIdx, ItemStack itemstack)
    {
        super.setInventorySlotContents(slotIdx, itemstack);
        if(stackSlot != null)
        {
            blockModel = ((IItemBlockModelHolder)stackSlot.getItem()).GetBlockModel(this);
            NBTTagCompound nbt = stackSlot.getTagCompound();
            blockModel.readFromNBT(nbt);
            blockModel.SetWorld(worldObj);

            NBTTagCompound modelNbt = (NBTTagCompound) nbt.getTag("model");
            registeredModelScale = modelNbt.getFloat("scale");
            registeredModelOffset.ReadFromNBT("offset", modelNbt);
            registeredModelRotate.SetFrom(
                    modelNbt.getFloat("pitch"),
                    modelNbt.getFloat("yaw"),
                    modelNbt.getFloat("rot")
            );
        }
    }

    ////////////// IRailController

    @Override
    public IRail GetRail(int x, int y, int z) {
        return rail;
    }

    @Override
    public int x() {
        return xCoord;
    }

    @Override
    public int y() {
        return yCoord;
    }

    @Override
    public int z() {
        return zCoord;
    }

    @Override
    public void NotifyChange() {

    }

    @Override
    public void FixConnection() {

    }

    @Override
    public void SyncData() {

    }

    @Override
    public void SyncMiscData() {

    }

    @Override
    public boolean RegisterPrevRailPos(IRailController prevController) {
        return false;
    }

    @Override
    public boolean RegisterNextRailPos(IRailController nextController) {
        return false;
    }

    @Override
    public void RegisterBufferForSync(ByteBuf buf) {

    }

    @Override
    public void ReadBufferForSync(ByteBuf buf) {

    }
}
