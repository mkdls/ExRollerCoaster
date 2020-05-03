package erc._mc.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._mc.entity.EntityCoaster;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class ERC_MessageRequestConnectCtS implements IMessage, IMessageHandler<ERC_MessageRequestConnectCtS, IMessage> {

	public int playerEntityID;
	public int CoasterID;
	
	public ERC_MessageRequestConnectCtS(){}
	
	public ERC_MessageRequestConnectCtS(int playerid, int coasterid)
	{
		playerEntityID = playerid;
		CoasterID = coasterid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.playerEntityID);
		buf.writeInt(this.CoasterID);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
		this.playerEntityID = buf.readInt();
		this.CoasterID = buf.readInt();
    }	    	

	@Override
    public IMessage onMessage(ERC_MessageRequestConnectCtS message, MessageContext ctx)
    {
    	World world = ctx.getServerHandler().playerEntity.worldObj;
		EntityCoaster coaster = (EntityCoaster) world.getEntityByID(message.CoasterID);
//		ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageCoasterMisc(coaster, 2));
		return null;
    }
}