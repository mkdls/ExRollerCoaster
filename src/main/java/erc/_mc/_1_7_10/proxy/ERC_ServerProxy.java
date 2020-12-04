package erc._mc._1_7_10.proxy;

import erc._mc._1_7_10._core.ERC_Core;
import erc._mc._1_7_10.handler.CoasterCameraController;
import net.minecraft.client.Minecraft;

public class ERC_ServerProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return -1;
	}

	@Override
	public void preInit()
	{
		ERC_Core.coasterCameraController = new CoasterCameraController(Minecraft.getMinecraft());
//		FMLCommonHandler.instance().bus().register(ERC_Core.coasterCameraController);
	}

	@Override
	public void init() {}

	@Override
	public void postInit() {}
}
