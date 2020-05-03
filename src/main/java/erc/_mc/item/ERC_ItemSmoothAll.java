package erc._mc.item;

import erc.rail.IRail;
import erc._mc.tileentity.TileEntityRail;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ERC_ItemSmoothAll extends Item {

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		
		if(world.isRemote == false)
		{
			TileEntity tile = world.getTileEntity(x, y, z);
			if(tile instanceof TileEntityRail)
			{
				IRail rail = ((TileEntityRail) tile).getRail();
				if(rail == null)return true;
				smoothBack(0, rail, rail, world);
				smoothNext(0, rail, rail, world);
			}
		}
			
		
		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}

	private void smoothBack(int num, IRail root, IRail rail, World world)
	{
		if(num >= 100)return;
		if(rail == null)return;
		if(num != 0 && root.equals(rail))return;

		rail.Smoothing();
		rail.ConstructCurve();
		rail.SyncData();
		IRail prev = rail.GetPrevRail();
		if(prev != null)
		{
			prev.SetNextPoint(rail.GetBasePoint());
			prev.ConstructCurve();
			prev.SyncData();
		}
		smoothBack(num++, root, prev, world);
	}

	private void smoothNext(int num, IRail root, IRail rail, World world)
	{
		if(num >= 100)return;
		if(rail == null)return;
		if(num != 0 && root.equals(rail))return;

		IRail next = rail.GetNextRail();
		if(next != null)
		{
			smoothNext(num++, root, next, world);
			rail.SetNextPoint(next.GetBasePoint());
		}

		rail.Smoothing();
		rail.ConstructCurve();
		rail.SyncData();
	}
}
