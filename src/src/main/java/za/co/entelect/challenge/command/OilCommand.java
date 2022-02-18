package za.co.entelect.challenge.command;

import za.co.entelect.challenge.Bot;
import za.co.entelect.challenge.Result;

public class OilCommand extends Command {

    @Override
    public String render() {
        return String.format("USE_OIL");
    }

    public void run(Result cur, int myX, int myY, int otherX, int otherY, int otherSpeed, Bot bot) {
        if(cur.ctOil>0) {
            if(otherY==myY && otherX+otherSpeed>=myX) {
                bot.bestCom = "USE_OIL";
            }
        }
    }
}
