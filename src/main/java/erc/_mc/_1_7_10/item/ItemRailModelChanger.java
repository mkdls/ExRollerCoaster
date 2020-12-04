package erc._mc._1_7_10.item;

import erc._mc._1_7_10.block.BlockCoasterModelConstructor;
import erc._mc._1_7_10.block.rail.BlockRail;
import erc._mc._1_7_10.tileentity.TileEntityCoasterModelConstructor;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import erc.renderer.rail.BlockModelRailRenderer;
import mochisystems.blockcopier.DefBlockModel;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IItemBlockModelHolder;
import mochisystems.util.IModelController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemRailModelChanger extends Item implements IItemBlockModelHolder {

	public ItemRailModelChanger(){}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ)
	{
		if(!stack.hasTagCompound()) return true;

		if(BlockRail.isBlockRail(world.getBlock(x, y, z))) {
			TileEntityRail tile = (TileEntityRail) world.getTileEntity(x, y, z);
			NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("model");
			tile.ChangeModel(new BlockModelRailRenderer(tile.getRail(), world, nbt, x, y, z));
		}

		if(world.getBlock(x, y, z) instanceof BlockCoasterModelConstructor) {
			TileEntityCoasterModelConstructor tile = (TileEntityCoasterModelConstructor) world.getTileEntity(x, y, z);
			NBTTagCompound nbt = (NBTTagCompound) stack.getTagCompound().getTag("model");
			tile.ChangeModel(new BlockModelRailRenderer(tile.rail, world, nbt, x, y, z));
		}
        return true;
	}

	@Override
	public IModel GetBlockModel(IModelController controller) {
		return new DefBlockModel(controller);
	}

	@Override
	public void OnSetInventory(IModel part, int slotidx, ItemStack itemStack, EntityPlayer player) {

	}
}
