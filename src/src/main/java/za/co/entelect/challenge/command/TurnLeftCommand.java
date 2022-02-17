package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Result;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static za.co.entelect.challenge.Util.updateXiYi;

public class TurnLeftCommand extends Command {
    public int xi=-1, yi=-1;

    @Override
    public String render() {return "TURN_LEFT";}

    public TurnLeftCommand() {
        resi = new Result();
    }

    public boolean run(Result res, int x, int y, int[][] truck, int end, Result[][] prefix, int[][]ctDamage, int[][] ctWall) {
        if(y>0 && res.speed>0) {
            int[] xiyi;
            int[][] trucki;
            resi = new Result(res);
            trucki = new int[2][2];
            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
            xi = min(x+resi.speed-1,end); yi = y-1;
            resi.xbonus = max(0,x+resi.speed-1-end);
            xiyi = updateXiYi(x-1,xi,yi,resi,trucki,prefix,ctDamage,ctWall);
            xi = xiyi[0]; yi = xiyi[1];
            resi.xr=xi; resi.yr=yi;
            return true;
        }
        return false;
    }
}
