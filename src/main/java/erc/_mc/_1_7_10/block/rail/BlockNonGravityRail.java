package erc._mc._1_7_10.block.rail;

import erc._mc._1_7_10.tileentity.TileEntityRail;

public class BlockNonGravityRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance() {
        return new TileEntityRail.AntiGravity();
    }

}
