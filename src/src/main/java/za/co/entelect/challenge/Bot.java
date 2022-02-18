package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.*;
import static za.co.entelect.challenge.Util.*;

import java.util.*;

import static java.lang.Math.*;

public class Bot {

//    private static final int maxSpeed = 9;
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    public GameState gameState;
//    private Car opponent;
    private Car myCar;
//    private final static Command FIX = new FixCommand();
    private Result bestRes = new Result();
    private String bestCom;
    private Result[][] prefix;
    private int[][] ctWall;
    private int[][] ctDamage;
    private int[] ctBad;
    private int start, end, best;
    private boolean useLiz;
    public static int ctLane;
    private int[][] truck = new int[][] {{-1,-1}, {-1,-1}};
    final private int track_length = 600;
//    private int ctDebug=0;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
        this.myCar = gameState.player;
//        this.opponent = gameState.opponent;
        this.ctLane = gameState.lanes.size();
        int blockLength = gameState.lanes.get(0).length;
        for(int i=0;i<ctLane;i++) {
            for(int j=0;j<blockLength;j++) {
                if(gameState.lanes.get(i)[j].isOccupiedByCyberTruck) {
                    if (truck[0][0] ==-1) {
                        truck[0][0]=j; truck[0][1]=i;
                    } else if(truck[1][0]==-1) {
                        truck[1][0]=j;truck[1][1]=i;
                    }
                }
            }
        }
        directionList.add(-1);
        directionList.add(1);
    }

    public String run() {
//        List<Object> blocks = getBlocksInFront(myCar.position.lane, myCar.position.block);
//        if (myCar.damage >= 5) {
//            return new FixCommand();
//        }
//        if (blocks.contains(Terrain.MUD)) {
//            int i = random.nextInt(directionList.size());
//            return new ChangeLaneCommand(directionList.get(i));
//        }
        prefix = new Result[gameState.lanes.size()][gameState.lanes.get(0).length];
        for(int i=0;i<prefix.length;i++) {
            prefix[i][0] = new Result(gameState.lanes.get(i)[0].terrain);
            for(int j=1;j<prefix[0].length;j++) {
//                prefix[i][j] = prefix[i][j-1].add(new Result(gameState.lanes.get(i)[j].terrain));
                prefix[i][j] = new Result(prefix[i][j-1]);
                prefix[i][j].add(new Result(gameState.lanes.get(i)[j].terrain));
            }
        }
        this.start = gameState.player.position.block-gameState.lanes.get(0)[0].position.block;
        this.end = gameState.lanes.get(0)[gameState.lanes.get(0).length-1].position.block - gameState.lanes.get(0)[0].position.block;
        int curLane = gameState.player.position.lane -1;
        Result now = new Result(gameState);

        ctWall = new int[ctLane][end+1];
        ctDamage = new int[ctLane][end+1];
        for(int i=0;i<ctLane;i++) {
            if(gameState.lanes.get(i)[0].terrain==Terrain.WALL) {
                ctWall[i][0] = 1;
                ctDamage[i][0] = 2;
            } else if(gameState.lanes.get(i)[0].terrain==Terrain.MUD) {
                ctWall[i][0] = 0;
                ctDamage[i][0] = 1;
            } else if(gameState.lanes.get(i)[0].terrain==Terrain.OIL_SPILL) {
                ctWall[i][0] = 0;
                ctDamage[i][0] = 1;
            } else {
                ctWall[i][0] = 0;
                ctDamage[i][0] = 0;
            }
            for(int j=1;j<=end;j++) {
                if(gameState.lanes.get(i)[j].terrain==Terrain.WALL) {
                    ctWall[i][j] = ctWall[i][j-1]+1;
                    ctDamage[i][j] = ctDamage[i][j-1]+2;
                } else if(gameState.lanes.get(i)[j].terrain==Terrain.MUD || gameState.lanes.get(i)[j].terrain==Terrain.OIL_SPILL) {
                    ctWall[i][j] = ctWall[i][j-1];
                    ctDamage[i][j] = ctDamage[i][j-1]+1;
                } else {
                    ctWall[i][j] = ctWall[i][j-1];
                    ctDamage[i][j] = ctDamage[i][j-1];
                }
            }
        }
//        System.out.printf("%d truck[0][0] %d truck[0][1] %d truck[1][0] %d truck[1][1]%n", truck[0][0], truck[0][1], truck[1][0], truck[1][1]);
//        System.out.printf("%d time, %d speed, %d damage, %d ctBoost, %d boostcounter%n", bestRes.time, bestRes.speed, bestRes.damage, bestRes.ctBoost, bestRes.boostcounter);
//        System.out.printf("%d truck[0][0] %d truck[0][1] %d truck[1][0] %d truck[1][1]%n", truck[0][0], truck[0][1], truck[1][0], truck[1][1]);
        if(now.damage>=5) {
            return "FIX";
        }
        updateBoostcounter(now);
// //        greedyByDamage(now, start, curLane);
//         Comparator<Command> comp = (Command c1, Command c2) -> c1.resi.greaterThanV3(c2.resi);
//         // ganti versi greaterThan buat coba kriteria yang lain

//         // greedy by damage
//         List<Command> commands = new ArrayList<>();
//         BoostCommand bc = new BoostCommand();
//         if(bc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
//             commands.add(bc);
//         }
//         AccelerateCommand ac = new AccelerateCommand();
//         if(ac.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
//             commands.add(ac);
//         }
//         NothingCommand nc = new NothingCommand();
//         if(nc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
//             commands.add(nc);
//         }
//         LizardCommand lc = new LizardCommand();
//         if(lc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall,gameState)) {
//             commands.add(lc);
//         }
//         TurnLeftCommand tlc = new TurnLeftCommand();
//         if(tlc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
//             commands.add(tlc);
//         }
//         TurnRightCommand trc = new TurnRightCommand();
//         if(trc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
//             commands.add(trc);
//         }
//         FixCommand fc = new FixCommand();
//         if(fc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
//             commands.add(fc);
//         }
//         Collections.sort(commands,comp);
//         return commands.get(0).render();
//        gapake attack dulu
//        boolean attack = bestCom.equals("NOTHING");
//        if(bestCom.equals("ACCELERATE")) {
//            if(now.boostcounter==1) {
//                attack = true;
//            } else {
//                if(min(nextSpeed(now.speed),maxSpeedIfDamage[now.damage])==now.speed) attack=true;
//            }
//        }
//        if(attack) {
//            attackStrategy(now);
//        }
//        return bestCom;

//        greedyByDamage(now, start, curLane);
        // Comparator<Command> comp = (Command c1, Command c2) -> c1.resi.greaterThanV3(c2.resi);
        // ganti versi greaterThan buat coba kriteria yang lain

        // greedy by lane
        ctBad = new int[ctLane];
        for(int i=0;i<ctLane;i++) {
            ctBad[i] = 0;
            for(int j=0;j<=end;j++) {
                if(gameState.lanes.get(i)[j].terrain==Terrain.WALL || gameState.lanes.get(i)[j].terrain==Terrain.MUD || gameState.lanes.get(i)[j].terrain==Terrain.OIL_SPILL){
                    ctBad[i]++;
                }
            }
        }

        best = ctBad[0];
        for(int i=1;i<ctLane;i++){
            if (best > ctBad[i]){
                best = i;
            }
        }

        // Mengecek jika harus memakai lizard atau tidak
        useLiz = false;
        if (best == curLane){
            for (int i=0; i<myCar.speed; i++){
                if ((gameState.lanes.get(curLane)[i].terrain==Terrain.WALL || gameState.lanes.get(curLane)[i].terrain==Terrain.MUD || gameState.lanes.get(curLane)[i].terrain==Terrain.OIL_SPILL) && hasPowerUp(PowerUps.LIZARD, myCar.powerups)){
                    useLiz = true;
                    break;
                }
            }
        }
        List<Command> commands = new ArrayList<>();

        AccelerateCommand ac = new AccelerateCommand();
        TurnLeftCommand tlc = new TurnLeftCommand();
        TurnRightCommand trc = new TurnRightCommand();
        BoostCommand bc = new BoostCommand();
        LizardCommand lc = new LizardCommand();
        if (myCar.speed == 0){
            commands.add(ac);
        }
        else if(best < curLane) {
            commands.add(tlc);
        }
        else if(best > curLane) {
            commands.add(trc);
        }
        else if(useLiz) {
            commands.add(lc);
        }
        else if(best == curLane && hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            commands.add(bc);
        }
        else if(best == curLane) {
            commands.add(ac);
        }
        // NothingCommand nc = new NothingCommand();
        // if(nc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
        //     commands.add(nc);
        // }
        // FixCommand fc = new FixCommand();
        // if(fc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
        //     commands.add(fc);
        // } 
        return commands.get(0).render();

    }

    private Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }    

    private void attackStrategy(Result cur) {
        int myX = gameState.player.position.block;
        int myY = gameState.player.position.lane-1;
        int otherX = gameState.opponent.position.block;
        int otherY = gameState.opponent.position.lane-1;
        int otherSpeed = gameState.opponent.speed;
        if(myX>otherX) {
            if(cur.ctOil>0) {
                if(otherY==myY && otherX+otherSpeed>=myX) {
                    bestCom = "USE_OIL";
                }
            }
            if(cur.ctTweet>0) {
                if(!bestCom.equals("USE_OIL")) {
                    boolean flagTruck=true;
                    int xLane = myX - gameState.lanes.get(0)[0].position.block;
                    List<Lane[]> curLanes = gameState.lanes;
                    Terrain myTerrain = curLanes.get(myY)[xLane].terrain;
                    if(myTerrain == Terrain.WALL || myTerrain == Terrain.MUD || myTerrain == Terrain.OIL_SPILL) {
                        flagTruck = false;
                    }
                    if(myY>0) {
                        Terrain leftTerrain = curLanes.get(myY-1)[xLane].terrain;
                        if(!(leftTerrain == Terrain.WALL || leftTerrain == Terrain.MUD || leftTerrain == Terrain.OIL_SPILL)) {
                            flagTruck = false;
                        }
                    }
                    if(myY+1<ctLane) {
                        Terrain rTerrain = curLanes.get(myY+1)[xLane].terrain;
                        if(!(rTerrain == Terrain.WALL || rTerrain == Terrain.MUD || rTerrain == Terrain.OIL_SPILL)) {
                            flagTruck = false;
                        }
                    }
                    if(flagTruck) {
                        bestCom = "USE_TWEET " + (myY+1) + " " + (myX);
                    }
                }
            }
        } else {
            if(cur.ctEmp>0) {
                if(cur.speed+myX>otherX && abs(myY-otherY)==1) {
                    bestCom = "USE_EMP";
                }
                if(otherX+cur.ctEmp*15>=track_length) {
                    bestCom = "USE_EMP";
                }
                if(otherSpeed==15 && cur.ctEmp>2) {
                    bestCom = "USE_EMP";
                }
            }
        }
    }



    /**
     * Returns map of blocks and the objects in the for the current lanes, returns the amount of blocks that can be
     * traversed at max speed.
     **/
//    private List<Object> getBlocksInFront(int lane, int block) {
//        List<Lane[]> map = gameState.lanes;
//        List<Object> blocks = new ArrayList<>();
//        int startBlock = map.get(0)[0].position.block;
//
//        Lane[] laneList = map.get(lane - 1); // current lane
//        for (int i = max(block - startBlock, 0); i <= block - startBlock + Bot.maxSpeed; i++) {
//            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
//                break;
//            }
//
//            blocks.add(laneList[i].terrain);
//
//        }
//        return blocks;
//    }
//    private void greedyByDamage(Result res, int x, int y) {
//        Result resi;
//        int[][] trucki;
//        int[] xiyi;
//        int xi, yi;
//        int bestDamage = 10;
//        if(res.ctBoost>0 && res.speed!=maxSpeedIfDamage[res.damage]) {
//            resi = new Result(res);
//            resi.ctBoost -=1;
//            resi.boosting=true;
//            resi.boostcounter=5;
//            resi.speed = maxSpeedIfDamage[resi.damage];
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            xi = min(x+resi.speed,end); yi = y;
//            resi.xbonus = max(0,x+resi.speed-end);
//            xiyi = updateXiYi(x,xi,yi,resi,trucki);
//            xi = xiyi[0]; yi = xiyi[1];
//            if(resi.damage<bestDamage) {
//                bestCom = "USE_BOOST";
//                bestDamage = resi.damage;
//            }
//        }
//        if(!res.boosting) {
//            resi = new Result(res);
//            resi.speed = min(nextSpeed(resi.speed), maxSpeedIfDamage[resi.damage]);
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            xi = min(x+resi.speed,end); yi=y;
//            resi.xbonus = max(0,x+resi.speed-end);
//            xiyi = updateXiYi(x,xi,yi,resi,trucki);
//            xi = xiyi[0]; yi = xiyi[1];
//            if(resi.damage<bestDamage) {
//                bestCom = "ACCELERATE";
//                bestDamage = resi.damage;
//            }
//        }
//        resi = new Result(res);
//        trucki = new int[2][2];
//        trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//        xi = min(x+resi.speed,end); yi=y;
//        resi.xbonus = max(0,x+resi.speed-end);
//        xiyi = updateXiYi(x,xi,yi,resi,trucki);
//        xi = xiyi[0]; yi = xiyi[1];
//        if(resi.damage<bestDamage) {
//            bestCom = "NOTHING";
//            bestDamage = resi.damage;
//        }
//        if(res.ctLizard>0) {
//            resi = new Result(res);
//            --resi.ctLizard;
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            xi = min(x+resi.speed,end); yi = y;
//            resi.xbonus = max(0,x+resi.speed-end);
//            if(xi!=x && xi==x+resi.speed) {
//                int curDamage=0; int curWall=0;
//                if(yi==trucki[0][1] && xi==trucki[0][0]) {
//                    --xi;
//                    ++curWall;
//                    curDamage +=2;
//                    trucki[0][0] = -1; trucki[0][1] = -1;
//                }
//                if(yi==trucki[1][1] && xi==trucki[1][0]) {
//                    --xi;
//                    ++curWall;
//                    curDamage +=2;
//                    trucki[1][0] = -1; trucki[1][1] = -1;
//                }
//                Terrain curTerrain = gameState.lanes.get(yi)[xi].terrain;
//                if(curTerrain==Terrain.MUD) {
//                    ++curDamage;
//                } else if(curTerrain==Terrain.OIL_SPILL) {
//                    ++curDamage;
//                } else if(curTerrain==Terrain.OIL_POWER) {
//                    ++resi.ctOil;
//                } else if(curTerrain==Terrain.BOOST) {
//                    ++resi.ctBoost;
//                } else if(curTerrain==Terrain.WALL) {
//                    curDamage +=2;
//                    ++curWall;
//                } else if(curTerrain==Terrain.LIZARD) {
//                    ++resi.ctLizard;
//                } else if(curTerrain==Terrain.TWEET) {
//                    ++resi.ctTweet;
//                } else if(curTerrain==Terrain.EMP) {
//                    ++resi.ctEmp;
//                }
//                if(curDamage>0) {
//                    if(curWall>0) {
//                        resi.speed = min(3,resi.speed);
//                    } else {
//                        resi.speed = max(3,prevSpeed(resi.speed));
//                    }
//                    resi.damage = min(5,resi.damage+curDamage);
//                    resi.speed = min(resi.speed,maxSpeedIfDamage[resi.damage]);
//                    if(resi.boosting) {
//                        resi.boosting=false;
//                        resi.boostcounter=0;
//                    }
//                }
//            }
//            if(resi.damage<bestDamage) {
//                bestCom = "USE_LIZARD";
//                bestDamage = resi.damage;
//            }
//        }
//        if(y>0) {
//            resi = new Result(res);
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            xi = min(x+resi.speed-1,end); yi = y-1;
//            resi.xbonus = max(0,x+resi.speed-1-end);
//            xiyi = updateXiYi(x-1,xi,yi,resi,trucki);
//            xi = xiyi[0]; yi = xiyi[1];
//            if(resi.damage<bestDamage) {
//                bestCom = "TURN_LEFT";
//                bestDamage = resi.damage;
//            }
//        }
//        if(y+1<ctLane) {
//            resi = new Result(res);
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            xi = min(x+resi.speed-1,end); yi=y+1;
//            resi.xbonus = max(0,x+resi.speed-1-end);
//            xiyi = updateXiYi(x-1,xi,yi,resi,trucki);
//            xi = xiyi[0]; yi = xiyi[1];
//            if(resi.damage<bestDamage) {
//                bestCom = "TURN_RIGHT";
//                bestDamage = resi.damage;
//            }
//        }
//        if(res.speed>0) {
//            resi = new Result(res);
//            resi.speed = min(prevSpeed(resi.speed),maxSpeedIfDamage[resi.damage]);
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            xi = min(x+resi.speed,end); yi=y;
//            resi.xbonus = max(0,x+resi.speed-end);
//            xiyi = updateXiYi(x, xi, yi, resi, trucki);
//            xi = xiyi[0]; yi = xiyi[1];
//            if(resi.damage<bestDamage) {
//                bestCom = "DECELERATE";
//                bestDamage = resi.damage;
//            }
//        }
//        if(res.damage>0) {
//            resi = new Result(res);
//            xi =x; yi=y;
//            resi.damage = max(0,resi.damage-2);
//            trucki = new int[2][2];
//            trucki[0][0] = truck[0][0]; trucki[0][1]=truck[0][1]; trucki[1][0]=truck[1][0]; trucki[1][1]=truck[1][1];
//            if(resi.damage<bestDamage) {
//                bestCom = "FIX";
//                bestDamage = resi.damage;
//            }
//        }
//    }

}
