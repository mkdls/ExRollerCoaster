package erc._mc._1_7_10.block.rail;


import erc._mc._1_7_10.tileentity.TileEntityRail;
import erc.rail.AccelRail;
import mochisystems._mc._1_7_10._core.Logger;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockAccelRail extends BlockRail{

    @Override
    protected TileEntityRail GetTileEntityInstance()
    {
        return new TileEntityRail.Accel();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (!world.isRemote)
        {
            boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z);

            if (isPowered || block.canProvidePower())
            {
                TileEntityRail tile = (TileEntityRail)world.getTileEntity(x, y, z);
                AccelRail rail = (AccelRail)tile.getRail();
                if (rail.IsActive() != isPowered)
                {
                    Logger.debugInfo("cycle");
                    rail.CycleActivation();
                    tile.SyncMiscData();
                    this.playSound(null, world, x, y, z, isPowered);
                }
            }
        }
    }

    protected void playSound(@Nullable EntityPlayer player, World world, int x, int y, int z, boolean isPowered)
    {
        world.playAuxSFXAtEntity(null, 1003, x, y, z, 0);
    }


//    @Override
//    public void onNeighborBlockChange(World world, int CorePosX, int y, int z, Block block)
//    {
//        if (!world.isRemote)
//        {
//            boolean isPowered = world.isBlockIndirectlyGettingPowered(CorePosX, y, z);
//
//            if (isPowered || blockIn.getDefaultState().canProvidePower())
//            {
//                AccelRail rail = (AccelRail)((TileEntityRail)worldIn.getTileEntity(pos)).getRail();
//                if (rail.IsActive() != isPowered)
//                {
//                    rail.CycleActivation();
//                    this.playSound((EntityPlayer)null, worldIn, pos, isPowered);
//                }
//            }
//        }
//    }
//
//    protected void playSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos, boolean isPowered)
//    {
//        if (isPowered)
//        {
//            worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
//        }
//        else
//        {
//            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_WOOD_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
//        }
//    }
}
