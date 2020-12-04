package erc._mc._1_7_10.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import erc.rail.Rail;
import io.netty.buffer.ByteBuf;

public class ERC_MessageConnectRailCtS implements IMessage, IMessageHandler<ERC_MessageConnectRailCtS, IMessage> {

	// �N���C�A���g���痈�郌�[���̐ڑ��v�����b�Z�[�W
	
	public int bx, by, bz;
	public int nx, ny, nz;
	
	public ERC_MessageConnectRailCtS(){}
	
	public ERC_MessageConnectRailCtS(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
  	}
	
	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.bx);
		buf.writeInt(this.by);
		buf.writeInt(this.bz);
		buf.writeInt(this.nx);
		buf.writeInt(this.ny);
		buf.writeInt(this.nz);
	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.bx = buf.readInt();
	    this.by = buf.readInt();
	    this.bz = buf.readInt();
	    this.nx = buf.readInt();
	    this.ny = buf.readInt();
	    this.nz = buf.readInt();
    }
	
	@Override
    public IMessage onMessage(ERC_MessageConnectRailCtS message, MessageContext ctx)
    {
		TileEntityRail BaseRail = (TileEntityRail)ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.bx, message.by, message.bz);
		TileEntityRail NextRail = (TileEntityRail)ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.nx, message.ny, message.nz);

//		ERC_Logger.info("CorePosX:"+ message.bx+"y:"+message.by+"z:"+message.bz);
//		ERC_Logger.info("CorePosX:"+ message.nx+"y:"+message.ny+"z:"+message.nz);

        if ((BaseRail != null && NextRail != null))
        {
			Rail.Connect(BaseRail.getRail(), NextRail.getRail());
		}
        return null;
    }
    
}