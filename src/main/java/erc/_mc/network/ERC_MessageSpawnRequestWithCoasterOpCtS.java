package erc._mc.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.coaster.Coaster;
import erc.coaster.CoasterSettings;
import erc._mc.entity.EntityCoaster;
import erc._mc.tileentity.TileEntityRail;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class ERC_MessageSpawnRequestWithCoasterOpCtS implements IMessage, IMessageHandler<ERC_MessageSpawnRequestWithCoasterOpCtS, IMessage> {
	
	int itemID;
	int modelID;
	CoasterSettings settings;
	int x, y, z;
	int parentID = -1;
	
	public ERC_MessageSpawnRequestWithCoasterOpCtS()
	{
		settings = CoasterSettings.Default();
	}
	
	public ERC_MessageSpawnRequestWithCoasterOpCtS(CoasterSettings settings, int x, int y, int z)
	{
		this.settings = settings;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public ERC_MessageSpawnRequestWithCoasterOpCtS(CoasterSettings settings, int x, int y, int z, int parentid)
	{
		this(settings, x, y, z);
		parentID = parentid;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(itemID);
		buf.writeInt(modelID);
		byte[] data = settings.ModelID.getBytes();
        buf.writeInt(data.length);
        buf.writeBytes(data);
		buf.writeDouble(settings.Width);
		buf.writeDouble(settings.Height);
		buf.writeDouble(settings.Weight);
		buf.writeDouble(settings.ConnectLength);
		buf.writeInt(settings.Seats.length);
		for(int i = 0; i < settings.Seats.length; ++i)
		{
			buf.writeFloat(settings.Seats[i].SeatSize);
			settings.Seats[i].LocalPosition.WriteBuf(buf);
			settings.Seats[i].LocalRotationDegree.WriteBuf(buf);
		}
		buf.writeInt(x);
		buf.writeInt(y); 
		buf.writeInt(z); 
		buf.writeInt(parentID); 
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
	{
		itemID = buf.readInt(); 
		modelID = buf.readInt();
		settings = CoasterSettings.Default();
		byte[] bytes = new byte[buf.readInt()];
		buf.readBytes(bytes);
		settings.ModelID = new String(bytes);
		settings.Width = buf.readDouble();
		settings.Height = buf.readDouble();
		settings.Weight = buf.readDouble();
		settings.ConnectLength = buf.readDouble();
        settings.setSeatNum(buf.readInt());
		for(int i = 0; i < settings.Seats.length; ++i)
		{
			settings.Seats[i].SeatSize = buf.readFloat();
			settings.Seats[i].LocalPosition.New().ReadBuf(buf);
			settings.Seats[i].LocalRotationDegree.New().ReadBuf(buf);
		}
		x = buf.readInt(); 
		y = buf.readInt(); 
		z = buf.readInt(); 
		parentID = buf.readInt();
	}

	@Override
    public IMessage onMessage(ERC_MessageSpawnRequestWithCoasterOpCtS m, MessageContext ctx)
    {
    	boolean isParentCoaster = m.parentID == -1;

		// spawn!
		World world = ctx.getServerHandler().playerEntity.worldObj;
		TileEntityRail tile = (TileEntityRail)world.getTileEntity(m.x, m.y, m.z);
//		Coaster coaster = isParentCoaster ? new Coaster() : new ConnectionCoaster();
		EntityCoaster entitycoaster = new EntityCoaster(world, m.settings, m.x+0.5, m.y+0.5, m.z+0.5);
		Coaster coaster = entitycoaster.GetCoaster();
		coaster.SetNewRail(tile.getRail());
		coaster.SetSettingData(m.settings);

		if(!isParentCoaster)
		{
			EntityCoaster parent = (EntityCoaster) world.getEntityByID(m.parentID);
			parent.GetCoaster().ConnectChild(coaster);
		}
		
		world.spawnEntityInWorld(entitycoaster);
		entitycoaster.SpawnSeatEntity();
		tile.getRail().OnEnterCoaster();
		return null;
    }
}