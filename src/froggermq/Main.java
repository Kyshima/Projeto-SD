/**
 * Copyright (c) 2009 Vitaliy Pavlenko
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package froggermq;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import edu.ufp.inf.sd.rabbitmqservices.project.producer.FroggerClient;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

public class Main extends StaticScreenGame {
	static final int WORLD_WIDTH = (13*32);
	static final int WORLD_HEIGHT = (14*32);


	static final ArrayList<Vector2D> FROGGER_START_ARRAY = new ArrayList<>();

	static final ArrayList<Frogger> FROGGERS= new ArrayList<>();

	static final String RSC_PATH = "resources/";
	static final String SPRITE_SHEET = RSC_PATH + "frogger_sprites.png";
	static final int FROGGER_LIVES      = 5;
    static final int STARTING_LEVEL     = 1;
	static final int DEFAULT_LEVEL_TIME = 60;
	
	private FroggerCollisionDetection frogCol;
	private AudioEfx audiofx;
	private FroggerUI ui;
	private WindGust wind;
	private HeatWave hwave;
	private GoalManager goalmanager;
	
	private AbstractBodyLayer<MovingEntity> movingObjectsLayer;
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

	static final int GAME_BREAK         = 5;
    
	protected int GameState = GAME_INTRO;
	protected int GameLevel = STARTING_LEVEL;
	
    public int GameLives    = FROGGER_LIVES;
    public int GameScore    = 0;
    
    public int levelTimer = DEFAULT_LEVEL_TIME;
    
    private boolean space_has_been_released = false;
	private boolean keyPressed = false;
	private boolean listenInput = true;

	public static int id;

	public FroggerClient fc;

	public boolean enabled = false;

	//public boolean building = false;
	
    /**
	 * Initialize game objects
	 */
	public Main () {
		
		super(WORLD_WIDTH, WORLD_HEIGHT, false);
		
		gameframe.setTitle("Frogger");
		
		ResourceFactory.getFactory().loadResources(RSC_PATH, "resources.xml");

		ImageResource bkg = ResourceFactory.getFactory().getFrames(
				SPRITE_SHEET + "#background").get(0);
		backgroundLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH,
				WORLD_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);
		
		// Used in CollisionObject, basically 2 different collision spheres
		// 30x30 is a large sphere (sphere that fits inside a 30x30 pixel rectangle)
		//  4x4 is a tiny sphere
		PaintableCanvas.loadDefaultFrames("col", 30, 30, 2, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("colSmall", 4, 4, 2, JIGSHAPE.RECTANGLE, null);
			

		ui = new FroggerUI(this);
		wind = new WindGust();
		hwave = new HeatWave();
		goalmanager = new GoalManager();
		
		movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<>();
		particleLayer = new AbstractBodyLayer.IterativeUpdate<>();
		
		//initializeLevel(1);
	}

	public Main (int num) {

		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		id = num;

		gameframe.setTitle("Frogger");

		ResourceFactory.getFactory().loadResources(RSC_PATH, "resources.xml");

		ImageResource bkg = ResourceFactory.getFactory().getFrames(
				SPRITE_SHEET + "#background").get(0);
		backgroundLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH,
				WORLD_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);

		// Used in CollisionObject, basically 2 different collision spheres
		// 30x30 is a large sphere (sphere that fits inside a 30x30 pixel rectangle)
		//  4x4 is a tiny sphere
		PaintableCanvas.loadDefaultFrames("col", 30, 30, 2, JIGSHAPE.RECTANGLE, null);
		PaintableCanvas.loadDefaultFrames("colSmall", 4, 4, 2, JIGSHAPE.RECTANGLE, null);


		ui = new FroggerUI(this);
		wind = new WindGust();
		hwave = new HeatWave();
		goalmanager = new GoalManager();

		movingObjectsLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();
		particleLayer = new AbstractBodyLayer.IterativeUpdate<MovingEntity>();

		//initializeLevel(1);
	}
	
	
	public void initializeLevel(int level) {

		/* dV is the velocity multiplier for all moving objects at the current game level */
		double dV = level*0.05 + 1;
		
		movingObjectsLayer.clear();
		
		/* River Traffic */
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
		
		/* Road Traffic */
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
		/*for (int i=0; i<500; i++)
			cycleTraffic(10);*/
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
	    if ((m = riverLine1.buildShortLogWithTurtles()) != null) movingObjectsLayer.add(m);
		
		riverLine2.update(deltaMs);
	    if ((m = riverLine2.buildLongLogWithCrocodile()) != null) movingObjectsLayer.add(m);
		
		riverLine3.update(deltaMs);
	    if ((m = riverLine3.buildShortLogWithTurtles()) != null) movingObjectsLayer.add(m);
		
		riverLine4.update(deltaMs);
	    if ((m = riverLine4.buildLongLogWithCrocodile()) != null) movingObjectsLayer.add(m);

		riverLine5.update(deltaMs);
	    if ((m = riverLine5.buildShortLogWithTurtles()) != null) movingObjectsLayer.add(m);
	    
	    // Do Wind
	    if ((m = wind.genParticles(GameLevel)) != null) particleLayer.add(m);
	    
	    // HeatWave
		for (int i = 0; i < 4; i++){
			if ((m = hwave.genParticles(FROGGERS.get(i).getCenterPosition())) != null) particleLayer.add(m);
		}
	        
	    movingObjectsLayer.update(deltaMs);
	    particleLayer.update(deltaMs);
	}
	
	/**
	 * Handling Frogger movement from keyboard input
	 */
	public void froggerKeyboardHandler() throws IOException, TimeoutException {
 		keyboard.poll();
		
 		boolean keyReleased = false;
        boolean downPressed = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean upPressed = keyboard.isPressed(KeyEvent.VK_UP);
		boolean leftPressed = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean rightPressed = keyboard.isPressed(KeyEvent.VK_RIGHT);
		
		// Enable/Disable cheating
		if (keyboard.isPressed(KeyEvent.VK_C))
			FroggerClient.godMode(id);
		if (keyboard.isPressed(KeyEvent.VK_V))
			FROGGERS.get(id).cheating = false;
		if (keyboard.isPressed(KeyEvent.VK_0)) {
			GameLevel = 10;
			initializeLevel(GameLevel);
		}
		
		
		/*
		 * This logic checks for key strokes.
		 * It registers a key press, and ignores all other key strokes
		 * until the first key has been released
		 */
		if (downPressed || upPressed || leftPressed || rightPressed)
			keyPressed = true;
		else if (keyPressed)
			keyReleased = true;
		
		if (listenInput) {
		    if (downPressed)
				fc.movement_frogger(id, 0);
		    if (upPressed)
				fc.movement_frogger(id, 1);
		    if (leftPressed)
				fc.movement_frogger(id, 2);
	 	    if (rightPressed)
				 fc.movement_frogger(id, 3);
	 	    if (keyPressed)
	            listenInput = false;
		}
		
		if (keyReleased) {
			listenInput = true;
			keyPressed = false;
		}
		
		if (keyboard.isPressed(KeyEvent.VK_ESCAPE))
			GameState = GAME_INTRO;
	}
	
	/**
	 * Handle keyboard events while at the game intro menu
	 */
	public void menuKeyboardHandler() throws IOException, TimeoutException {
		keyboard.poll();
		
		// Following 2 if statements allow capture space bar key strokes
		if (!keyboard.isPressed(KeyEvent.VK_SPACE)) {
			space_has_been_released = true;
		}
		
		if (!space_has_been_released)
			return;

		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			FroggerClient.start_frogger();
			if(enabled) {
				addFroggers();
				switch (GameState) {
					case GAME_INSTRUCTIONS:
					case GAME_OVER:
						GameState = GAME_INTRO;
						space_has_been_released = false;
						break;
					default:
						GameLives = FROGGER_LIVES;
						GameScore = 0;
						GameLevel = STARTING_LEVEL;
						levelTimer = DEFAULT_LEVEL_TIME;
						for (int i = 0; i < 4; i++) {
							//System.out.println(FROGGER_START_ARRAY.get(i).toString());
							FROGGERS.get(id).setPosition(FROGGER_START_ARRAY.get(id + 1));
						}
						GameState = GAME_PLAY;
						audiofx.playGameMusic();
						initializeLevel(GameLevel);
				}
			}
		}
		if (keyboard.isPressed(KeyEvent.VK_H))
			GameState = GAME_INSTRUCTIONS;
	}
	
	/**
	 * Handle keyboard when finished a level
	 */
	public void finishLevelKeyboardHandler() throws IOException, TimeoutException, InterruptedException {
		keyboard.poll();
		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			GameState = GAME_BREAK;
			//building = true;
			Thread.sleep(1000);
			FroggerClient.nextLevel();
		}
	}
	
	
	/**
	 * w00t
	 */
	public void update(long deltaMs) {
		switch(GameState) {
		case GAME_PLAY:
			if(enabled) {
				try {
					froggerKeyboardHandler();
				} catch (IOException | TimeoutException e) {
					throw new RuntimeException(e);
				}
				wind.update(deltaMs);
				hwave.update(deltaMs);

				for (int i = 0; i < 4; i++) {
					FROGGERS.get(i).update(deltaMs);
				}

				//audiofx.update(deltaMs);
				ui.update(deltaMs);

				cycleTraffic(deltaMs);
				try {
					frogCol.testCollision(movingObjectsLayer);
				} catch (IOException | TimeoutException e) {
					throw new RuntimeException(e);
				}

				// Wind gusts work only when Frogger is on the river
				if (frogCol.isInRiver())
					wind.start(GameLevel);

				for (int i = 0; i < 4; i++) {
					wind.perform(FROGGERS.get(i), GameLevel, deltaMs);

					// Do the heat wave only when Frogger is on hot pavement
					if (frogCol.isOnRoad())
						hwave.start(FROGGERS.get(i), GameLevel);
					hwave.perform(FROGGERS.get(i), deltaMs, GameLevel);


					if (!FROGGERS.get(i).isAlive)
						particleLayer.clear();
				}

				goalmanager.update(deltaMs);

				if (goalmanager.getUnreached().size() == 0) {
					try {
						FroggerClient.endLevel();
					} catch (IOException | TimeoutException e) {
						throw new RuntimeException(e);
					}
				}

				FROGGERS.get(id).deltaTime += deltaMs;
				if (FROGGERS.get(id).deltaTime > 1000) {
					FROGGERS.get(id).deltaTime = 0;
					levelTimer--;
				}

				if (levelTimer <= 0) {
					if(!FROGGERS.get(id).cheating) {
						try {
							fc.kill_frogger(id);
						} catch (IOException | TimeoutException e) {
							throw new RuntimeException(e);
						}
					}
				}


				if (GameLives < 1) {
					GameState = GAME_OVER;
				}

				break;
			}
		
		case GAME_OVER:		
		case GAME_INSTRUCTIONS:
		case GAME_INTRO:
			goalmanager.update(deltaMs);
			try {
				menuKeyboardHandler();
				//cycleTraffic(deltaMs);
			} catch (IOException | TimeoutException e) {
				throw new RuntimeException(e);
			}
			break;
			
		case GAME_FINISH_LEVEL:
			try {
				finishLevelKeyboardHandler();
			} catch (IOException | TimeoutException | InterruptedException e) {
				throw new RuntimeException(e);
			}
			break;		
		case GAME_BREAK:
			break;
		}
	}
	
	
	/**
	 * Rendering game objects
	 */
	public void render(RenderingContext rc) {
		switch(GameState) {
		case GAME_FINISH_LEVEL:
		case GAME_PLAY:
			backgroundLayer.render(rc);

			for (int i = 0; i < 4; i++) {
				if (FROGGERS.get(i).isAlive) {
					FROGGERS.get(i).render(rc);
					movingObjectsLayer.render(rc);
				} else {
					movingObjectsLayer.render(rc);
					FROGGERS.get(i).render(rc);
				}
			}

			particleLayer.render(rc);
			ui.render(rc);
			break;
			
		case GAME_OVER:
		case GAME_INSTRUCTIONS:
		case GAME_INTRO:
			backgroundLayer.render(rc);
			movingObjectsLayer.render(rc);
			ui.render(rc);
			break;		
		}
	}

	public void addFroggers() {
		for(int i = 0; i < 6; i++){
			FROGGER_START_ARRAY.add(i, new Vector2D((WORLD_WIDTH * (i / (double)5)),WORLD_HEIGHT-32));
			System.out.println("Array pos " + i + ": " + FROGGER_START_ARRAY.get(i));
		}

		for(int x = 0; x < 4; x++){
			FROGGERS.add(x, new Frogger(this,FROGGER_START_ARRAY.get(x+1), id));
			System.out.println("Frogger "+x+": "+FROGGERS.get(x).pos.getX());
		}

		frogCol = new FroggerCollisionDetection(FROGGERS.get(id));
		audiofx = new AudioEfx(frogCol,FROGGERS.get(id));

		//initializeLevel(1);
	}

	public void moveFroggers(int frogger, int dir) {
		switch(dir){
			case 0: FROGGERS.get(frogger).moveDown(); break;
			case 1: FROGGERS.get(frogger).moveUp(); break;
			case 2: FROGGERS.get(frogger).moveLeft(); break;
			case 3: FROGGERS.get(frogger).moveRight(); break;
		}
	}

	public void die(int frogger) {
		if(!FROGGERS.get(frogger).cheating) {
			FROGGERS.get(frogger).isAlive = false;
			FROGGERS.get(frogger).die(frogger);
		}
	}

	public void reset(int frogger) {
		FROGGERS.get(frogger).resetFrog();
	}

	public void start() throws RemoteException{
		enabled = true;
	}

	public void godMode(int id) {
		FROGGERS.get(id).cheating = true;
	}

	public void finished() {
		GameState = GAME_FINISH_LEVEL;
		audiofx.playCompleteLevel();
		particleLayer.clear();
	}

	public void nextLevel() {
		//if(building) {
			//building = false;
			GameState = GAME_PLAY;
			audiofx.playGameMusic();
			initializeLevel(++GameLevel);
		//}
	}

}