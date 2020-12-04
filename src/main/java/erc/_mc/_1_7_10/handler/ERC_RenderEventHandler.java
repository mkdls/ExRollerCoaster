package erc._mc._1_7_10.handler;

import mochisystems.math.Quaternion;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._mc._1_7_10._core.ERC_Logger;
import erc._mc._1_7_10.entity.EntityCoasterSeat;
import erc.sound.ERCMovingSound;
import erc.sound.ERCMovingSoundRiding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

@SideOnly(Side.CLIENT)
public class ERC_RenderEventHandler {
	

	EntityCoasterSeat getCoaster(EntityLivingBase target)
	{
		if (!target.isRiding()) {
			return null;
		}
		if (!(target.ridingEntity instanceof EntityCoasterSeat)) {
			return null;
		}
		return (EntityCoasterSeat)target.ridingEntity;
	}
	 
	@SideOnly(Side.CLIENT)
  	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void renderPre(RenderLivingEvent.Pre event)
	{
//		MeshCache.instance.Buffer();
//		MeshCache.instance.Render();

		if (event.isCanceled()) {
			return;
		}
		EntityCoasterSeat entitySeat;
		if ((entitySeat = getCoaster(event.entity)) == null) {
			return;
		}
		GL11.glPushMatrix();
	    
		Timer timer = (Timer)ReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), new String[] { "field_71428_T", "timer" });
	    float partialTicks = timer.renderPartialTicks;
	    
//	    float yaw = entitySeat.parent.ERCPosMat.getFixedYaw(partialTicks);
//	    float pitch = entitySeat.parent.ERCPosMat.getFixedPitch(partialTicks);
//	    float roll = entitySeat.parent.ERCPosMat.getFixedRoll(partialTicks) + (float)Math.toDegrees(entitySeat.getRotZ());
		Quaternion rotation = entitySeat.seat.GetParent().attitude;

//        event.entity.renderYawOffset = yaw;
//        event.entity.rotationYawHead = yaw;
	    Entity theplayer = Minecraft.getMinecraft().thePlayer;
	    Entity e = event.entity;
	    double x = theplayer.prevPosX+(theplayer.posX-theplayer.prevPosX)*partialTicks - (e.prevPosX+(e.posX-e.prevPosX)*partialTicks);
	    double y = theplayer.prevPosY+(theplayer.posY-theplayer.prevPosY)*partialTicks - (e.prevPosY+(e.posY-e.prevPosY)*partialTicks);
	    double z = theplayer.prevPosZ+(theplayer.posZ-theplayer.prevPosZ)*partialTicks - (e.prevPosZ+(e.posZ-e.prevPosZ)*partialTicks);
//	    double CorePosX = event.entity.prevPosX+(event.entity.posX-event.entity.prevPosX)*partialTicks;
//	    double y = event.entity.prevPosY+(event.entity.posY-event.entity.prevPosY)*partialTicks;
//	    double z = event.entity.prevPosZ+(event.entity.posZ-event.entity.prevPosZ)*partialTicks;
	    GL11.glTranslated(-x,-y,-z);
//	    GL11.glRotatef(yaw, 0.0F, -1.0F, 0.0F);
//	    GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
//	    GL11.glRotatef(roll, 0.0F, 0.0F, 1.0F);
//	    GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
		GL11.glMultMatrix(rotation.GetBuffer());
	    GL11.glTranslated(x,y,z);
	}
	  
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void renderPost(RenderLivingEvent.Post event)
	{
		@SuppressWarnings("unused")
		EntityCoasterSeat coaster;
		if ((coaster = getCoaster(event.entity)) == null) {
			return;
		}
		GL11.glPopMatrix();
	}

	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void ridingErc(LivingEvent.LivingUpdateEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
	    if (!(event.entity instanceof EntityClientPlayerMP)) return;
	    
	    EntityClientPlayerMP player = (EntityClientPlayerMP)event.entity;
	    
	    String key = "RideERC";
	    if (player.getEntityData().hasKey(key))
	    {
	    	if ((player.ridingEntity == null) || (!(player.ridingEntity instanceof EntityCoasterSeat)))
	    	{
	    		player.getEntityData().removeTag(key);
	    	}
	    }
	    else if ((player.ridingEntity != null) && ((player.ridingEntity instanceof EntityCoasterSeat)))
	    {
	    	mc.getSoundHandler().playSound(new ERCMovingSoundRiding(player, (EntityCoasterSeat)player.ridingEntity));
	    	mc.getSoundHandler().playSound(new ERCMovingSound(player, (EntityCoasterSeat)player.ridingEntity));
	    	player.getEntityData().setBoolean(key, true);
	    	ERC_Logger.debugInfo("sound update");
	    }
	}
}
