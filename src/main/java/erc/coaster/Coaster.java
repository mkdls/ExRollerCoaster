package erc.coaster;

import erc.rail.IRail;
import mochisystems.math.Quaternion;
import mochisystems.math.Vec3d;

public class Coaster {

	public interface ICoasterController
	{
		IRail GetCurrentRail();
		void Destroy();
	}

	private final ICoasterController controller;
	public final CoasterPos pos;
    public final Vec3d position = new Vec3d();
    public final Vec3d prevPosition = new Vec3d();
//	public final Mat4 AttitudeMatrix = new Mat4();
//	public final Mat4 prevAttitudeMatrix = new Mat4();
	public final Quaternion attitude = new Quaternion();
	public final Quaternion prevAttitude = new Quaternion();

	protected double speed = 0;
	private CoasterSettings settings = CoasterSettings.Default();

	public IRail GetCurrentRail(){return pos.rail;}
	public void setSpeed(double speed){this.speed = speed;}
	public double getSpeed(){ return this.speed; }

    private Seat[] seats = new Seat[0];
	public Seat GetSeat(int index){return seats[index];}

//	/**
//	 * 連結されたコースターを表すリスト
//	 * 1つの連結コースターのグループで１インスタンスとすること
//	 */
	private Coaster rootCoaster;
	private Coaster parentCoaster;
	private Coaster childCoaster;


	public Coaster(ICoasterController controller)
	{
		this.controller = controller;
	    pos = new CoasterPos();
		rootCoaster = this;
		parentCoaster = null;
		childCoaster = null;
	}

	public void ConnectChild(Coaster child)
	{
		Coaster lastCoaster = getLastCoaster();
		lastCoaster.childCoaster = child;
		child.parentCoaster = lastCoaster;
		child.rootCoaster = this.rootCoaster;
	}

	private Coaster getLastCoaster()
	{
		return (childCoaster != null) ? childCoaster.getLastCoaster() : this;
	}

    public void SetNewRail(IRail rail)
    {
        pos.rail = rail;
    }

    public void SetSettingData(CoasterSettings settings)
    {
        this.settings = settings;
		UpdateSeatSettings();
    }

    public void UpdateSettings(String SettingString)
	{
		settings.FromString(SettingString);
		UpdateSeatSettings();
	}

	private void UpdateSeatSettings()
	{
		seats = new Seat[settings.Seats.length];
		for(int i = 0; i < seats.length; i++)
		{
			seats[i] = new Seat(this);
			seats[i].SetSetting(settings.Seats[i]);
		}
	}

    public CoasterSettings GetSettings()
    {
        return settings;
    }

    private long lastUpdatedTick = -1;
    /**
	 * コースター全体更新関数
	 * 連結された全てのコースターの中で最初に呼ばれたUpdateで
	 * 連結しているコースター全ての更新のmoveを行う。
	 * 最初かどうか判定するために利用する値はTickを想定している
	 */
	public void Update(long tick)
	{
		if(lastUpdatedTick == tick) return;
		_Update(rootCoaster, tick);
	}

	private static void _Update(Coaster root, long tick)
	{
		Coaster coaster = root;
		while(coaster != null)
		{
			coaster.lastUpdatedTick = tick;
			coaster.move();
			for(int i = 0; i < coaster.seats.length; ++i)
			{
				if(coaster.seats[i]!=null)coaster.seats[i].Update(tick);
			}
			coaster = coaster.childCoaster;
		}
	}

    public void setPosition(double t)
	{
		pos.t = t;
	}

	protected void move()
	{
		if(pos.hasNotRail())
		{
			IRail rail = controller.GetCurrentRail();
			if(rail == null) { Delete(); return; }
			SetNewRail(rail);
		}

        prevPosition.CopyFrom(position);
		prevAttitude.CopyFrom(attitude);
//		prevAttitudeMatrix.CopyFrom(AttitudeMatrix);

		speed = resist(speed);
		speed = accelerate(speed);
		speed = pos.move(speed);
		pos.rail.CalcAttitudeAt(pos.t, attitude, position);
		attitude.makeDirection();
//        coaster.AttitudeMatrix.makeBuffer();
	}

	private double resist(double speed)
	{
		return pos.rail.RegisterAt(pos.t, speed);
	}
	
	private double accelerate(double speed)
	{
		return speed + TotalAccel();
	}
	
	private double TotalAccel()
	{
        if(pos.hasNotRail()) return 0;
		double sum = 0;
		Coaster coaster = rootCoaster;
		while(coaster != null)
		{
			sum += coaster.pos.rail.AccelAt(coaster.pos.t) /  coaster.settings.Weight;
			coaster = coaster.childCoaster;
		}
		return sum;
	}
	
	public void Delete()
	{
		if(pos.hasNotRail()) return;
		pos.rail.OnDeleteCoaster();
		if(childCoaster != null && parentCoaster != null)
		{
			parentCoaster.childCoaster = childCoaster;
			childCoaster.parentCoaster = parentCoaster;
		}
		else if(childCoaster != null) childCoaster.parentCoaster = null;
		else if(parentCoaster != null) parentCoaster.childCoaster = null;
	}

	public void Destroy()
	{
		for(Seat seat : seats)seat.Destroy();
		if(GetCurrentRail()!=null) GetCurrentRail().OnDeleteCoaster();
	}

	public void ConnectTo(Coaster parent)
    {
        parent.childCoaster = this;
    }

	public class CoasterPos{
		public IRail rail;
		public double t = 0;

		public double t()
        {
            return t;
        }

        public boolean hasNotRail()
		{
			return rail == null;
		}
		
		public double move(double speed)
		{
			t += speed / rail.Length();

			// back and brake if next rail is nothing
			boolean isExistRail = PassOverRail();
			if(isExistRail)
			{
				return speed;
			}
			else
			{
				return speed * -0.1;
			}
		}
		
		public CoasterPos Clone(CoasterPos source)
		{
			this.rail = source.rail;
			this.t = source.t;
			return this;
		}
		
		void Shift(double shiftLength)
		{
			t += shiftLength;
			PassOverRail();
		}

		// @return : isExistRail
		private boolean PassOverRail()
		{
			IRail overRail;
			if(t >= 1.0) overRail = rail.NextRail();
			else if(t < 0) overRail = rail.PrevRail();
			else return true;

			if(overRail == null) {
                rail.GetController().FixConnection();
                if(t >= 1.0) overRail = rail.NextRail();
                else if(t < 0) overRail = rail.PrevRail();
                if (overRail == null)
				{
					t = 1;
					return false;
				}
			}

            rail.OnLeaveCoaster();
            overRail.OnEnterCoaster();

            t += (t < 0) ? 1.0 : -1.0;
            t *= rail.Length();
            t /= overRail.Length();
            rail = overRail;
            return true;
		}
	}
}
