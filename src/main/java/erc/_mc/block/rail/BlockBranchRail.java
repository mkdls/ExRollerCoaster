package erc._mc.block.rail;

import mochisystems.math.Vec3d;
import erc.rail.BranchRail;
import erc.rail.IRail;
import erc._mc.tileentity.TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockBranchRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance()
    {
        return new TileEntityRail.Branch();
    }

    @Override
    protected void onTileEntityInitFirst(World world, EntityLivingBase player, TileEntityRail tileEntityRail, int x, int y, int z)
    {
        double yaw = Math.toRadians(player.rotationYaw);
        double pit = -Math.toRadians(player.rotationPitch);
        Vec3d railPos = new Vec3d(x, y, z).add(0.5);
        Vec3d railUp = ConvertVec3FromMeta(world.getBlockMetadata(x, y, z));
        Vec3d railDir = new Vec3d(
                -Math.sin(yaw) * (railUp.x != 0 ? 0 : 1),
                Math.sin(pit) * (railUp.y != 0 ? 0 : 1),
                Math.cos(yaw) * (railUp.z != 0 ? 0: 1))
                .mul(10);
        Vec3d railSide = railUp.New().cross(railDir).normalize().mul(4);

        IRail rail = tileEntityRail.getRail();
        rail.SetBasePoint(railPos, railDir, railUp, 5f);
        rail.SetNextPoint(railPos.New().add(railDir).add(railSide), railDir, railUp, 5f);
        rail.ConstructCurve();
        ((BranchRail)rail).RotateSelected();
        rail.SetBasePoint(railPos, railDir, railUp, 5f);
        rail.SetNextPoint(railPos.New().add(railDir).sub(railSide), railDir, railUp, 5f);
        rail.ConstructCurve();
        ((BranchRail)rail).RotateSelected();
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        if (!world.isRemote)
        {
            boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z) || block.canProvidePower();

            if (isPowered)
            {
                BranchRail rail = ((BranchRail) ((TileEntityRail) world.getTileEntity(x, y, z)).getRail());

                rail.RotateSelected();
//                	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);

            }
        }
    }

//    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
//    {
//        if (!worldIn.isRemote)
//        {
//            boolean isPowered = worldIn.isBlockPowered(pos);
//            if (isPowered || blockIn.getDefaultState().canProvidePower())
//            {
//                BranchRail rail = (BranchRail) ((TileEntityRail)worldIn.getTileEntity(pos)).getRail();
//                rail.RotateSelected();
//                this.playSound((EntityPlayer)null, worldIn, pos);
//            }
//        }
//    }

//    protected void playSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos)
//    {
//        worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
//    }
}
