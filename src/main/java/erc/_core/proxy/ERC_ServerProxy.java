package erc._core.proxy;

import cpw.mods.fml.common.FMLCommonHandler;
import erc._core.ERC_Core;
import erc.handler.CoasterCameraController;
import mochisystems.handler.TickEventHandler;
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
