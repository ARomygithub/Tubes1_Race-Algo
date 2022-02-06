package za.co.entelect.challenge;

import com.google.gson.Gson;
import za.co.entelect.challenge.command.Command;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final String ROUNDS_DIRECTORY = "rounds";
    private static final String STATE_FILE_NAME = "state.json";
//    public static int prevSpeed=-1;

    /**
     * Read the current state, feed it to the bot, get the output and print it to stdout
     *
     * @param args the args
     **/
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();
        Random random = new Random(System.nanoTime());

        while(true) {
            try {
                int roundNumber = sc.nextInt();

                String statePath = String.format("./%s/%d/%s", ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
//                String statePath = String.format("target/%s/%d/%s", ROUNDS_DIRECTORY, roundNumber, STATE_FILE_NAME);
                String state = new String(Files.readAllBytes(Paths.get(statePath)));

                GameState gameState = gson.fromJson(state, GameState.class);
//                printPowerUps(gameState);
//                printWorldMap(gameState);
                String command = new Bot(random, gameState).run();
                System.out.println(String.format("C;%d;%s", roundNumber, command));
//                prevSpeed = gameState.player.speed;
//                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void printWorldMap(GameState gS) {
        int i=0;
        for(Lane[] lanei : gS.lanes) {
            System.out.println("Lane[] ke-"+i);
            i +=1;
            System.out.println("banyak petak: "+lanei.length);
            for(Lane petak : lanei) {
                System.out.println(petak.terrain);
            }
        }
    }

    public static void printPowerUps(GameState gS) {
        for(PowerUps p : gS.player.powerups) {
            System.out.println(p);
        }
    }
}
