import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;

import bwta.BWTA;

public class TestBot1 extends DefaultBWListener {

    private Mirror mirror = new Mirror();

    private Game game;

    private Player self;

    public void run() {
        mirror.getModule().setEventListener(new DefaultBWListener() {
            @Override
            public void onUnitCreate(Unit unit) {
                System.out.println("New unit " + unit.getType());
            }

            @Override
            public void onStart() {
                game = mirror.getGame();
                self = game.self();

                //Use BWTA to analyze map
                //This may take a few minutes if the map is processed first time!
                System.out.println("Analyzing map...");
                BWTA.readMap();
                BWTA.analyze();
                System.out.println("Map data ready");

            }

            @Override
            public void onFrame() {
                // Called once every frame

                // Display the game frame rate in the upper left area of the screen
                game.drawTextScreen(200, 0, "FPS: " + game.getFPS());
                game.drawTextScreen(200, 20, "Avarage FPS: " + game.getAverageFPS());

                // Return if the game is a replay or is paused
                if ( game.isReplay() || game.isPaused() || game.self() == null) {
                    return;
                }

                // Prevent spamming by only running our onFrame every number of latency frames.
                // Latency frames are the number of frames before commands are processed.
                if ( game.getFrameCount() % game.getLatencyFrames() != 0) {
                    return;
                }

                // Iterate through all the units that we own
                for(Unit u : game.getAllUnits()) {
                    if ( !u.exists() )
                        continue;

                    if ( u.isLockedDown() || u.isMaelstrommed() || u.isStasised() ) 
                        continue;

                    if ( u.isLoaded() || !u.isPowered() || u.isStuck() )
                        continue;

                    if ( !u.isCompleted() || u.isConstructing() )
                        continue;

                    if ( u.getType().isWorker() ) {
                        if (u.isIdle()) {
                            if ( u.isCarryingGas() || u.isCarryingMinerals() )
                                u.returnCargo();
                            else if ( u.getPowerUp() != null ) {
                                if (u.gather(u.getClosestUnit()) == null); 
                            }
                        }
                    }
                }


            }

        });

        mirror.startGame();
    }

    public static void main(String[] args) {
        new TestBot1().run();
    }
}
