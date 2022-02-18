package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.*;
import static za.co.entelect.challenge.Util.*;

import java.util.*;

import static java.lang.Math.*;

public class Bot {
    private List<Integer> directionList = new ArrayList<>();

    private Random random;
    public GameState gameState;
//    private Car opponent;
//    private Car myCar;
    private Result bestRes = new Result();
    public String bestCom;
    private Result[][] prefix;
    private int[][] ctWall;
    private int[][] ctDamage;
    private int start, end;
    public static int ctLane;
    private int[][] truck = new int[][] {{-1,-1}, {-1,-1}};
//    private int ctDebug=0;

    public Bot(Random random, GameState gameState) {
        this.random = random;
        this.gameState = gameState;
//        this.myCar = gameState.player;
//        this.opponent = gameState.opponent;
        ctLane = gameState.lanes.size();
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
        // metode run mengembalikan string Command yang dipilih berdasarkan strategi greedy
        // start: indeks block Car sekarang
        // end: indeks block terakhir yang terlihat
        // curLane: indeks lane Car sekarang
        this.start = gameState.player.position.block-gameState.lanes.get(0)[0].position.block;
        this.end = gameState.lanes.get(0).length-1;
        int curLane = gameState.player.position.lane -1;
        // prekomputasi variabel prefix, ctDamage, dan ctWall. Secara berurut berisi informasi
        // prefix jumlah powerups, jumlah Damage, dan jumlah Wall ditambah cybertruck yang ada pada lintasan.
        precompute();
        // now berisi informasi Car sekarang, berupa speed, damage, jumlah setiap powerups, boostcounter
        // dan boosting.
        Result now = new Result(gameState);

        // Jika damage>=5, pilih Fix
        if(now.damage>=5) {
            return "FIX";
        }
        // update kondisi boost
        updateBoostcounter(now);
        // digunakan metode greaterThanV6 sebagai fungsi seleksi utama
        // juga sebagai penentu kriteria optimum lokal
        Comparator<Command> comp = (Command c1, Command c2) -> c1.resi.greaterThanV6(c2.resi);
        // versi greaterThan dapat diganti untuk mencoba kriteria yang lain

        List<Command> commands = new ArrayList<>();
        // untuk setiap command dari 8 command non-attack, metode run mengembalikan boolean
        // fungsi seleksi dan kelayakan khusus setiap command. Bila memenuhi, disimpan atribut
        // resi bertipe Result berisi informasi Car setelah command tersebut dijalankan
        BoostCommand bc = new BoostCommand();
        if(bc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(bc);
        }
        AccelerateCommand ac = new AccelerateCommand();
        if(ac.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(ac);
        }
        NothingCommand nc = new NothingCommand();
        if(nc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(nc);
        }
        LizardCommand lc = new LizardCommand();
        if(lc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall,gameState)) {
            commands.add(lc);
        }
        TurnLeftCommand tlc = new TurnLeftCommand();
        if(tlc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(tlc);
        }
        TurnRightCommand trc = new TurnRightCommand();
        if(trc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(trc);
        }
        DecelerateCommand dc = new DecelerateCommand();
        if(dc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(dc);
        }
        FixCommand fc = new FixCommand();
        if(fc.run(now,start,curLane,truck,end,prefix,ctDamage,ctWall)) {
            commands.add(fc);
        }
        // Command yang memenuhi diurutkan untuk mencari optimum lokal
        Collections.sort(commands,comp);
        bestCom = commands.get(0).render();
        // Bila command yang terpilih berefek sama dengan nothing, yaitu command nothing atau
        // command accelerate dengan kondisi Car sedang speed maksimum, lanjutkan dengan
        // attackStrategy menggunakan 3 command attack.
        boolean attack = bestCom.equals("NOTHING");
        if(bestCom.equals("ACCELERATE")) {
            if(min(nextSpeed(now.speed),maxSpeedIfDamage[now.damage])==now.speed) attack=true;
        }
        if(attack) {
            attackStrategy(now);
        }
        return bestCom;
    }

    private void precompute() {
        prefix = new Result[gameState.lanes.size()][gameState.lanes.get(0).length];
        for(int i=0;i<prefix.length;i++) {
            prefix[i][0] = new Result(gameState.lanes.get(i)[0].terrain);
            for(int j=1;j<prefix[0].length;j++) {
                prefix[i][j] = new Result(prefix[i][j-1]);
                prefix[i][j].add(new Result(gameState.lanes.get(i)[j].terrain));
            }
        }

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
    }

    private void attackStrategy(Result cur) {
        int myX = gameState.player.position.block;
        int myY = gameState.player.position.lane-1;
        int otherX = gameState.opponent.position.block;
        int otherY = gameState.opponent.position.lane-1;
        int otherSpeed = gameState.opponent.speed;
        if(myX>otherX) {
            OilCommand oc = new OilCommand();
            oc.run(cur,myX,myY,otherX,otherY,otherSpeed,this);
            TweetCommand tc = new TweetCommand();
            tc.run(cur,myX,myY,otherX,otherY,otherSpeed,this,gameState.lanes,ctDamage);
            if(bestCom.equals("NOTHING") || bestCom.equals("ACCELERATE")) {
                if(cur.ctOil*15+myX>=track_length) {
                    bestCom = "USE_OIL";
                }
            }
        } else if(myX<otherX){
            EmpCommand ec = new EmpCommand();
            ec.run(cur,myX,myY,otherX,otherY,otherSpeed,this);
        }
    }
}
