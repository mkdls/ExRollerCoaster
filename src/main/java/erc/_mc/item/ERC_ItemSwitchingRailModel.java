package erc._mc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.loader.ModelPackLoader;
import erc._mc.block.rail.BlockRail;
import erc._mc.gui.GUIRail;
import erc._mc.network.ERC_MessageRailGUICtS;
import erc._mc.network.ERC_PacketHandler;
import erc._mc.tileentity.TileEntityRail;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ERC_ItemSwitchingRailModel extends Item{

	private int modelCount = 0;
	protected IIcon itemIcons[];
	
	public ERC_ItemSwitchingRailModel(){}
	
    public int getModelCount(){return modelCount;}
    
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	if (world.isRemote)
    	{
	    	if (!BlockRail.isBlockRail(world.getBlock(x, y, z))) return false;
	    	
	    	TileEntityRail tile = (TileEntityRail) world.getTileEntity(x, y, z);
	    	
	    	tile.ChangeModel(modelCount);
	    	
	    	ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(x, y, z, GUIRail.editFlag.RailModelIndex.ordinal(), modelCount);
	    	ERC_PacketHandler.INSTANCE.sendToServer(packet);
    	}
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public void ScrollMouseHweel(int dhweel)
    {
//    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
    	modelCount += dhweel>0?1:-1;
    	if(modelCount >= ModelPackLoader.getRailModelCount()+1) modelCount=0;
    	else if(modelCount < 0)modelCount = ModelPackLoader.getRailModelCount();
//    	ERC_Logger.info("modelcount:"+modelIndex);
    }
    
	@SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister p_94581_1_)
    {
		String[] names = ModelPackLoader.getRailIconStrings();
		itemIcons = new IIcon[names.length];
    	for(int i=0;i<names.length;++i)
    	{
    		this.itemIcons[i] = p_94581_1_.registerIcon(names[i]);
    	}
    	itemIcon = itemIcons[0];
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_) 
	{
		return itemIcons[modelCount];
	}
}
