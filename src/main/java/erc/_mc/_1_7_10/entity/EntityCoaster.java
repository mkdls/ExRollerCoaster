package erc._mc._1_7_10.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import erc._mc._1_7_10.tileentity.TileEntityRail;
import erc.coaster.Coaster;
import erc.coaster.CoasterSettings;
import erc.loader.ModelPackLoader;
import erc.model.CoasterMeshModel;
import erc.rail.IRail;
import mochisystems._mc._1_7_10._core.Logger;
import mochisystems.util.IModel;
import mochisystems.blockcopier.IModelCollider;
import mochisystems._mc._1_7_10.world.MTYBlockAccess;
import mochisystems.math.Vec3d;
import mochisystems.util.IModelController;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;

import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;


public class EntityCoaster extends Entity implements IModelController, Coaster.ICoasterController {

	private final Coaster coaster;

	public IModel model;

	public Coaster GetCoaster(){ return coaster; }

	public EntityCoaster(World worldIn)
	{
		super(worldIn);
		coaster = new Coaster(this);
        ySize = 0;
	}

	public EntityCoaster(World world, CoasterSettings settings, double x, double y, double z)
	{
		super(world);
		this.coaster = new Coaster(this);
		setLocationAndAngles(x, y, z, 0, 0);
		dataWatcher.updateObject(dwSettings, settings.toString());
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		if(model!=null) model.SetWorld(world);
	}
//    public void ActivateSetting()
//    {
//        ActivateSetting(settings, worldObj);
//    }

//    public void ActivateSetting(CoasterSettings settings, World world)
//	{
//        this.height = 0.01f;
//        this.width = 1.0f;
//		MakeSeats(settings, world);
//	}

    public void SpawnSeatEntity()
    {
		if(worldObj.isRemote)
		{
			throw new RuntimeException("Development Bug! Must not call this function on client side.");
		}

		CoasterSettings settings = coaster.GetSettings();
		int seatNum = settings.Seats.length;
		for (int i = 0; i < seatNum; ++i) {
			EntityCoasterSeat entitySeat = new EntityCoasterSeat(worldObj, this);
//			entitySeat.setCoasterSettings(settings, i);
			coaster.GetSeat(i).SetController(entitySeat);
			entitySeat.setLocationAndAngles(posX, posY, posZ, 0, 0);
			worldObj.spawnEntityInWorld(entitySeat);
		}
    }

    void SetSeat(EntityCoasterSeat seat, int index)
	{
		coaster.GetSeat(index).SetController(seat);
	}

	@Override
	protected boolean canTriggerWalking()
    {
        return false;
    }

	@Override
	protected void entityInit()
    {
		this.dataWatcher.addObject(dwSettings, "");
		this.dataWatcher.addObject(dwPosX, 0);
		this.dataWatcher.addObject(dwPosY, 0);
		this.dataWatcher.addObject(dwPosZ, 0);
	}

//	private static final DataParameter<Float> PosT = EntityDataManager.<Float>createKey(EntityMinecart.class, DataSerializers.FLOAT);

	private final int dwSettings = 19;
	private final int dwPosX = 16;
	private final int dwPosY = 17;
    private final int dwPosZ = 18;
//    private final int dwSeatId = 25;


	@Override
	public AxisAlignedBB getCollisionBox(Entity entity)
    {
    	return null; // 当たり判定は椅子のみとする
    }

	@Override
	public AxisAlignedBB getBoundingBox()
    {
        return null;
    }
	
	@Override
	public boolean canBePushed()
    {
        return false;
    }

	@Override
	public boolean canBeCollidedWith()
	{
		return false;
	}

	@Override
    public void setDead()
    {
        super.setDead();
        coaster.Destroy();
		if(model != null) model.Invalidate();
    }

	@Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
		if (!getWorld().isRemote && !this.isDead)
		{
			boolean canDamage =
					source.getSourceOfDamage() instanceof EntityPlayer
					&& ((EntityPlayer)source.getSourceOfDamage()).capabilities.isCreativeMode;
			
			if (canDamage)// || this.getDamage() > 40.0F)
			{
//				this.removePassengers();
				coaster.Delete();
				this.setDead();
			}
    	}
		return true;
    }
    @Override
    public void onUpdate()
    {
		super.onUpdate();
        if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)){setDead();return;}
		syncToClient();

		coaster.Update(getWorld().getWorldTime());
		Vec3d pos = coaster.position;
        this.setPosition(pos.x, pos.y, pos.z);

        model.Update();
    }

	// railpos.tとSettingsの同期
	protected void syncToClient()
	{
		if(!worldObj.isRemote && coaster.GetCurrentRail() != null)
		{
			IRail rail = coaster.GetCurrentRail();
			dataWatcher.updateObject(dwPosX, rail.GetController().x());
			dataWatcher.updateObject(dwPosY, rail.GetController().y());
			dataWatcher.updateObject(dwPosZ, rail.GetController().z());
		}
        else if(worldObj.isRemote && coaster.GetCurrentRail() == null)
        {
            coaster.UpdateSettings(dataWatcher.getWatchableObjectString(dwSettings));
            TileEntity tile = worldObj.getTileEntity(
                    dataWatcher.getWatchableObjectInt(dwPosX),
                    dataWatcher.getWatchableObjectInt(dwPosY),
                    dataWatcher.getWatchableObjectInt(dwPosZ));
            if(tile instanceof TileEntityRail) coaster.SetNewRail(((TileEntityRail)tile).getRail());
//            ActivateSetting();
        }
		if(model == null)
		{
			CoasterSettings pack = ModelPackLoader.GetHeadCoasterSettings("__default");
			model = new CoasterMeshModel(pack.Model, pack.Texture, pack.ModelID);
		}
//        if(this.UpdatePacketCounter-- <= 0) {
//            UpdatePacketCounter = 50;
//			if(!getWorld().isRemote && coaster.pos.rail != null)
//			{
//                MessageSyncCoasterPosStC packet =
//                        new MessageSyncCoasterPosStC(this);
//				ERC_PacketHandler.INSTANCE.sendToAll(packet);
//			}
//        }
	}

	@Override
	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		double f = Math.max(width, height) * 0.5 * 1.73205;
		this.boundingBox.setBounds(x - f, y - f, z - f,
								   x + f, y + f, z + f);
	}

	@Override
	public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch)
	{
		Logger.debugInfo("EntityCoaster ; call setLocationAndAngles");
		super.setLocationAndAngles(x, y, z, yaw, pitch);
		if(coaster != null)coaster.position.SetFrom(x, y, z);
	}

    @Override
    public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    {
        //nothing　サーバーからの規定のEntity同期で使われており、同期を無効にするため
    }

	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender(float p_70070_1_)
	{
		int i = MathHelper.floor_double(this.posX);
		int j = MathHelper.floor_double(this.posZ);
		int k = MathHelper.floor_double(this.posY);
		return this.getWorld().getLightBrightnessForSkyBlocks(i, k, j, 0);
	}

	@Override
	public boolean isInRangeToRenderDist(double p_70112_1_)
	{
		return true;
	}


	private int currentRailX;
	private int currentRailY;
	private int currentRailZ;
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		currentRailX = nbt.getInteger("railx");
		currentRailY = nbt.getInteger("raily");
		currentRailZ = nbt.getInteger("railz");
        coaster.UpdateSettings(nbt.getString("settings"));
        Logger.debugInfo("read from nbt : seat num="+coaster.GetSettings().Seats.length);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
	    if(coaster.GetCurrentRail() != null) {
			currentRailX = coaster.GetCurrentRail().GetController().x();
			currentRailY = coaster.GetCurrentRail().GetController().y();
			currentRailZ = coaster.GetCurrentRail().GetController().z();
			nbt.setInteger("railx", currentRailX);
			nbt.setInteger("raily", currentRailY);
			nbt.setInteger("railz", currentRailZ);
        }
        nbt.setString("settings", coaster.GetSettings().toString());
		Logger.debugInfo("write to nbt : localx="+ coaster.GetSettings().Seats[0].LocalPosition.x);
	}


	public World getWorld()
	{
		return worldObj;
	}

	///////////////////// ICoasterController
	@Override
	public IRail GetCurrentRail()
	{
		TileEntityRail tile = (TileEntityRail)World().getTileEntity(currentRailX, currentRailY, currentRailZ);
		if(tile == null) return null;
		return tile.getRail();
	}

	@Override
	public void Destroy()
	{
		setDead();
	}


	////////////////////// IModelController

	void ChangeModel(IModel model, NBTTagCompound nbtData)
	{
		this.model = model;
		this.model.Reset();
		this.model.SetWorld(World());
		this.model.readFromNBT(nbtData);
	}

	@Override
	public int CorePosX() {
		return (int)posX;
	}

	@Override
	public int CorePosY() {
		return (int)posY;
	}

	@Override
	public int CorePosZ() {
		return (int)posZ;
	}

	@Override
	public int CoreSide() {
		return 2;
	}

	@Override
	public boolean IsInvalid() {
		return isDead;
	}

	@Override
	public boolean IsRemote() {
		return getWorld().isRemote;
	}

	@Override
	public void markBlockForUpdate() {

	}

	@Override
	public World World() {
		return getWorld();
	}

	@Override
	public IModel GetBlockModel(int x, int y, int z) {
		return null;
	}

	@Override
	public IModelCollider MakeAndSpawnCollider(IModel parent, MTYBlockAccess blockAccess) {
		return null;
	}
}
