package erc._mc.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc.loader.ModelPackLoader;
import erc._mc.network.ERC_MessageSpawnRequestWithCoasterOpCtS;
import erc._mc.network.ERC_PacketHandler;
import erc._mc.block.rail.BlockRail;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.Collection;

public class ItemCoaster extends Item{

	protected IIcon itemIcons[];
	protected String[] modelIDs;

	protected int modelIndex = 0;
	public int getModelIndex(){return modelIndex;}

	@SideOnly(Side.CLIENT)
    public void ScrollMouseHweel(int dhweel)
    {
    	modelIndex += dhweel>0?1:-1;
		if(modelIndex >= ModelPackLoader.getCoasterModelCollection().size()) modelIndex =0;
		else if(modelIndex < 0) modelIndex = ModelPackLoader.getCoasterModelCollection().size() - 1;
    }

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iIconRegister)
	{
		Collection<ModelPackLoader.CoasterPackData> collection = ModelPackLoader.getCoasterModelCollection();
		itemIcons = new IIcon[collection.size()];
		modelIDs = new String[collection.size()];
		int i = 0;
		for(ModelPackLoader.CoasterPackData data : collection)
		{
			itemIcons[i] = iIconRegister.registerIcon(data.IconName);
			modelIDs[i] = data.MainSetting.ModelID;
			i++;
		}
		itemIcon = itemIcons[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int p_77617_1_)
	{
		return itemIcons[modelIndex];
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
	{
		if (!BlockRail.isBlockRail(world.getBlock(x, y, z))) return false;
		if(itemStack.stackSize == 0) return false;

		if (world.isRemote) {
			setCoasterOnRail(x, y, z, -1);
		}

		--itemStack.stackSize;
		return true;
	}

	public void setCoasterOnRail(int x, int y, int z, int parentID)
	{
		ERC_MessageSpawnRequestWithCoasterOpCtS packet =
				new ERC_MessageSpawnRequestWithCoasterOpCtS(ModelPackLoader.GetHeadCoasterSettings(modelIDs[modelIndex]), x, y, z, parentID);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);
	}
	
}
