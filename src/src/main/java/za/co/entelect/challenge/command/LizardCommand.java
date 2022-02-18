package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Result;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.enums.Terrain;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static za.co.entelect.challenge.Util.maxSpeedIfDamage;
import static za.co.entelect.challenge.Util.prevSpeed;

public class LizardCommand extends Command {
    public int xi=-1, yi=-1;

    @Override
    public String render() {return "USE_LIZARD";}

    public boolean run(Result res, int x, int y, int[][] truck, int end, Result[][] prefix, int[][]ctDamage, int[][] ctWall, GameState gameState) {
        int[][] trucki;
        // cek stok Lizard
        if(res.ctLizard>0) {
            resi = new Result(res);
            --resi.ctLizard;
            trucki = new int[2][2];
            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
            xi = min(x+resi.speed,end); yi = y;
            resi.xbonus = max(0,x+resi.speed-end);
            if(xi!=x && xi==x+resi.speed) {
                int curDamage=0; int curWall=0;
                if(yi==trucki[0][1] && xi==trucki[0][0]) {
                    --xi;
                    ++curWall;
                    curDamage +=2;
                    trucki[0][0] = -1; trucki[0][1] = -1;
                }
                if(yi==trucki[1][1] && xi==trucki[1][0]) {
                    --xi;
                    ++curWall;
                    curDamage +=2;
                    trucki[1][0] = -1; trucki[1][1] = -1;
                }
                Terrain curTerrain = gameState.lanes.get(yi)[xi].terrain;
                if(curTerrain==Terrain.MUD) {
                    ++curDamage;
                } else if(curTerrain==Terrain.OIL_SPILL) {
                    ++curDamage;
                } else if(curTerrain==Terrain.OIL_POWER) {
                    ++resi.ctOil;
                } else if(curTerrain==Terrain.BOOST) {
                    ++resi.ctBoost;
                } else if(curTerrain==Terrain.WALL) {
                    curDamage +=2;
                    ++curWall;
                } else if(curTerrain==Terrain.LIZARD) {
                    ++resi.ctLizard;
                } else if(curTerrain==Terrain.TWEET) {
                    ++resi.ctTweet;
                } else if(curTerrain==Terrain.EMP) {
                    ++resi.ctEmp;
                }
                if(curDamage>0) {
                    if(curWall>0) {
                        resi.speed = min(3,resi.speed);
                    } else {
                        resi.speed = max(3,prevSpeed(resi.speed));
                    }
                    resi.damage = min(5,resi.damage+curDamage);
                    resi.speed = min(resi.speed,maxSpeedIfDamage[resi.damage]);
                    if(resi.boosting) {
                        resi.boosting=false;
                        resi.boostcounter=0;
                    }
                }
            }
            resi.xr=xi; resi.yr=yi;
            return true;
        }
        return false;
    }
}
