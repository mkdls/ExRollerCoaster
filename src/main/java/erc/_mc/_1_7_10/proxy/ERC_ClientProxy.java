package erc._mc._1_7_10.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import erc._mc._1_7_10._core.ERC_Core;
import erc._mc._1_7_10.renderer.*;
import erc._mc._1_7_10.tileentity.TileEntityCoasterModelConstructor;
import erc._mc._1_7_10.tileentity.TileEntityRailModelConstructor;
import erc._mc._1_7_10.entity.EntityCoaster;
import erc._mc._1_7_10.entity.EntityCoasterSeat;
import erc._mc._1_7_10.entity.entitySUSHI;
import erc._mc._1_7_10.handler.CoasterCameraController;
import erc._mc._1_7_10.handler.ERC_InputEventHandler;
import erc._mc._1_7_10.handler.ERC_RenderEventHandler;
import mochisystems._mc._1_7_10.renderer.renderTileEntityLimitFrame;
import erc.loader.ModelPackLoader;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import mochisystems.manager.RollingSeatManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ERC_ClientProxy implements IProxy{
	
	@Override
	public int getNewRenderType()
	{
		return RenderingRegistry.getNextAvailableRenderId();
	}
	

	@Override
	public void preInit()
	{
//		FMLClientHandler.instance().addModAsResource( (new customResourceLoader() ).get());
		
		// �f�t�H�R�[�X�^�[�o�^
//		String defaultModel = ERC_Core.MODID+":models/coaster.obj";
//		String defaultModel_c = ERC_Core.MODID+":models/coaster_connect.obj";
//		String defaultTex = ERC_Core.MODID+":textures/gui/gui.png";
//		String defaultIcon_c = "erc:coaster_c";
//		String defaultIcon = "erc:coaster";
		
//		CoasterData main = new CoasterData(defaultModel, defaultTex, defaultIcon);
//		CoasterData connect = new CoasterData(defaultModel_c, defaultTex, defaultIcon_c);
//		CoasterData mono = new CoasterData(defaultModel, defaultTex, defaultIcon);
//		CoasterData inv = new CoasterData(ERC_Core.MODID+":models/coaster_inv.obj", defaultTex, "erc:coaster_inv");
//		main.setSeatOffset(0, 0f, 0.4f, 0f);
//		connect.setSeatOffset(0, 0f, 0.4f, 0f);
//		mono.setSeatOffset(0, 0f, 0.4f, 0f);
//
//		main.setCoasterMainData(1.5f, 2.2f, 2.0f, true);
//		connect.setCoasterMainData(1.5f, 1.0f, 3.0f, true);
//		mono.setCoasterMainData(1.5f, 2.2f, 2.0f, true);
//
//		main.setSeatSize(0, 0.8f);
//		connect.setSeatSize(0, 0.8f);
//		mono.setSeatSize(0, 0.8f);
//
//		ModelPackLoader.registerCoaster(main);
//		ModelPackLoader.registerConnectionCoaster(connect);
//		ModelPackLoader.registerMonoCoaster(mono);
		
//		inv.setCoasterMainData(1.6f, 1.0f, 2.0f, true);
//		inv.setSeatOffset(0, 0, 2.0f, 0.6f);
//		inv.setSeatRotation(0, 0, 0f, (float)Math.PI);
//		ModelPackLoader.registerCoaster(inv);
//		ModelPackLoader.registerConnectionCoaster(inv);
//
//		///// ����
//		CoasterData head = new CoasterData("erc:models/coaster_d.obj", defaultTex, "erc:coaster_d");
//		CoasterData sub = new CoasterData("erc:models/coaster_d_c.obj", defaultTex, "erc:coaster_dc");
//		head.setSeatNum(2);
//		head.setCoasterMainData(1.9f, 3f, 3f, true);
//		head.setSeatSize(0, 0.9f);
//		head.setSeatSize(1, 0.9f);
//		head.setSeatOffset(0, -0.5f, 0.5f, 0);
//		head.setSeatOffset(1, 0.5f, 0.5f, 0);
//		ModelPackLoader.registerCoaster(head);
//
//		sub.setSeatNum(2);
//		sub.setCoasterMainData(1.9f, 3f, 3f, true);
//		sub.setSeatSize(0, 0.9f);
//		sub.setSeatSize(1, 0.9f);
//		sub.setSeatOffset(0, -0.5f, 0.5f, 0);
//		sub.setSeatOffset(1, 0.5f, 0.5f, 0);
//		ModelPackLoader.registerConnectionCoaster(sub);
		

		// TileEntityRender�̓o�^
		RenderTileEntityRailBase tileRenderer = new RenderTileEntityRailBase();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Accel.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Const.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Detector.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Branch.class,tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.Invisible.class, tileRenderer);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRail.AntiGravity.class, tileRenderer);

		renderTileEntityLimitFrame rendererFrame = new renderTileEntityLimitFrame();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRailModelConstructor.class, rendererFrame);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoasterModelConstructor.class, new RendererCoasterModelConstructor());


		// Entity�`��o�^
		RenderEntityCoaster renderer = new RenderEntityCoaster();
		RenderingRegistry.registerEntityRenderingHandler(EntityCoaster.class, renderer);
		RenderingRegistry.registerEntityRenderingHandler(EntityCoasterSeat.class, new renderEntityCoasterSeat());
		RenderingRegistry.registerEntityRenderingHandler(entitySUSHI.class, new renderEntitySUSIHI());
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterMonodentate.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterDoubleSeat.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterConnector.class, renderer);


		// �u���b�N�J�X�^�������_�[�̓o�^
		RenderingRegistry.registerBlockHandler(new renderBlockRail());
		
		// Handler�̓o�^
		Minecraft mc = Minecraft.getMinecraft();
		ERC_Core.coasterCameraController = new CoasterCameraController(mc);

//		FMLCommonHandler.instance().bus().register(new TickEventHandler());
//		FMLCommonHandler.instance().bus().register(ERC_Core.coasterCameraController);
		MinecraftForge.EVENT_BUS.register(new ERC_InputEventHandler(mc));
		MinecraftForge.EVENT_BUS.register(new ERC_RenderEventHandler());
		
		// �X�V�̃��f���Ƃ�
		entitySUSHI.clientInitSUSHI();
	}

	@Override
	public void init() 
	{
		RollingSeatManager.Init();
	}

	@Override
	public void postInit() 
	{
		ModelPackLoader.Load();
	}
}