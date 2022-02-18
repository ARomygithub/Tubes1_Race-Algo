package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Bot;
import za.co.entelect.challenge.Result;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.Terrain;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TweetCommand extends Command{
    @Override
    public String render() {return "NOTHING";} // render tidak dipakai untuk TweetCommand

    public void run(Result cur,int myX, int myY, int otherX, int otherY, int otherSpeed, Bot bot, List<Lane[]> lanes, int[][] ctDamage) {
        if(cur.ctTweet>0) {
            if(!bot.bestCom.equals("USE_OIL")) {
                boolean flagTruck=true;
                int xLane = myX - lanes.get(0)[0].position.block;
                int ctLane = lanes.size();
                int end = lanes.get(0).length-1;
                Terrain myTerrain = lanes.get(myY)[xLane].terrain;
                if(myTerrain == Terrain.WALL || myTerrain == Terrain.MUD || myTerrain == Terrain.OIL_SPILL) {
                    flagTruck = false;
                }
                if(myY>0) {
//                        Terrain leftTerrain = curLanes.get(myY-1)[xLane].terrain;
                    if(ctDamage[myY-1][min(end,xLane+2)]-ctDamage[myY-1][max(0,xLane-2)]==0) {
                        flagTruck = false;
                    }
                }
                if(myY+1<ctLane) {
//                        Terrain rTerrain = curLanes.get(myY+1)[xLane].terrain;
                    if(ctDamage[myY+1][min(end,xLane+2)]-ctDamage[myY+1][max(0,xLane-2)]==0) {
                        flagTruck = false;
                    }
                }
                if(flagTruck) {
                    bot.bestCom = "USE_TWEET " + (myY+1) + " " + (myX);
                } else {
                    int tweetX=otherX+2*otherSpeed-2;
                    if(otherSpeed==3) {
                        tweetX = otherX+7;
                    } else if(otherSpeed==0) {
                        tweetX = otherX+1;
                    }
                    bot.bestCom = "USE_TWEET " + (otherY+1) + " " + tweetX;
                }
            }
        }
    }
}
