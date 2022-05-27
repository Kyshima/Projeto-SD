package frogger;

import java.awt.event.KeyEvent;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

public class MainServerSide extends StaticScreenGame{
    static final int WORLD_WIDTH = (13*32);
    static final int WORLD_HEIGHT = (14*32);
    static final Vector2D FROGGER_START = new Vector2D(6*32,WORLD_HEIGHT-32);

    static final String RSC_PATH = "resources/";
    static final String SPRITE_SHEET = RSC_PATH + "frogger_sprites.png";

    static final int FROGGER_LIVES      = 5;
    static final int STARTING_LEVEL     = 1;
    static final int DEFAULT_LEVEL_TIME = 60;

    private FroggerCollisionDetection frogCol;
    private Frogger frog;
    private AudioEfx audiofx;
    private FroggerUI ui;
    private WindGust wind;
    private HeatWave hwave;
    private GoalManager goalmanager;

    public static AbstractBodyLayer<MovingEntity> movingObjectsLayer;
    private AbstractBodyLayer<MovingEntity> particleLayer;

    private MovingEntityFactory roadLine1;
    private MovingEntityFactory roadLine2;
    private MovingEntityFactory roadLine3;
    private MovingEntityFactory roadLine4;
    private MovingEntityFactory roadLine5;

    private MovingEntityFactory riverLine1;
    private MovingEntityFactory riverLine2;
    private MovingEntityFactory riverLine3;
    private MovingEntityFactory riverLine4;
    private MovingEntityFactory riverLine5;

    private ImageBackgroundLayer backgroundLayer;

    static final int GAME_INTRO        = 0;
    static final int GAME_PLAY         = 1;
    static final int GAME_FINISH_LEVEL = 2;
    static final int GAME_INSTRUCTIONS = 3;
    static final int GAME_OVER         = 4;

    protected int GameState = GAME_INTRO;
    protected int GameLevel = STARTING_LEVEL;

    public int GameLives    = FROGGER_LIVES;
    public int GameScore    = 0;

    public int levelTimer = DEFAULT_LEVEL_TIME;

    private boolean space_has_been_released = false;
    private boolean keyPressed = false;
    private boolean listenInput = true;

    /**
     * Initialize game objects
     */
    public MainServerSide() {
        super(WORLD_WIDTH, WORLD_HEIGHT, false);
        movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
        initializeLevel(1);
    }


    public void initializeLevel(int level) {

        /* dV is the velocity multiplier for all moving objects at the current game level */
        double dV = level*0.05 + 1;

        movingObjectsLayer.clear();

        // River Traffic
        riverLine1 = new MovingEntityFactory(new Vector2D(-(32*3),2*32),
                new Vector2D(0.06*dV,0));

        riverLine2 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH,3*32),
                new Vector2D(-0.04*dV,0));

        riverLine3 = new MovingEntityFactory(new Vector2D(-(32*3),4*32),
                new Vector2D(0.09*dV,0));

        riverLine4 = new MovingEntityFactory(new Vector2D(-(32*4),5*32),
                new Vector2D(0.045*dV,0));

        riverLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH,6*32),
                new Vector2D(-0.045*dV,0));

        // Road Traffic
        roadLine1 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 8*32),
                new Vector2D(-0.1*dV, 0));

        roadLine2 = new MovingEntityFactory(new Vector2D(-(32*4), 9*32),
                new Vector2D(0.08*dV, 0));

        roadLine3 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 10*32),
                new Vector2D(-0.12*dV, 0));

        roadLine4 = new MovingEntityFactory(new Vector2D(-(32*4), 11*32),
                new Vector2D(0.075*dV, 0));

        roadLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 12*32),
                new Vector2D(-0.05*dV, 0));

        goalmanager.init(level);
        for (Goal g : goalmanager.get()) {
            movingObjectsLayer.add(g);
        }

        /* Build some traffic before game starts buy running MovingEntityFactories for fews cycles */
        for (int i=0; i<500; i++)
            cycleTraffic(10);
    }


    /**
     * Populate movingObjectLayer with a cycle of cars/trucks, moving tree logs, etc
     *
     * @param deltaMs
     */
    public void cycleTraffic(long deltaMs) {
        MovingEntity m;
        /* Road traffic updates */
        roadLine1.update(deltaMs);
        if ((m = roadLine1.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine2.update(deltaMs);
        if ((m = roadLine2.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine3.update(deltaMs);
        if ((m = roadLine3.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine4.update(deltaMs);
        if ((m = roadLine4.buildVehicle()) != null) movingObjectsLayer.add(m);

        roadLine5.update(deltaMs);
        if ((m = roadLine5.buildVehicle()) != null) movingObjectsLayer.add(m);


        /* River traffic updates */
        riverLine1.update(deltaMs);
        if ((m = riverLine1.buildShortLogWithTurtles(40)) != null) movingObjectsLayer.add(m);

        riverLine2.update(deltaMs);
        if ((m = riverLine2.buildLongLogWithCrocodile(30)) != null) movingObjectsLayer.add(m);

        riverLine3.update(deltaMs);
        if ((m = riverLine3.buildShortLogWithTurtles(50)) != null) movingObjectsLayer.add(m);

        riverLine4.update(deltaMs);
        if ((m = riverLine4.buildLongLogWithCrocodile(20)) != null) movingObjectsLayer.add(m);

        riverLine5.update(deltaMs);
        if ((m = riverLine5.buildShortLogWithTurtles(10)) != null) movingObjectsLayer.add(m);

        // Do Wind
        if ((m = wind.genParticles(GameLevel)) != null) particleLayer.add(m);

        // HeatWave
        if ((m = hwave.genParticles(frog.getCenterPosition())) != null) particleLayer.add(m);

        movingObjectsLayer.update(deltaMs);
        particleLayer.update(deltaMs);
    }

    /**
     * w00t
     */
    public void update(long deltaMs) {
        if (GameState == GAME_PLAY) {
            cycleTraffic(deltaMs);
        }
    }
}
