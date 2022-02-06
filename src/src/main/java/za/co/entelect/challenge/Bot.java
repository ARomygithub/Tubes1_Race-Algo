package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.*;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Bot {

//    private static final int maxSpeed = 9;
    private static final int[] maxSpeedIfDamage = {15,9,8,6,3,0};
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    private GameState gameState;
//    private Car opponent;
//    private Car myCar;
//    private final static Command FIX = new FixCommand();
    private Result bestRes = new Result();
    private String bestCom;
    private Result[][] prefix;
    private int[][] ctWall;
    private int[][] ctDamage;
    private int start, end;
    private int ctLane;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
//        this.myCar = gameState.player;
//        this.opponent = gameState.opponent;
        this.ctLane = gameState.lanes.size();

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
                prefix[i][j] = prefix[i][j-1].add(new Result(gameState.lanes.get(i)[j].terrain));
            }
        }
        this.start = gameState.player.position.block-gameState.lanes.get(0)[0].position.block;
        this.end = gameState.lanes.get(0)[gameState.lanes.get(0).length-1].position.block - gameState.lanes.get(0)[0].position.block;
        int curLane = gameState.player.position.lane;
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
                } else if(gameState.lanes.get(i)[0].terrain==Terrain.MUD || gameState.lanes.get(i)[0].terrain==Terrain.OIL_SPILL) {
                    ctWall[i][j] = ctWall[i][j-1];
                    ctDamage[i][j] = ctDamage[i][j-1]+1;
                } else {
                    ctWall[i][j] = ctWall[i][j-1];
                    ctDamage[i][j] = ctDamage[i][j-1];
                }
            }
        }

        dfs(0,start, curLane, now, "NONE");
        return bestCom;
    }

    private int nextSpeed(int x) {
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

    private int prevSpeed(int x) {
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

    private void dfs(int t, int x, int y, Result res, String com) {
        if(res.boosting) {
            --res.boostcounter;
            if(res.boostcounter==0){
                res.boosting=false;
                if(res.speed==15) res.speed=9;
            }
        }
        int xi, yi;
        Result resi;
        String comi;
        if(res.damage>=5) {
//            xi, yi, resi, prevSpeedi  = FIX(x,y,res,prevSpeed);
            xi = x; yi = y;
            resi = new Result(res);
            resi.damage = max(0,resi.damage-2);

            if(com.equals("NONE")) {
                comi = "FIX";
            } else {
                comi = com;
            }
            if(xi>=end) {
                resi.time = t+1;
                if(resi.greaterThan(bestRes)) {
                    bestRes = resi;
                    bestCom = comi;
                }
            } else {
                if(t+1<bestRes.time) {
                    dfs(t+1,xi,yi,resi, comi);
                }
            }
        } else {
            if(res.ctBoost>0) {
//                xi, yi, resi, prevSpeedi = USE_BOOST(x,y,res,prevSpeed);
                resi = new Result(res);
                resi.ctBoost -=1;
                resi.boosting=true;
                resi.boostcounter=5;
                resi.speed = maxSpeedIfDamage[resi.damage];
                xi = min(x+resi.speed,end); yi = y;
                Result temp = prefix[yi][xi].minus(prefix[yi][x]);
                // tambah temp ke resi (powerups).
                resi = resi.add(temp);
                // damage dari ctWall, ctDamage, boosting, boostcounter juga.
                int curDamage = ctDamage[yi][xi] - ctDamage[yi][x];
                int curWall = ctWall[yi][xi] - ctWall[yi][x];
                if(curDamage>0) {
                    resi.damage = min(5,resi.damage+curDamage);
                    resi.speed = min(resi.speed,maxSpeedIfDamage[resi.damage]);
                    if(curWall>0) {
                        resi.speed = min(3,resi.speed);
                    }
                    resi.boosting=false;
                    resi.boostcounter=0;
                }
                if(com.equals("NONE")) {
                    comi = "USE_BOOST";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
                }
            }
            if(!res.boosting) {
//            xi, yi, resi, prevSpeedi = ACCELERATE(x,y,res,prevSpeed);
                resi = new Result(res);
                resi.speed = min(nextSpeed(resi.speed), maxSpeedIfDamage[resi.damage]);
                xi = min(x+resi.speed,end); yi=y;
                Result temp = prefix[yi][xi].minus(prefix[yi][x]);
                resi = resi.add(temp);
                int curDamage = ctDamage[yi][xi]-ctDamage[yi][x];
                int curWall = ctWall[yi][xi] - ctWall[yi][x];
                if(curDamage>0) {
                    if(curWall>0) {
                        resi.speed = min(3,resi.speed);
                    } else {
                        resi.speed = max(3,prevSpeed(resi.speed));
                    }
                    resi.damage = min(5,resi.damage+curDamage);
                    resi.speed = min(resi.speed,maxSpeedIfDamage[resi.damage]);
                }
                if(com.equals("NONE")) {
                    comi = "ACCELERATE";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
                }
            }
            if(res.ctLizard>0) {
//                xi, yi, resi, prevSpeedi = USE_LIZARD(x,y,res,prevSpeed);
                resi = new Result(res);
                xi = min(x+resi.speed,end); yi = y;
                if(xi!=x) {
                    int curDamage=0; int curWall=0;
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
                if(com.equals("NONE")) {
                    comi = "USE_LIZARD";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
                }
            }
//            xi, yi, resi, prevSpeedi = NOTHING(x,y,res,prevSpeed);
            resi = new Result(res);
            xi = min(x+resi.speed,end); yi=y;
            Result temp = prefix[yi][xi].minus(prefix[yi][x]);
            resi = resi.add(temp);
            int curDamage= ctDamage[yi][xi]-ctDamage[yi][x];
            int curWall = ctWall[yi][xi]-ctWall[yi][x];
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
            if(com.equals("NONE")) {
                comi = "NOTHING";
            } else {
                comi = com;
            }
            if(xi>=end) {
                resi.time = t+1;
                if(resi.greaterThan(bestRes)) {
                    bestRes = resi;
                    bestCom = comi;
                }
            } else {
                if(t+1<bestRes.time) {
                    dfs(t+1,xi,yi,resi, comi);
                }
            }
            if(y>0) {
//                xi, yi, resi, prevSpeedi = TURN_LEFT(x,y,res,prevSpeed);
                resi = new Result(res);
                xi = min(x+resi.speed-1,end); yi = y-1;
                if(x-1<0) {
                    temp = prefix[yi][xi];
                } else {
                    temp = prefix[yi][xi].minus(prefix[yi][x-1]);
                }
                resi = resi.add(temp);
                if(x-1<0) {
                    curDamage = ctDamage[yi][xi];
                    curWall = ctWall[yi][xi];
                } else {
                    curDamage = ctDamage[yi][xi]-ctDamage[yi][x-1];
                    curWall = ctWall[yi][xi] - ctWall[yi][x-1];
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
                if(com.equals("NONE")) {
                    comi = "TURN_LEFT";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
                }
            }
            if(y+1<ctLane) {
//                xi, yi, resi, prevSpeedi = TURN_RIGHT(x,y,res,prevSpeed);
                resi = new Result(res);
                xi = min(x+resi.speed-1,end); yi=y+1;
                if(x-1<0) {
                    temp = prefix[yi][xi];
                } else {
                    temp = prefix[yi][xi].minus(prefix[yi][x-1]);
                }
                resi = resi.add(temp);
                if(x-1<0) {
                    curDamage = ctDamage[yi][xi];
                    curWall = ctWall[yi][xi];
                } else {
                    curDamage = ctDamage[yi][xi]-ctDamage[yi][x-1];
                    curWall = ctWall[yi][xi] - ctWall[yi][x-1];
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
                if(com.equals("NONE")) {
                    comi = "TURN_RIGHT";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
                }
            }
            if(res.speed>0) {
//                xi, yi, resi, prevSpeedi = DECELERATE(x,y,res,prevSpeed);
                resi = new Result(res);
                resi.speed = min(prevSpeed(resi.speed),maxSpeedIfDamage[resi.damage]);
                xi = min(x+resi.speed,end); yi=y;
                temp = prefix[yi][xi].minus(prefix[yi][x]);
                resi = resi.add(temp);
                curDamage= ctDamage[yi][xi]-ctDamage[yi][x];
                curWall = ctWall[yi][xi]-ctWall[yi][x];
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
                if(com.equals("NONE")) {
                    comi = "DECELERATE";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
                }
            }
            if(res.damage>0) {
//                xi, yi, resi, prevSpeedi = FIX(x,y,res,prevSpeed);
                resi = new Result(res);
                xi =x; yi=y;
                resi.damage = max(0,resi.damage-2);
                if(com.equals("NONE")) {
                    comi = "FIX";
                } else {
                    comi = com;
                }
                if(xi>=end) {
                    resi.time = t+1;
                    if(resi.greaterThan(bestRes)) {
                        bestRes = resi;
                        bestCom = comi;
                    }
                } else {
                    if(t+1<bestRes.time) {
                        dfs(t+1,xi,yi,resi, comi);
                    }
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

}
