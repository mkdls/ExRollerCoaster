package erc._mc._1_7_10.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.rail.IRail;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import erc.rail.IRailController;
import io.netty.buffer.ByteBuf;

public class MessageSyncRailStC implements IMessage, IMessageHandler<MessageSyncRailStC, IMessage> {

	IRail rail;
	boolean isNeedReconstruct;

	public MessageSyncRailStC(){}

	public MessageSyncRailStC(IRail rail, boolean needToReconstructCurve)
	{
		this.rail = rail;
        isNeedReconstruct = needToReconstructCurve;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		IRailController controller = rail.GetController();
        buf.writeInt(controller.x());
        buf.writeInt(controller.y());
        buf.writeInt(controller.z());
        controller.RegisterBufferForSync(buf);
		rail.WriteToBytes(buf);
        buf.writeBoolean(isNeedReconstruct);
    }

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int x = buf.readInt();
		int y = buf.readInt();
		int z = buf.readInt();
        TileEntityRail tile = (TileEntityRail)FMLClientHandler.instance().getClient().theWorld.getTileEntity(x, y, z);
        tile.ReadBufferForSync(buf);
        rail = tile.getRail();
        rail.SetController(tile);
		rail.ReadFromBytes(buf);
        isNeedReconstruct = buf.readBoolean();
        if(isNeedReconstruct){
			rail.ForceDirty();
            rail.ConstructCurve();
            tile.ConstructModel();
        }
	}

	@Override
    public IMessage onMessage(MessageSyncRailStC message, MessageContext ctx)
    {
    	return null;
    }
    
}
