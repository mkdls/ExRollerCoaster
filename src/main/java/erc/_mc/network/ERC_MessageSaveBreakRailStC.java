package erc._mc.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc.manager.AutoRailConnectionManager;
import erc.rail.IRail;
import erc.rail.IRailController;
import io.netty.buffer.ByteBuf;

public class ERC_MessageSaveBreakRailStC implements IMessage, IMessageHandler<ERC_MessageSaveBreakRailStC, IMessage>{

	// �󂵂����[���u���b�N�̐ڑ�����
	public int bx = -1, by = -1, bz = -1;
	public int nx = -1, ny = -1, nz = -1;
	
	public ERC_MessageSaveBreakRailStC(){}
	
	public ERC_MessageSaveBreakRailStC(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
	}

	public ERC_MessageSaveBreakRailStC(IRail prev, IRail next)
	{
		IRailController controller;
		if(prev != null)
		{
			controller = prev.GetController();
			this.bx = controller.x();
			this.by = controller.y();
			this.bz = controller.z();
		}
		if(next != null)
		{
			controller = next.GetController();
			this.nx = controller.x();
			this.ny = controller.y();
			this.nz = controller.z();
		}
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
    public IMessage onMessage(ERC_MessageSaveBreakRailStC message, MessageContext ctx)
	{
		AutoRailConnectionManager.SetPrevRailPosConnectedDestroyBlock(message.bx, message.by, message.bz);
		AutoRailConnectionManager.SetNextRailPosConnectedDestroyBlock(message.nx, message.ny, message.nz);
        return null;
    }
    
}