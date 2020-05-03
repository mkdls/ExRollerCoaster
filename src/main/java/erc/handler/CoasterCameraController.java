package erc.handler;

import erc._mc.entity.EntityCoasterSeat;
import erc.coaster.Coaster;
import erc._mc.entity.EntityCoaster;
import mochisystems.handler.TickEventHandler;
import mochisystems.hook.OrientCameraHooker;
import mochisystems.manager.RollingSeatManager;
import org.lwjgl.opengl.Display;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public class CoasterCameraController {
	
	private Minecraft mc;

	public CoasterCameraController(Minecraft minecraft)
	{
	    mc = minecraft;
	    TickEventHandler.AddRenderTickPreListener(this::onRenderTickPre);
	}


	private void onRenderTickPre(float partialTicks)
	{
		if (Minecraft.getMinecraft().isGamePaused()) return;

	    EntityPlayer player = this.mc.thePlayer;
	    if (player == null) return;

		EntityCoasterSeat riddenSeat = null;
	    if (player.ridingEntity instanceof EntityCoasterSeat)
	    {
			riddenSeat = ((EntityCoasterSeat) player.ridingEntity);
	    }

	    if (riddenSeat == null || riddenSeat.seat == null)
		{
			RollingSeatManager.ResetAngles();
			return;
		}

		if (mc.inGameHasFocus && Display.isActive())
		{
			mc.mouseHelper.mouseXYChange();
			float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f2 = f1 * f1 * f1 * 8.0F;
			float f3 = (float)mc.mouseHelper.deltaX * f2;
			float f4 = (float)mc.mouseHelper.deltaY * f2;

			if (this.mc.gameSettings.invertMouse)
			{
				f4 *= -1;
			}

			RollingSeatManager.ShakeHeadOnRollingSeat(f3, f4);
			RollingSeatManager.SetAttitude(riddenSeat.seat.GetParent().attitude, riddenSeat.seat.GetParent().prevAttitude);

			mc.mouseHelper.deltaX = 0;
			mc.mouseHelper.deltaY = 0;
	    }
	}
	  
//	private void onRenderTickPost(float partialTicks) { }


//	private void onPlayerTickPre(EntityPlayer player) {}
//	private void onPlayerTickPost(EntityPlayer player) {}
// 	private void onTickPre() { }
//	private void onTickPost() { }
	  
	  
//	@SubscribeEvent
//	public void onPlayerTickEvent(TickEvent.PlayerTickEvent event)
//	{
//		if (event.phase == TickEvent.Phase.START) {
//			onPlayerTickPre(event.player);
//		}
//		if (event.phase == TickEvent.Phase.END) {
//			onPlayerTickPost(event.player);
//		}
//	}
//
//	@SubscribeEvent
//	public void onClientTickEvent(TickEvent.ClientTickEvent event)
//	{
//		if (event.phase == TickEvent.Phase.START) {
//			onTickPre();
//		}
//		if (event.phase == TickEvent.Phase.END) {
//			onTickPost();
//		}
//	}
	
//	@SubscribeEvent
//	public void onRenderTickEvent(TickEvent.RenderTickEvent event)
//	{
//		if (event.phase == TickEvent.Phase.START) {
//			onRenderTickPre(event.renderTickTime);
//		}
//		if (event.phase == TickEvent.Phase.END) {
//			onRenderTickPost(event.renderTickTime);
//		}
//	}
	
}
