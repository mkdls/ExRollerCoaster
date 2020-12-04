package erc.renderer.rail;

import erc._mc._1_7_10.block.BlockCoasterModelConstructor;
import erc._mc._1_7_10.block.BlockRailModelConstructor;
import mochisystems.blockcopier.BlocksScanner;
import mochisystems.math.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

public class ERCBlocksScanner extends BlocksScanner {

    int slotIdx;
    int layer;

//	private int ConnectorNum;
    private class Connector{
        Vec3d pos;
        String name;
    }
	public ArrayList<Connector> connectors = new ArrayList<>();

    public void SetIndex(int slotIdx, int layer)
    {
        this.slotIdx = slotIdx;
        this.layer = layer;
    }

	protected void allocBlockArray(int x, int y, int z)
	{
        connectors.clear();
		super.allocBlockArray(x, y, z);
    }

    private boolean isExcludedBlock(Block block)
    {
        if(block instanceof BlockRailModelConstructor) return true;
        if(block instanceof BlockCoasterModelConstructor) return true;

        return false;
    }

	protected void setBlock(Block block, int meta, int x, int y, int z, TileEntity tile)
	{
		if(isExcludedBlock(block)) return;
//
//		if(block instanceof blockFerrisConnector)
//		{
//            Connector c = new Connector();
//            c.tile = (TileEntityConnector)tile;
//            c.pos = new Vec3d(x-(srcPosMaxX+srcPosMinX)/2, y-(srcPosMaxY+srcPosMinY)/2, z-(srcPosMaxZ+srcPosMinZ)/2);
//            c.name =  c.tile.GetName();
//			connectors.add(c);
//			return;
//		}
//		else if (block instanceof blockFerrisConstructor)
//        {
//            if(((TileEntityFerrisConstructor)tile).isCoreConnector) {
//                Connector c = new Connector();
//                c.pos = new Vec3d(0, 0, 0);
//                c.name = "_Core_";
//                connectors.add(c);
//                return;
//            }
//        }
        super.setBlock(block, meta, x, y, z, tile);
    }

//    public void makeTag(NBTTagCompound nbt, IBLockCopyHandler handler)
//    {
//        super.makeTag(nbt, handler);
//        NBTTagCompound model = (NBTTagCompound) nbt.getTag("model");
//        int connectorNum = connectors.size();
//        model.setInteger("connectornum", connectorNum);
//        int i = 0;
//        for( Connector connector : connectors)
//        {
//            connector.pos.WriteToNBT("connector"+i, model);
//            model.setString("connectorName"+i, connector.name);
//
//            TileEntityFerrisConstructor childConstructor = connector.tile.isRegisteredConstructor();
//            if(childConstructor != null)
//            {
//                int cx = childConstructor.CorePosX;
//                int cy = childConstructor.CorePosY;
//                int cz = childConstructor.CorePosZ;
//                BlocksScanner scanner = new MFWBlocksCopier();
//                scanner.Init(childConstructor, cx, cy, cz, childConstructor.GetLimitFrame(), childConstructor.FlagDrawEntity);
//                scanner.Register();
//                nbt.setTag("PartSlot"+i, scanner.GetNbt());
//                handler.InstantiateModelItem().writeToNBT(scanner.GetNbt());
//            }
//            i++;
//        }
//    }

}
