package erc._mc.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._mc.tileentity.TileEntityRail;
import erc.coaster.Coaster;
import erc._mc.entity.EntityCoaster;
import erc.rail.IRail;
import io.netty.buffer.ByteBuf;
import mochisystems._core.Logger;
import net.minecraft.world.World;

public class MessageSyncCoasterPosStC implements IMessage, IMessageHandler<MessageSyncCoasterPosStC, IMessage> {

	public int entityID;
	public double paramT;
	public double speed;
	public int x = -1;
	public int y = -1;
	public int z = -1;

	public MessageSyncCoasterPosStC() {}

	public MessageSyncCoasterPosStC(EntityCoaster entity)
	{
        this.entityID = entity.getEntityId();
        this.paramT = entity.GetCoaster().pos.t;
	    this.speed = entity.GetCoaster().getSpeed();
	    IRail rail = entity.GetCoaster().pos.rail;
	    if(rail != null)
        {
            this.x = rail.GetController().x();
            this.y = rail.GetController().y();
            this.z = rail.GetController().z();
        }
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(this.entityID);
        buf.writeDouble(this.paramT);
		buf.writeDouble(this.speed);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        this.paramT = buf.readDouble();
	    this.speed = buf.readDouble();
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
    }

	@Override
    public IMessage onMessage(MessageSyncCoasterPosStC message, MessageContext ctx)
    {
        World world = FMLClientHandler.instance().getClient().theWorld;
		EntityCoaster entityCoaster = (EntityCoaster) world.getEntityByID(message.entityID);
		
		if(entityCoaster == null)return null;

		Coaster coaster = entityCoaster.GetCoaster();

        TileEntityRail tileRail = (TileEntityRail)world.getTileEntity(message.x, message.y, message.z);
        IRail rail = tileRail.getRail();

        coaster.SetNewRail(rail);
        coaster.setPosition(message.paramT);
        coaster.setSpeed(message.speed);
		Logger.debugInfo("update t");
//		if(message.connectparentID > -1)
//		{
//			 ERC_EntityCoaster parent = (ERC_EntityCoaster)FMLClientHandler.instance().getClient().theWorld.getEntityByID(message.connectparentID);
//			 if(parent == null)
//			 {
//				 coaster.killCoaster();
//				 return null;
//			 }
//			 ((ERC_EntityCoasterConnector)coaster).setParentPointer(parent);
//			 parent.connectionCoaster((ERC_EntityCoasterConnector) coaster);
//			 ((ERC_EntityCoasterConnector)coaster).setConnectParentFlag(-1);
//		}
        return null;
    }
    
}