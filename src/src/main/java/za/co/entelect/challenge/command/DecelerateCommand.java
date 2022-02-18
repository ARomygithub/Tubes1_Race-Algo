package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Result;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static za.co.entelect.challenge.Bot.ctLane;
import static za.co.entelect.challenge.Util.*;

public class DecelerateCommand extends Command {
    public int xi=-1, yi=-1;

    @Override
    public String render() {
        return String.format("DECELERATE");
    }

    public boolean run(Result res, int x, int y, int[][] truck, int end, Result[][] prefix, int[][]ctDamage, int[][] ctWall) {
        int[] xiyi;
        int[][] trucki;
        if(res.speed>3) {
            resi = new Result(res);
            resi.speed = min(prevSpeed(resi.speed),maxSpeedIfDamage[resi.damage]);
            trucki = new int[2][2];
            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
            xi = min(x+resi.speed,end); yi=y;
            boolean nextLaneBetter = false;
            if(yi+1<ctLane) {
                if(ctDamage[yi+1][end]-ctDamage[yi+1][xi-1]==0) {
                    nextLaneBetter = true;
                }
            }
            if(yi-1>=0) {
                if(ctDamage[yi-1][end]-ctDamage[yi-1][xi-1]==0) {
                    nextLaneBetter = true;
                }
            }
            if(!nextLaneBetter) {
                return false;
            }
            resi.xbonus = max(0,x+resi.speed-end);
            xiyi = updateXiYi(x, xi, yi, resi, trucki, prefix, ctDamage, ctWall);
            xi = xiyi[0]; yi = xiyi[1];
            resi.xr=xi; resi.yr=yi;
            return true;
        }
        return false;
    }
}
