package erc._mc.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._core.ERC_Core;
import erc._mc.tileentity.TileEntityCoasterModelConstructor;
import erc._mc.tileentity.TileEntityRailModelConstructor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;


public class BlockCoasterModelConstructor extends BlockContainer{

	@SideOnly(Side.CLIENT)
	private IIcon TopIcon;
	@SideOnly(Side.CLIENT)
	private IIcon SideIcon;

	public BlockCoasterModelConstructor()
	{
		super(Material.ground);
		this.setHardness(1.0F);
		this.setResistance(2000.0F);
		this.setLightOpacity(0);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		this.setLightLevel(0.0F);
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if(side==1 || side==0)return TopIcon;
		else return SideIcon;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		this.TopIcon = iconRegister.registerIcon(ERC_Core.MODID+":ferrisBasketConstructor");
		this.SideIcon = iconRegister.registerIcon(ERC_Core.MODID+":ferrisBasketConstructor_s");
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
		//OPEN GUI
		player.openGui(ERC_Core.INSTANCE, ERC_Core.GUIID_CoasterModelConstructor, player.worldObj, x, y, z);
        return true;
    }

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityCoasterModelConstructor();
	}


	// Args: World, X, Y, Z, constructSide, hitX, hitY, hitZ, block metadata
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta)
	{
		return side;
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack)
	{
		TileEntityCoasterModelConstructor tile = (TileEntityCoasterModelConstructor) world.getTileEntity(x, y, z);
		tile.createVertex();
		tile.SetSide(world.getBlockMetadata(x, y, z));
	}

	public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_)
    {
		TileEntityCoasterModelConstructor tile = (TileEntityCoasterModelConstructor)world.getTileEntity(x, y, z);

        if (tile != null)
        {
            for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1)
            {
                tile.SpillItemStack();
            }

            world.func_147453_f(x, y, z, block);
        }

        super.breakBlock(world, x, y, z, block, p_149749_6_);
    }

	//	// �u���b�N���j�󂳂ꂽ��Ă΂��@
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int i)
	{	
//		if(!Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode)
//			this.dropBlockAsItem(world, CorePosX, y, z, new ItemStack(this));
	}
 
	//�����蔻��B�T�{�e����\�E���T���h���Q�l�ɂ���Ɨǂ��B�R�R�̐ݒ������ƁAonEntityCollidedWithBlock���Ă΂��悤�ɂȂ�
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z)
	{
		return AxisAlignedBB.getBoundingBox(
				((double)x)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
				((double)x)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
				);
	}
//
//	//�u���b�N�Ɏ��_�����킹�����ɏo�Ă��鍕�����̃A��
//	@SideOnly(Side.CLIENT)
//	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int CorePosX, int y, int z)
//	{
//		return AxisAlignedBB.getBoundingBox(
//				((double)CorePosX)+this.minX,((double)y)+this.minY,((double)z)+this.minZ,
//				((double)CorePosX)+this.maxX,((double)y)+this.maxY,((double)z)+this.maxZ
//				);
//	}

}
