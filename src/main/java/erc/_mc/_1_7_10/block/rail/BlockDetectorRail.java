package erc._mc._1_7_10.block.rail;

import erc.rail.DetectorRail;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import net.minecraft.world.IBlockAccess;

public class BlockDetectorRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance() {
        return new TileEntityRail.Detector();
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int p_149709_5_)
    {
        DetectorRail rail = ((DetectorRail) ((TileEntityRail) world.getTileEntity(x, y, z)).getRail());
        return rail.ExistCoaster() ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int p_149748_5_)
    {
        DetectorRail rail = ((DetectorRail) ((TileEntityRail) world.getTileEntity(x, y, z)).getRail());
        return rail.ExistCoaster() ? 15 : 0;
    }

}
