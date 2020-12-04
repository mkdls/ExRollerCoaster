package erc.manager;

import erc.coaster.Seat;
import erc.rail.IRailController;
import mochisystems._mc._1_7_10._core.Logger;
import mochisystems.math.Vec3d;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import erc.rail.IRail;

import erc._mc._1_7_10._core.ERC_Logger;
import erc._mc._1_7_10.entity.EntityCoasterSeat;
import erc._mc._1_7_10.network.ERC_MessageConnectRailCtS;
import erc._mc._1_7_10.network.ERC_PacketHandler;
import net.minecraft.entity.player.EntityPlayer;

public class AutoRailConnectionManager {

	private static int savedPrevX = -1;
	private static int savedPrevY = -1;
	private static int savedPrevZ = -1;
	private static int savedNextX = -1;
	private static int savedNextY = -1;
	private static int savedNextZ = -1;

	public static TileEntityRail clickedTileForGUI;
	
	public AutoRailConnectionManager()
	{
		ResetData();
		clickedTileForGUI = null;
	}

	public static void MemoryOrConnect(int x, int y, int z)
	{
		if(!isSavedPrevRail()) {
			SetPrevRailPosConnectedDestroyBlock(x, y, z);
		}
		else{
			ERC_MessageConnectRailCtS packet
					= new ERC_MessageConnectRailCtS(savedPrevX, savedPrevY, savedPrevZ, x, y, z);
			ERC_PacketHandler.INSTANCE.sendToServer(packet);
			AutoRailConnectionManager.ResetData();
		}
	}

	public static void SetPrevRailPosConnectedDestroyBlock(int x, int y, int z)
	{
		savedPrevX = x;
		savedPrevY = y;
		savedPrevZ = z;
		Logger.debugInfo("save prev : x="+x);
	}
	public static void SetPrevRailPosConnectedDestroyBlock(IRail rail)
	{
		if(rail != null)
		{
			IRailController controller = rail.GetController();
			savedPrevX = controller.x();
			savedPrevY = controller.y();
			savedPrevZ = controller.z();
			Logger.debugInfo("save prev by break : x="+savedPrevX);
		}
		else {
			savedPrevX = savedPrevY = savedPrevZ = -1;
			Logger.debugInfo("reset prev by break");
		}
	}

	public static void SetNextRailPosConnectedDestroyBlock(int x, int y, int z)
	{
		savedNextX = x;
		savedNextY = y;
		savedNextZ = z;
		Logger.debugInfo("save next : x="+x);
	}
	public static void SetNextRailPosConnectedDestroyBlock(IRail rail)
	{
		if(rail != null)
		{
			IRailController controller = rail.GetController();
			savedNextX = controller.x();
			savedNextY = controller.y();
			savedNextZ = controller.z();
			Logger.debugInfo("save next by break : x="+savedPrevX);
		}
		else {
			savedNextX = savedNextY = savedNextZ = -1;
			Logger.debugInfo("reset next by break");
		}
	}

	public static void ResetData()
	{
		savedPrevX = -1;
		savedPrevY = -1;
		savedPrevZ = -1;
		savedNextX = -1;
		savedNextY = -1;
		savedNextZ = -1;
	}
	
	public static boolean isPlacedRail()
	{
		return isSavedPrevRail() || isSavedNextRail();
	}

	public static void ConnectToMemoriedPosition(int x, int y, int z)
	{
		if(AutoRailConnectionManager.isSavedPrevRail()){
			AutoRailConnectionManager.NotifyConnectPrevRail(x, y, z);
		}
		if(AutoRailConnectionManager.isSavedNextRail()) {
			AutoRailConnectionManager.NotifyConnectNextRail(x, y, z);
			ResetData();
		}
		else SetPrevRailPosConnectedDestroyBlock(x, y, z);

	}

	private static boolean isSavedPrevRail()
	{
		return savedPrevY >= 0;
	}
	private static boolean isSavedNextRail()
	{
		return savedNextY >= 0;
	}

	private static void NotifyConnectPrevRail(int x, int y, int z)
	{
		ERC_MessageConnectRailCtS packet
			= new ERC_MessageConnectRailCtS(
					savedPrevX, savedPrevY, savedPrevZ,
					x, y, z
					);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);
	}

	private static void NotifyConnectNextRail(int x, int y, int z)
	{
		ERC_MessageConnectRailCtS packet 
			= new ERC_MessageConnectRailCtS(
				x, y, z,
				savedNextX, savedNextY, savedNextZ
				);
		ERC_PacketHandler.INSTANCE.sendToServer(packet);

		ResetData();
	}
	
//	public static TileEntityRail GetPrevTileEntity(World world)
//	{
//		return ((TileEntityRail)world.getTileEntity(savedPrevX, savedPrevY, savedPrevZ));
//	}
//	public static TileEntityRail GetNextTileEntity(World world)
//	{
//		return ((TileEntityRail)world.getTileEntity(savedNextX, savedNextY, savedNextZ));
//	}
	
	public static void SaveRailForOpenGUI(TileEntityRail tl)
	{
		clickedTileForGUI = tl;
	}
	public static void CloseRailGUI()
	{
		clickedTileForGUI = null;
	}



    static Vec3d dir;
    static double speed;
    static EntityPlayer player;
    public static void GetOffAndButtobi(EntityPlayer Player)
    {
    	if(/*!Player.worldObj.isRemote &&*/ Player.isSneaking())
    	{
    		if(Player.ridingEntity instanceof EntityCoasterSeat)
    		{
    			Seat seat = ((EntityCoasterSeat)Player.ridingEntity).seat;
//    			dir = seat.GetParent().AttitudeMatrix.Dir();
    			player = Player;
    			speed = seat.GetParent().getSpeed();
    			//Player.motionX += seat.parent.Speed * dir.xCoord * 1;
    			//Player.motionY += seat.parent.Speed * dir.yCoord * 1;
    			//Player.motionZ += seat.parent.Speed * dir.zCoord * 1;
    			ERC_Logger.debugInfo("NotifyRailConnectionMgr : " + dir.toString());
    		}
    	}
    }
    public static void motionactive()
    {
    	player.motionX += speed * dir.x * 1;
    	player.motionY += speed * dir.y * 1;
		player.motionZ += speed * dir.z * 1;
    }
}

