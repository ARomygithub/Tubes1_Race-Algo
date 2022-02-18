package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Result;

import static java.lang.Math.max;

public class FixCommand extends Command {
    public int xi=-1, yi=-1;

    @Override
    public String render() {
        return String.format("FIX");
    }

    public boolean run(Result res, int x, int y, int[][] truck, int end, Result[][] prefix, int[][]ctDamage, int[][] ctWall) {
        int[] xiyi;
        int[][] trucki;
        // Bila damage=0, Fix tidak dipertimbangkan
        if(res.damage>0) {
            resi = new Result(res);
            xi =x; yi=y;
            resi.damage = max(0,resi.damage-2);
            trucki = new int[2][2];
            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
            resi.xr=xi; resi.yr=yi;
            return true;
        }
        return false;
    }
}
