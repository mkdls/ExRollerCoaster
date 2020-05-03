package erc._mc.block.rail;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Core;
import erc._mc.item.ItemCoaster;
import erc.manager.AutoRailConnectionManager;
import mochisystems.math.Vec3d;
import erc.rail.IRail;
import erc._mc.tileentity.TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRail extends BlockContainer{

	public BlockRail()
	{
		super(Material.ground);
	}
 
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2)
	{
		return super.getIcon(par1,par2);
	}
 
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){}
 
	@Override
	public int getRenderType()
	{
		return ERC_Core.blockRailRenderId; // RenderBlockRail�p
	}

	private static Block textureBase;

	public Block setTextureBase(Block textureBase)
	{
		this.textureBase = textureBase;
		return this;
	}

	protected TileEntityRail GetTileEntityInstance()
	{
		return new TileEntityRail.Normal();
	}

	public static Block GetTextureBase()
	{
		return textureBase;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
 
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		if (player.getHeldItem().getItem() instanceof ItemCoaster) return false;

		AutoRailConnectionManager.SaveRailForOpenGUI((TileEntityRail) world.getTileEntity(x, y, z));
		if(!world.isRemote) {
			player.openGui(ERC_Core.INSTANCE, ERC_Core.GUIID_RailBase, player.worldObj, x, y, z);
		}

		return true;
    }
	
	// Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		return side;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemStack)
	{
		super.onBlockPlacedBy(world, x, y, z, placer, itemStack);
		if(world.isRemote)
		{
			AutoRailConnectionManager.ConnectToMemoriedPosition(x, y, z);
		}
		else
		{
			TileEntityRail tileEntityRail = (TileEntityRail) world.getTileEntity(x, y ,z);
			onTileEntityInitFirst(world, placer, tileEntityRail, x, y, z);
			tileEntityRail.SyncData(placer);
		}
	}

	protected void onTileEntityInitFirst(World world, EntityLivingBase player, TileEntityRail tileEntityRail, int x, int y, int z)
	{
		double yaw = Math.toRadians(player.rotationYaw);
		double pit = -Math.toRadians(player.rotationPitch);
		Vec3d railPos = new Vec3d(x, y, z).add(0.5);
		Vec3d railUp = ConvertVec3FromMeta(world.getBlockMetadata(x, y, z));
		Vec3d railDir = new Vec3d(
			-Math.sin(yaw) * (railUp.x != 0 ? 0 : 1),
			Math.sin(pit) * (railUp.y != 0 ? 0 : 1),
			Math.cos(yaw) * (railUp.z != 0 ? 0: 1));

		tileEntityRail.InitRail(railPos, railDir, railUp);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta)
	{
		TileEntityRail tileEntity = (TileEntityRail)world.getTileEntity(x, y, z);
		if(tileEntity == null) return;
		IRail rail = tileEntity.getRail();
		if(rail == null) return;

		IRail prev = rail.GetPrevRail();
		if(prev!=null)prev.SetNextRail(null);
		IRail next = rail.GetNextRail();
		if(next!=null)next.SetPrevRail(null);
		super.breakBlock(world, x, y, z, block, meta);
	}

	// can't use this because this method don't be called in creative and this is called after delete tileentity
//	@Override
//	public void harvestBlock(World world, EntityPlayer player, int CorePosX, int y, int z, int p_149636_6_)
//	{
//		ERC_Logger.debugInfo("harvest in "+(world.isRemote ? "client" : "server"));
////		IRail rail = ((TileEntityRail)world.getTileEntity(CorePosX, y, z)).getRail();
////		ERC_MessageSaveBreakRailStC packet = new ERC_MessageSaveBreakRailStC(rail.PrevRail(), rail.NextRail());
////		ERC_PacketHandler.INSTANCE.sendTo(packet,(EntityPlayerMP) player);
//		super.harvestBlock(world, player, CorePosX, y, z, p_149636_6_);
//	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int x, int y, int z)
	{
		this.setBlockBounds(
				0.25f,
				0.25F,
				0.25F,
				0.75F,
				0.75F,
				0.75F);

	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
	    return null;
//		return AxisAlignedBB.getBoundingBox(
//				((double)x)+this.minX,
//				((double)y)+this.minY,
//				((double)z)+this.minZ,
//				((double)x)+this.maxX,
//				((double)y)+this.maxY,
//				((double)z)+this.maxZ
//				);
	}

 
//	@SideOnly(Side.CLIENT)
//	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int CorePosX, int y, int z)
//	{
//		return AxisAlignedBB.getBoundingBox(
//				((double)CorePosX)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
//				((double)CorePosX)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
//				);
//	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata)
	{
		return GetTileEntityInstance();
	}

	public static boolean isBlockRail(Block block)
	{
		 return block instanceof BlockRail;
	}

    protected Vec3d ConvertVec3FromMeta(int meta)
    {
    	switch(meta){
    	case 0:return new Vec3d(0, -1, 0);
    	case 1:return new Vec3d(0, 1, 0);
    	case 2:return new Vec3d(0, 0, -1);
    	case 3:return new Vec3d(0, 0, 1);
    	case 4:return new Vec3d(-1, 0, 0);
    	case 5:return new Vec3d(1, 0, 0);
    	}
		return new Vec3d(0, 0, 0);
    }
}
