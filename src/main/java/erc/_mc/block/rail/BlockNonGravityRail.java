package erc._mc.block.rail;

import erc._mc.tileentity.TileEntityRail;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class BlockNonGravityRail extends BlockRail {

    @Override
    protected TileEntityRail GetTileEntityInstance() {
        return new TileEntityRail.AntiGravity();
    }

}
