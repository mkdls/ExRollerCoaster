package erc._mc.block.rail;

import erc.rail.ConstVelocityRail;
import erc._mc.tileentity.TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockConstVelocityRail extends BlockRail {

    @Override
    public TileEntityRail GetTileEntityInstance()
    {
        return new TileEntityRail.Const();
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
                ConstVelocityRail rail = (ConstVelocityRail)tile.getRail();
                if (rail.IsActive() != isPowered)
                {
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
//    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
//    {
//        if (!worldIn.isRemote)
//        {
//            boolean isPowered = worldIn.isBlockPowered(pos);
//
//            if (isPowered || blockIn.getDefaultState().canProvidePower())
//            {
//                ConstVelocityRail rail = (ConstVelocityRail)((TileEntityRail)worldIn.getTileEntity(pos)).getRail();
//
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
