package erc._mc._1_7_10.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._mc._1_7_10._core.ERC_Core;
import erc._mc._1_7_10.item.ItemRailModelChanger;
import erc._mc._1_7_10.gui.GUIRailModelConstructor;
import erc.renderer.rail.ERCBlocksScanner;
import mochisystems._mc._1_7_10.tileentity.TileEntityBlocksScannerBase;
import mochisystems.blockcopier.*;
import mochisystems.math.Math;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.Random;

public class TileEntityRailModelConstructor extends TileEntityBlocksScannerBase {

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

	public GUIRailModelConstructor gui;

	public boolean FlagDrawEntity = false;

	public TileEntityRailModelConstructor()
	{
		super();
        LimitFrameLength = 6;
        LimitFrameWidth = 2;
        LimitFrameHeight = 1;
		modelName = "";
//		AuthorName = "";
	}

	public void SetSide(int side)
    {
        this.side = side;
    }

	@Override
    protected BlocksScanner InstantiateBlocksCopier(){
	    return new ERCBlocksScanner();
    }

	@Override
	public double getMaxRenderDistanceSquared() 
	{
		return 100000d;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() 
	{
		return INFINITE_EXTENT_AABB;
	}



    @Override
    public void setFrameLength(int add)
	{
		LimitFrameLength += add;
        LimitFrameLength = Math.Clamp(LimitFrameLength, 1, 32767);
        createVertex();
    }
    @Override
    public void setFrameWidth(int add)
    {
        LimitFrameWidth += add * 2;
        LimitFrameWidth = Math.Clamp(LimitFrameWidth, 0, 32767);
        createVertex();
    }

	@Override
    public void setFrameHeight(int add)
    {
        LimitFrameHeight += add * 2;
        LimitFrameHeight = Math.Clamp(LimitFrameHeight, 0, 32767);
        createVertex();
    }

    @Override
    public int getFrameLength(){return LimitFrameLength;}
    @Override
    public int getFrameHeight(){return LimitFrameHeight;}
    @Override
    public int getFrameWidth(){return LimitFrameWidth;}

    @Override
	public void resetFrameLength()
	{
        LimitFrameLength = 6;
        LimitFrameWidth = 2;
        LimitFrameHeight = 1;
		createVertex();
	}

    private boolean isDirX(int side){ return side == 4;}
    private boolean isDirNegX(int side){ return side == 5;}
    private boolean isDirY(int side){ return side == 0;}
    private boolean isDirNegY(int side){ return side == 1;}
    private boolean isDirZ(int side){ return side == 2;}
    private boolean isDirNegZ(int side){ return side == 3;}

    public void createVertex()
    {
        int height = LimitFrameHeight / 2;
        int width = LimitFrameWidth / 2;
        int side = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

        if(isDirX(side)) limitFrame.SetLengths(1, LimitFrameLength, -height, height, -width, width, isOdd,true);
        else if(isDirNegX(side)) limitFrame.SetLengths(-LimitFrameLength, -1, -height, height, -width, width, isOdd, true);
        else if(isDirY(side)) limitFrame.SetLengths(-width, width, 1, LimitFrameLength, -height, height, isOdd, true);
        else if(isDirNegY(side)) limitFrame.SetLengths(-width, width, -LimitFrameLength, -1, -height, height, isOdd, true);
        else if(isDirZ(side)) limitFrame.SetLengths(-width, width, -height, height, 1, LimitFrameLength, isOdd,true);
        else if(isDirNegZ(side)) limitFrame.SetLengths(-width, width, -height, height, -LimitFrameLength, -1, isOdd,true);
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

	public void toggleFlagDrawEntity()
	{
		FlagDrawEntity = !FlagDrawEntity;
	}


    public void render(Tessellator tess)
	{
		limitFrame.render(tess);
	}
	

    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);      
    	loadFromNBT(par1NBTTagCompound);
    }
	private void loadFromNBT(NBTTagCompound nbt)
    {
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
    }
    
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        saveToNBT(par1NBTTagCompound);
    }
    private void saveToNBT(NBTTagCompound nbt)
    {
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
    }

    @Override
    public void registerExternalParam(NBTTagCompound nbt)
    {
        super.registerExternalParam(nbt);

        int corex = xCoord, corey = yCoord, corez = zCoord;
        int localx = 0, localy = 0, localz = 0;
        switch(side)
        {
            case 0 : corey += 1; localx = LimitFrameWidth/2+1; localy = 1; localz = LimitFrameHeight/2+1; break;
            case 1 : corey -= 1; localx = LimitFrameWidth/2+1; localy = LimitFrameLength; localz = LimitFrameHeight/2+1; break;
            case 2 : corez += 1; localx = LimitFrameWidth/2+1; localy = LimitFrameHeight/2+1; localz = 1; break;
            case 3 : corez -= 1; localx = LimitFrameWidth/2+1; localy = LimitFrameHeight/2+1; localz = LimitFrameLength; break;
            case 4 : corex += 1; localx = 1; localy = LimitFrameHeight/2+1; localz = LimitFrameWidth/2+1; break;
            case 5 : corex -= 1; localx = LimitFrameLength; localy = LimitFrameHeight/2+1; localz = LimitFrameWidth/2+1; break;
        }
        // => FerrisPartBase
        nbt.setString("ModelName", modelName.equals("") ? "-NoName-" : modelName);

        // -> FerrisWheel
        nbt.setFloat("wsize", 1f);

        // => BlockReplicator
        nbt.setByte("constructorside", (byte)side);
        nbt.setInteger("copiedPosX", corex); // CTM
        nbt.setInteger("copiedPosY", corey);
        nbt.setInteger("copiedPosZ", corez);
        nbt.setInteger("originlocalx", localx);
        nbt.setInteger("originlocaly", localy);
        nbt.setInteger("originlocalz", localz);
        nbt.setFloat("widthratio", widthRatio);
        nbt.setFloat("heightratio", heightRatio);
        nbt.setInteger("copynum", 1);
    }

    @Override
    public ItemStack InstantiateModelItem() {
        return new ItemStack(ERC_Core.ItemRailBlockModelChanger);
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
	/////////////////////////SidedInventry///////////////////////

	 
//	@Override
//	public int getSizeInventory(){return 1;}
//
//	@Override
//	public ItemStack getStackInSlot(int idx) {return stackSlot;}
//
//	/**
//     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
//     * new stack.
//     */
//	@Override
//    public ItemStack decrStackSize(int slotIdx, int decrAmount)
//    {
//        if(slotIdx != 0) return null;
//        if (this.stackSlot == null) return null;
//
//        ItemStack itemstack;
//
//        if (stackSlot.stackSize <= decrAmount)
//        {
//            itemstack = stackSlot;
//            this.stackSlot = null;
//            return itemstack;
//        }
//        else
//        {
//            itemstack = this.stackSlot.splitStack(decrAmount);
//
//            if (this.stackSlot.stackSize == 0)
//            {
//                this.stackSlot = null;
//            }
//            return itemstack;
//        }
//    }

//	@Override
//	/**
//     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
//     * like when you close a workbench GUI.
//     */
//    public ItemStack getStackInSlotOnClosing(int slotIdx)
//    {
//        if(slotIdx != 0) return null;
//        if (this.stackSlot == null) return null;
//        ItemStack itemstack = this.stackSlot;
//        this.stackSlot = null;
//        return itemstack;
//    }
//
//	@Override
//	/**
//     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
//     */
//    public void setInventorySlotContents(int slotIdx, ItemStack itemstack)
//    {
//        if(slotIdx != 0) return;
//        if (this.stackSlot == null) return;
//        if((itemstack!=null) && !(itemstack.getItem() instanceof ItemRailModelChanger))return;
//        this.stackSlot = itemstack;
//
//        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
//        {
//            itemstack.stackSize = this.getInventoryStackLimit();
//        }
//    }
//
//	@Override
//	public String getInventoryName(){return "container.erc.railmodelconstructor";}
//
//	@Override
//	public boolean hasCustomInventoryName() {
//		return false;
//	}
//
//	@Override
//	public int getInventoryStackLimit() {return 64;}
//
//	@Override
//	public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
//	{
//		return true;
////		return this.worldObj.getTileEntity(this.CorePosX, this.CorePosY, this.CorePosZ) != this ? false : p_70300_1_.getDistanceSq((double)this.CorePosX + 0.5D, (double)this.CorePosY + 0.5D, (double)this.CorePosZ + 0.5D) <= 64.0D;
//	}
//
//	@Override
//	public void openInventory() {}
//
//	@Override
//	public void closeInventory() {}

//	@Override
	/**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
//    public boolean isItemValidForSlot(int slotidx, ItemStack itemstack)
//	{
//		return false;
//	}

//	@Override
//	/**
//     * Returns an array containing the indices of the slots that can be accessed by automation on the given constructSide of this
//     * block.
//     */
//    public int[] getAccessibleSlotsFromSide(int p_94128_1_)
//    {
//        return new int[1]; //TODO
//    }
//
//	@Override
//	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
//		return false;
//	}
//
//	@Override
//	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
//		return false;
//	}

}
