package erc._mc.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import erc._mc.gui.GUIRail;
import erc.rail.IRail;
import erc._mc.tileentity.TileEntityRail;
import erc.rail.IRailController;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public class ERC_MessageRailGUICtS implements IMessage, IMessageHandler<ERC_MessageRailGUICtS, IMessage> {

	public int x, y, z;
	public int FLAG;
	public int MiscInt;

	public ERC_MessageRailGUICtS(){}

	public ERC_MessageRailGUICtS(IRailController controller, int flag, int imisc)
	{
		this(controller.x(), controller.y(), controller.z(), flag, imisc);
	}

	public ERC_MessageRailGUICtS(int x, int y, int z, int flag, int imisc)
	{
	    this.x = x;
	    this.y = y;
	    this.z = z;
	    this.FLAG = flag;
	    this.MiscInt = imisc;
//	    this.ControlPointNum = cpnum;
//	    this.smoothflag = smooth;
//	    this.pow = pow;
//	    this.rotflag = rotflag;
//	    this.rotration = rotratio;
//	    this.reset = reset;
  	}
	
	@Override
    public void fromBytes(ByteBuf buf)
    {
	    this.x = buf.readInt();
	    this.y = buf.readInt();
	    this.z = buf.readInt();
	    this.FLAG = buf.readInt();
	    this.MiscInt = buf.readInt();
    }
	
	@Override
    public void toBytes(ByteBuf buf)
    {
    	buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.FLAG);
        buf.writeInt(this.MiscInt);
    }
	
	@Override
    public IMessage onMessage(ERC_MessageRailGUICtS message, MessageContext ctx)
    {
    	TileEntity tile = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.x, message.y, message.z);
        if ((tile instanceof TileEntityRail))
        {
        	IRail rail = ((TileEntityRail) tile).getRail();
        	GUIRail.editFlag[] values = GUIRail.editFlag.values();
        	GUIRail.editFlag align = values[message.FLAG];
        	switch(align)
        	{
        	case CONTROLPOINT:
        		rail.AddPointNum(message.MiscInt); break;
        		
        	case SMOOTH:
        		rail.Smoothing();
        		break;
        		
        	case POW:
        		rail.AddPower(message.MiscInt); break;
        		
        	case ROTRED:
        	case ROTGREEN:
        	case ROTBLUE:
        		((TileEntityRail) tile).UpdateDirection(align, message.MiscInt); break;

        	case RESET:
        		rail.GetBasePoint().Reset(); break;
        		
        	case SPECIAL:
        		rail.SetSpecialData(message.MiscInt); break;
        		
        	case RailModelIndex:
				((TileEntityRail) tile).ChangeModel(message.MiscInt);
        		return null;
        	}
        	
        	rail.ConstructCurve();
        	rail.SyncData();

        	IRail prev = rail.GetPrevRail();
        	if(prev != null)
        	{
        		prev.SetNextPoint(rail.GetBasePoint());
                prev.ConstructCurve();
        		prev.SyncData();
        	}
        }
        return null;
    }
    
}
