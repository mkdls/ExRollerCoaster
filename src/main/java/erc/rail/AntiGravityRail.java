package erc.rail;


import erc.rail.Rail;

public class AntiGravityRail extends Rail {

    @Override
    public double AccelAt(double pos)
    {
        return 0;
    }

    @Override
    public double RegisterAt(double pos, double speed)
    {
        return speed;
    }
}
