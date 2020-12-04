package erc._mc._1_7_10.network;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.coaster.Coaster;
import erc.coaster.CoasterSettings;
import erc._mc._1_7_10.entity.EntityCoaster;
import erc.rail.IRail;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class MessageSyncCoasterSettings implements IMessage, IMessageHandler<MessageSyncCoasterSettings, IMessage> {

	public int entityID;
	public double paramT;
	public double speed;
	public int x;
	public int y;
	public int z;
	public CoasterSettings settings;
	public State state = State.request;

	public enum State{
	    request(0), answer(1);
	    int idx;
	    State(int i){idx = i;}
	    public static State toState(int idx){return (idx==0)?State.request:State.answer;}
	}
	
	public MessageSyncCoasterSettings()
	{
		settings = CoasterSettings.Default();
	}

	public MessageSyncCoasterSettings(State state, int entityId)
    {
        this.state = state;
        this.entityID = entityId;
    }
	
	public MessageSyncCoasterSettings(int id, double t, double v, IRail rail, CoasterSettings settings)
	{
		this.paramT = t;
	    this.entityID = id;
	    this.speed = v;
	    this.x = rail.GetController().x();
	    this.y = rail.GetController().y();
	    this.z = rail.GetController().z();
	    this.settings = settings;
	    state = State.answer;
	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
        buf.writeInt(this.entityID);
        buf.writeInt(state.idx);
        if(state == State.request) return;

        buf.writeDouble(this.paramT);
		buf.writeDouble(this.speed);
		buf.writeInt(this.x);
		buf.writeInt(this.y);
		buf.writeInt(this.z);
		byte[] data = settings.ModelID.getBytes();
		buf.writeInt(data.length);
		buf.writeBytes(data);
		buf.writeDouble(settings.Width);
		buf.writeDouble(settings.Height);
		buf.writeDouble(settings.Weight);
		buf.writeDouble(settings.ConnectLength);



        buf.writeInt(settings.Seats.length);
        for (int i=0; i<settings.Seats.length; ++i)
		{
			buf.writeFloat(settings.Seats[i].SeatSize);
			settings.Seats[i].LocalPosition.WriteBuf(buf);
			settings.Seats[i].LocalRotationDegree.WriteBuf(buf);
		}
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
        this.entityID = buf.readInt();
        if(State.toState(buf.readInt()) == State.request) return;

        this.paramT = buf.readDouble();
	    this.speed = buf.readDouble();
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
		byte[] bytes = new byte[buf.readInt()];
		buf.readBytes(bytes);
		settings.ModelID = new String(bytes);
		settings.Width = buf.readDouble();
		settings.Height = buf.readDouble();
		settings.Weight = buf.readDouble();
		settings.ConnectLength = buf.readDouble();

        settings.setSeatNum(buf.readInt());
		for (int i=0; i<settings.Seats.length; ++i)
		{
			settings.Seats[i].SeatSize = buf.readFloat();
			settings.Seats[i].LocalPosition.ReadBuf(buf);
			settings.Seats[i].LocalRotationDegree.ReadBuf(buf);
		}
    }
	
	@Override
    public IMessage onMessage(MessageSyncCoasterSettings message, MessageContext ctx)
    {
        World world = FMLClientHandler.instance().getClient().theWorld;
		EntityCoaster entityCoaster = (EntityCoaster) world.getEntityByID(message.entityID);
		
		if(entityCoaster == null)return null;

		Coaster coaster = entityCoaster.GetCoaster();

        if(state == State.request)
        {
            MessageSyncCoasterSettings packet =
                    new MessageSyncCoasterSettings(
                            entityCoaster.getEntityId(),
                            coaster.pos.t(), coaster.getSpeed(),
                            coaster.pos.rail,
                            coaster.GetSettings());
            ERC_PacketHandler.INSTANCE.sendTo(packet, ctx.getServerHandler().playerEntity);
            return null;
        }

        TileEntityRail tileRail = (TileEntityRail)world.getTileEntity(message.x, message.y, message.z);
        IRail rail = tileRail.getRail();

		coaster.setPosition(message.paramT);
		coaster.setSpeed(message.speed);
		coaster.SetSettingData(message.settings);
		coaster.SetNewRail(rail);

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