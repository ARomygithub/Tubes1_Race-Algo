package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Bot;
import za.co.entelect.challenge.Result;

import static java.lang.Math.abs;
import static za.co.entelect.challenge.Util.track_length;

public class EmpCommand extends Command {

    @Override
    public String render() {
        return String.format("USE_EMP");
    }

    public void run(Result cur, int myX,int myY,int otherX,int otherY,int otherSpeed, Bot bot) {
        if(cur.ctEmp>0 && myX<otherX) {
            if(cur.speed+myX>otherX && abs(myY-otherY)==1) {
                bot.bestCom = "USE_EMP";
            }
            if(otherX+cur.ctEmp*15>=track_length && abs(myY-otherY)<=1) {
                bot.bestCom = "USE_EMP";
            }
            if(otherSpeed==15 && cur.ctEmp>2 && abs(myY-otherY)<=1) {
                bot.bestCom = "USE_EMP";
            }
        }
    }
}
