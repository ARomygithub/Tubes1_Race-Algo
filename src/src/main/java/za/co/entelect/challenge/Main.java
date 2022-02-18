package za.co.entelect.challenge;

import com.google.gson.Gson;
import za.co.entelect.challenge.entities.GameState;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final String ROUNDS_DIRECTORY = "rounds";
    private static final String STATE_FILE_NAME = "state.json";
//    public static int prevSpeed=-1;
    public int[] myTruckPos= new int[] {-1,-1};

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
                String state = new String(Files.readAllBytes(Paths.get(statePath)));

                GameState gameState = gson.fromJson(state, GameState.class);
                String command = new Bot(random, gameState).run();
                System.out.println(String.format("C;%d;%s", roundNumber, command));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
