package za.co.entelect.challenge;

import static java.lang.Math.*;

public class Util {
    public static final int[] maxSpeedIfDamage = {15,9,8,6,3,0};
    public static final int track_length = 1500;

    public static void updateBoostcounter(Result cur) {
        if(cur.boosting) {
            --cur.boostcounter;
            if(cur.boostcounter==0){
                cur.boosting=false;
                if(cur.speed==15) cur.speed=9;
            }
        }
    }

    public static int nextSpeed(int x) {
        if(x==0) {
            return 3;
        } else if(x==3) {
            return 6;
        } else if(x==5) {
            return 6;
        }else if(x==6) {
            return 8;
        } else if(x==8) {
            return 9;
        } else if(x==9) {
            return 9;
        } else {
            return x;
        }
    }

    public static int prevSpeed(int x) {
        if(x==0) {
            return 0;
        } else if(x==3) {
            return 0;
        } else if(x==5) {
            return 3;
        } else if(x==6) {
            return 3;
        } else if(x==8) {
            return 6;
        } else if(x==9) {
            return 8;
        } else if(x==15) {
            return 9;
        } else {
            return x;
        }
    }

    public static int[] updateXiYi(int x, int xi, int yi, Result resi, int[][] trucki, Result[][] prefix, int[][] ctDamage, int[][] ctWall) {
        // tambah temp ke resi (powerups).
        // damage dari ctWall, ctDamage, boosting, boostcounter juga.
        boolean hitTruck=false;
        if(yi==trucki[0][1] && x<trucki[0][0] && trucki[0][0]<=xi) {
//            System.out.printf("Tes trucki[0] di update%n");
//            System.out.printf("%d x %d xi %d yi%n", x, xi, yi);
            hitTruck = true;
            xi = trucki[0][0]-1;
            trucki[0][0] = -1; trucki[0][1] = -1;
        } else if(yi==trucki[1][1] && x<trucki[1][0] && trucki[1][0]<=xi) {
            hitTruck = true;
            xi = trucki[1][0] -1;
            trucki[1][0] = -1; trucki[1][1] = -1;
        }
        Result temp = new Result(prefix[yi][xi]);
        if(x>=0) {
            temp.minus(prefix[yi][x]);
        }
        resi.add(temp);
        int curDamage= ctDamage[yi][xi];
        int curWall = ctWall[yi][xi];
        if(x>=0) {
            curDamage -= ctDamage[yi][x];
            curWall -= ctWall[yi][x];
        }
        if(hitTruck) {
            curDamage +=2;
            curWall +=1;
            resi.xbonus = 0;
            if(xi==x) {
                curDamage += ctDamage[yi][x]-ctDamage[yi][x-1];
                curWall += ctWall[yi][x]-ctWall[yi][x-1];
                temp.add(prefix[yi][x]);
                temp.minus(prefix[yi][x-1]);
                resi.add(temp);
            }
        }
        if(curDamage>0) {
            if(curWall>0) {
                resi.speed = min(3,resi.speed);
            } else {
                resi.speed = max(3,prevSpeed(resi.speed));
            }
            resi.damage = min(5,resi.damage+curDamage);
            resi.speed = min(resi.speed, maxSpeedIfDamage[resi.damage]);
            if(resi.boosting) {
                resi.boosting = false;
                resi.boostcounter = 0;
            }
        }
        return new int[] {xi,yi};
    }
}
