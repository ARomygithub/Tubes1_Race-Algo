package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Result;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static za.co.entelect.challenge.Util.maxSpeedIfDamage;
import static za.co.entelect.challenge.Util.updateXiYi;

public class BoostCommand extends Command {
    public int xi=-1, yi=-1;

    @Override
    public String render() {
        return String.format("USE_BOOST");
    }

    public boolean run(Result res, int x, int y, int[][] truck, int end, Result[][] prefix, int[][]ctDamage, int[][] ctWall) {
        // cek stok powerups. Bila speed sudah max jangan gunakan boost
        if(res.ctBoost>0 && res.speed!=maxSpeedIfDamage[res.damage]) {
            int[] xiyi;
            int[][] trucki;
            resi = new Result(res);
            resi.ctBoost -=1;
            resi.boosting=true;
            resi.boostcounter=5;
            resi.speed = maxSpeedIfDamage[resi.damage];
            trucki = new int[2][2];
            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
            xi = min(x+resi.speed,end); yi = y;
            resi.xbonus = max(0,x+resi.speed-end);
            xiyi = updateXiYi(x,xi,yi,resi,trucki,prefix,ctDamage,ctWall);
            xi = xiyi[0]; yi = xiyi[1];
            resi.xr=xi; resi.yr=yi;
            return true;
        }
        return false;
    }
}
