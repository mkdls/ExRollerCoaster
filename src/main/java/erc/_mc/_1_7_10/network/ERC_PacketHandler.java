package erc._mc._1_7_10.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import erc._mc._1_7_10._core.ERC_Core;
import erc._mc._1_7_10.gui.GUICoasterModelConstructor;

public class ERC_PacketHandler
{
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ERC_Core.MODID);
  
  	public static void init()
  	{
  		int i=0;
		INSTANCE.registerMessage(ERC_MessageRailGUICtS.class, ERC_MessageRailGUICtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncRailStC.class, MessageSyncRailStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(ERC_MessageConnectRailCtS.class, ERC_MessageConnectRailCtS.class, i++, Side.SERVER);
		//INSTANCE.registerMessage(ERC_MessageCoasterCtS.class, ERC_MessageCoasterCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncCoasterSettings.class, MessageSyncCoasterSettings.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(MessageSyncCoasterSettings.class, MessageSyncCoasterSettings.class, i++, Side.SERVER);
		INSTANCE.registerMessage(MessageSyncCoasterPosStC.class, MessageSyncCoasterPosStC.class, i++, Side.CLIENT);
		INSTANCE.registerMessage(ERC_MessageItemWrenchSync.class, ERC_MessageItemWrenchSync.class, i++, Side.SERVER);
		//INSTANCE.registerMessage(ERC_MessageCoasterMisc.class, ERC_MessageCoasterMisc.class, i++, Side.CLIENT);
		//INSTANCE.registerMessage(ERC_MessageCoasterMisc.class, ERC_MessageCoasterMisc.class, i++, Side.SERVER);
		INSTANCE.registerMessage(ERC_MessageRequestConnectCtS.class, ERC_MessageRequestConnectCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(ERC_MessageSpawnRequestWithCoasterOpCtS.class, ERC_MessageSpawnRequestWithCoasterOpCtS.class, i++, Side.SERVER);
		INSTANCE.registerMessage(GUICoasterModelConstructor.Message.class, GUICoasterModelConstructor.Message.class, i++, Side.SERVER);
  	}
}
