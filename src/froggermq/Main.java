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

import edu.ufp.inf.sd.rmi.project.client.FroggerClient;
import edu.ufp.inf.sd.rmi.project.client.ObserverImpl;
import edu.ufp.inf.sd.rmi.project.server.FroggerGameImpl;
import edu.ufp.inf.sd.rmi.project.server.Movement;
import edu.ufp.inf.sd.rmi.project.server.State;
import jig.engine.ImageResource;
import jig.engine.PaintableCanvas;
import jig.engine.PaintableCanvas.JIGSHAPE;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.util.Vector2D;

import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

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

	public static int froggerNum = 0;
	public int gameNum;

	private boolean enable = false;

    /**
	 * Initialize game objects
	 */
	public Main () throws RemoteException {

		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		gameNum = FroggerClient.froggerGameRI.listGames();
		System.out.println("Games: "+gameNum);

		String size = Integer.toString(FroggerGameImpl.observers.size());
		froggerNum = FroggerClient.froggerGameRI.mainServer(new ObserverImpl(size, FroggerClient.m, FroggerClient.froggerGame, gameNum));

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

		System.out.println("FroggerNUM: "+froggerNum);
		//initializeLevel(1);
	}

	public void initializeLevel(int level) throws RemoteException {
		if(FroggerClient.create == -1) {
			ArrayList<String> lines = new ArrayList<>();
			/* dV is the velocity multiplier for all moving objects at the current game level */
			double dV = level * 0.05 + 1;

			movingObjectsLayer.clear();
			ArrayList<Integer> rd = genRand();
			MovingEntityFactory.rands = rd;

			// Road Traffic
			roadLine1 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 8 * 32), new Vector2D(-0.1 * dV, 0));
			lines.add(0,MovEntToString(roadLine1));
			roadLine2 = new MovingEntityFactory(new Vector2D(-(32 * 4), 9 * 32), new Vector2D(0.08 * dV, 0));
			lines.add(1,MovEntToString(roadLine2));
			roadLine3 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 10 * 32), new Vector2D(-0.12 * dV, 0));
			lines.add(2,MovEntToString(roadLine3));
			roadLine4 = new MovingEntityFactory(new Vector2D(-(32 * 4), 11 * 32), new Vector2D(0.075 * dV, 0));
			lines.add(3,MovEntToString(roadLine4));
			roadLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 12 * 32), new Vector2D(-0.05 * dV, 0));
			lines.add(4,MovEntToString(roadLine5));

			// River Traffic
			riverLine1 = new MovingEntityFactory(new Vector2D(-(32 * 3), 2 * 32), new Vector2D(0.06 * dV, 0));
			lines.add(5,MovEntToString(riverLine1));
			riverLine2 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 3 * 32), new Vector2D(-0.04 * dV, 0));
			lines.add(6,MovEntToString(riverLine2));
			riverLine3 = new MovingEntityFactory(new Vector2D(-(32 * 3), 4 * 32), new Vector2D(0.09 * dV, 0));
			lines.add(7,MovEntToString(riverLine3));
			riverLine4 = new MovingEntityFactory(new Vector2D(-(32 * 4), 5 * 32), new Vector2D(0.045 * dV, 0));
			lines.add(8,MovEntToString(riverLine4));
			riverLine5 = new MovingEntityFactory(new Vector2D(Main.WORLD_WIDTH, 6 * 32), new Vector2D(-0.045 * dV, 0));
			lines.add(9,MovEntToString(riverLine5));

			goalmanager.init(level);
			for (Goal g : goalmanager.get()) {
				movingObjectsLayer.add(g);
			}

			State s = new State(lines);
			int size = FroggerClient.froggerGame.getObservers().size();
			//System.out.println("Ini 1 Size: " + size);
			for(int i=0;i<size;i++){
				if(FroggerGameImpl.observers.get(i)==null) size++;
				else{
					FroggerClient.froggerGameRI.update(gameNum, s);
					//FroggerClient.froggerGameRI.getState().rand = rd;
					//System.out.println("traffic " + FroggerClient.froggerGameRI.getState().getTraffic());
				}
			}

			/* Build some traffic before game starts buy running MovingEntityFactories for fews cycles */
			for (int i = 0; i < 500; i++)
				cycleTraffic(10);

		} else {
			State s = FroggerClient.froggerGameRI.getUpdate(gameNum);
			//System.out.println(FroggerClient.froggerGameRI.getState());
			//System.out.println(s.getTraffic());

			//MovingEntityFactory.rands = FroggerClient.froggerGameRI.getState().rand;
			ArrayList<String> lines = new ArrayList<>(s.getTraffic());

			roadLine1 = StringToMovEnt(lines.get(0));
			roadLine2 = StringToMovEnt(lines.get(1));
			roadLine3 = StringToMovEnt(lines.get(2));
			roadLine4 = StringToMovEnt(lines.get(3));
			roadLine5 = StringToMovEnt(lines.get(4));

			riverLine1 = StringToMovEnt(lines.get(5));
			riverLine2 = StringToMovEnt(lines.get(6));
			riverLine3 = StringToMovEnt(lines.get(7));
			riverLine4 = StringToMovEnt(lines.get(8));
			riverLine5 = StringToMovEnt(lines.get(9));

			cycleTraffic(10);
		}
	}

	public String MovEntToString(MovingEntityFactory mef){
		return mef.position.getX() + ";" + mef.position.getY() + ";" + mef.velocity.getX() + ";" + mef.velocity.getY() + ";" + mef.time;
	}

	public MovingEntityFactory StringToMovEnt(String string){
		double xpos, ypos, xvel, yvel;
		long time;
		String[] novo = string.split(";");
		xpos = Double.parseDouble(novo[0]);
		ypos = Double.parseDouble(novo[1]);
		xvel = Double.parseDouble(novo[2]);
		yvel = Double.parseDouble(novo[3]);
		time = Long.parseLong(novo[4]);

		return new MovingEntityFactory(new Vector2D(xpos,ypos), new Vector2D(xvel, yvel), time);
	}

	public ArrayList<Integer> genRand(){
		Random r = new Random();
		ArrayList<Integer> genRand = new ArrayList<>();
		genRand.add(0,r.nextInt(100));
		genRand.add(0,r.nextInt(3));
		genRand.add(0,r.nextInt(100));
		genRand.add(0,r.nextInt(2));
		genRand.add(0,r.nextInt(100));
		genRand.add(0,r.nextInt(100));
		return genRand;
	}


	/**
	 * Populate movingObjectLayer with a cycle of cars/trucks, moving tree logs, etc
	 *
	 * @param deltaMs
	 */
	public void cycleTraffic(long deltaMs) throws RemoteException, NullPointerException {
		MovingEntity m;
		if(FroggerClient.create == -1){
			ArrayList<String> up = new ArrayList<>();
			/* Road traffic updates */
			roadLine1.update(deltaMs);
			if ((m = roadLine1.buildVehicle()) != null) movingObjectsLayer.add(m);
			up.add(0,UpdateToString(roadLine1));
			roadLine2.update(deltaMs);
			if ((m = roadLine2.buildVehicle()) != null) movingObjectsLayer.add(m);
			up.add(1,UpdateToString(roadLine2));
			roadLine3.update(deltaMs);
			if ((m = roadLine3.buildVehicle()) != null) movingObjectsLayer.add(m);
			up.add(2,UpdateToString(roadLine3));
			roadLine4.update(deltaMs);
			if ((m = roadLine4.buildVehicle()) != null) movingObjectsLayer.add(m);
			up.add(3,UpdateToString(roadLine4));
			roadLine5.update(deltaMs);
			if ((m = roadLine5.buildVehicle()) != null) movingObjectsLayer.add(m);
			up.add(4,UpdateToString(roadLine5));

			/* River traffic updates */
			riverLine1.update(deltaMs);
			if ((m = riverLine1.buildShortLogWithTurtles(40)) != null) movingObjectsLayer.add(m);
			up.add(5,UpdateToString(riverLine1));
			riverLine2.update(deltaMs);
			if ((m = riverLine2.buildLongLogWithCrocodile(30)) != null) movingObjectsLayer.add(m);
			up.add(6,UpdateToString(riverLine2));
			riverLine3.update(deltaMs);
			if ((m = riverLine3.buildShortLogWithTurtles(50)) != null) movingObjectsLayer.add(m);
			up.add(7,UpdateToString(riverLine3));
			riverLine4.update(deltaMs);
			if ((m = riverLine4.buildLongLogWithCrocodile(20)) != null) movingObjectsLayer.add(m);
			up.add(8,UpdateToString(riverLine4));
			riverLine5.update(deltaMs);
			if ((m = riverLine5.buildShortLogWithTurtles(10)) != null) movingObjectsLayer.add(m);
			up.add(9,UpdateToString(riverLine5));

			int size = FroggerClient.froggerGame.getObservers().size();
			for(int i=0;i<size;i++){
				if(FroggerGameImpl.observers.get(i)==null) size++;
				else{
					State s = new State(FroggerClient.froggerGameRI.getUpdate(gameNum).getTraffic(), up);
					FroggerClient.froggerGameRI.update(gameNum,s);
					//System.out.println(FroggerClient.froggerGameRI.getState().getUpdate());
				}
			}
		} else {
			//System.out.println(FroggerClient.froggerGameRI.getState().getTraffic());
			State s = FroggerClient.froggerGameRI.getUpdate(gameNum);
			//System.out.println(FroggerClient.froggerGameRI.getState().getUpdate());
			//System.out.println(s.getTraffic());
			ArrayList<String> up = new ArrayList<>(s.getUpdate());
			//System.out.println(s.getUpdate());
			StringToUpdate(roadLine1,up.get(0));
			roadLine1.update(deltaMs);
			if ((m = roadLine1.buildVehicle()) != null) movingObjectsLayer.add(m);
			StringToUpdate(roadLine2,up.get(1));
			roadLine2.update(deltaMs);
			if ((m = roadLine2.buildVehicle()) != null) movingObjectsLayer.add(m);
			StringToUpdate(roadLine3,up.get(2));
			roadLine3.update(deltaMs);
			if ((m = roadLine3.buildVehicle()) != null) movingObjectsLayer.add(m);
			StringToUpdate(roadLine4,up.get(3));
			roadLine4.update(deltaMs);
			if ((m = roadLine4.buildVehicle()) != null) movingObjectsLayer.add(m);
			StringToUpdate(roadLine5,up.get(4));
			roadLine5.update(deltaMs);
			if ((m = roadLine5.buildVehicle()) != null) movingObjectsLayer.add(m);

			StringToUpdate(riverLine1,up.get(5));
			riverLine1.update(deltaMs);
			if ((m = riverLine1.buildShortLogWithTurtles(40)) != null) movingObjectsLayer.add(m);
			StringToUpdate(riverLine2,up.get(6));
			riverLine2.update(deltaMs);
			if ((m = riverLine2.buildLongLogWithCrocodile(30)) != null) movingObjectsLayer.add(m);
			StringToUpdate(riverLine3,up.get(7));
			riverLine3.update(deltaMs);
			if ((m = riverLine3.buildShortLogWithTurtles(50)) != null) movingObjectsLayer.add(m);
			StringToUpdate(riverLine4,up.get(8));
			riverLine4.update(deltaMs);
			if ((m = riverLine4.buildLongLogWithCrocodile(20)) != null) movingObjectsLayer.add(m);
			StringToUpdate(riverLine5,up.get(9));
			riverLine5.update(deltaMs);
			if ((m = riverLine5.buildShortLogWithTurtles(10)) != null) movingObjectsLayer.add(m);
		}

	    // Do Wind
	    if ((m = wind.genParticles(GameLevel)) != null) particleLayer.add(m);

	    // HeatWave
		for (int i = 0; i < FroggerClient.froggerGameRI.getObservers().size(); i++){
			if ((m = hwave.genParticles(FROGGERS.get(i).getCenterPosition())) != null) particleLayer.add(m);
		}

	    movingObjectsLayer.update(deltaMs);
	    particleLayer.update(deltaMs);
	}

	public String UpdateToString(MovingEntityFactory mef){
		return mef.updateMs + ";" + mef.copCarDelay;
	}

	public void StringToUpdate(MovingEntityFactory mef, String string) {
		long up, cop;
		String[] novo = string.split(";", 0);
		up = Long.parseLong(novo[0]);
		cop = Long.parseLong(novo[1]);

		mef.update(up,cop);
	}

	/**
	 * Handling Frogger movement from keyboard input
	 */
	public void froggerKeyboardHandler() throws RemoteException {
 		keyboard.poll();

 		boolean keyReleased = false;
        boolean downPressed = keyboard.isPressed(KeyEvent.VK_DOWN);
        boolean upPressed = keyboard.isPressed(KeyEvent.VK_UP);
		boolean leftPressed = keyboard.isPressed(KeyEvent.VK_LEFT);
		boolean rightPressed = keyboard.isPressed(KeyEvent.VK_RIGHT);

		// Enable/Disable cheating
		if (keyboard.isPressed(KeyEvent.VK_C))
			FROGGERS.get(froggerNum).cheating = true;
		if (keyboard.isPressed(KeyEvent.VK_V))
			FROGGERS.get(froggerNum).cheating = false;
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
			int froggers = FroggerClient.froggerGameRI.getObservers().size();
		    if (downPressed) {
				//FroggerClient.froggerGameRI.getState().mov.add(FroggerClient.froggerGameRI.getState().mov.size(),new Movement(froggerNum,3));
				Movement m = new Movement(froggerNum,0,0);
				ArrayList<Movement> arr;
				if(!FroggerClient.froggerGameRI.getUpdate(gameNum).mov.isEmpty()) {
					arr = new ArrayList<>(FroggerClient.froggerGameRI.getUpdate(gameNum).mov);
					arr.add(m);
				} else {
					arr = new ArrayList<>();
					arr.add(m);
				}
				State s = new State(FroggerClient.froggerGameRI.getUpdate(gameNum).getTraffic(),FroggerClient.froggerGameRI.getUpdate(gameNum).getUpdate(),arr);
				FroggerClient.froggerGameRI.update(gameNum, s);
			}
		    if (upPressed) {
				Movement m = new Movement(froggerNum,1,0);
				ArrayList<Movement> arr;
				if(!FroggerClient.froggerGameRI.getUpdate(gameNum).mov.isEmpty()) {
					arr = new ArrayList<>(FroggerClient.froggerGameRI.getUpdate(gameNum).mov);
					arr.add(m);
				} else {
					arr = new ArrayList<>();
					arr.add(m);
				}
				State s = new State(FroggerClient.froggerGameRI.getUpdate(gameNum).getTraffic(),FroggerClient.froggerGameRI.getUpdate(gameNum).getUpdate(),arr);
				FroggerClient.froggerGameRI.update(gameNum, s);
			}
		    if (leftPressed) {
				Movement m = new Movement(froggerNum,2,0);
				ArrayList<Movement> arr;
				if(!FroggerClient.froggerGameRI.getUpdate(gameNum).mov.isEmpty()) {
					arr = new ArrayList<>(FroggerClient.froggerGameRI.getUpdate(gameNum).mov);
					arr.add(m);
				} else {
					arr = new ArrayList<>();
					arr.add(m);
				}
				State s = new State(FroggerClient.froggerGameRI.getUpdate(gameNum).getTraffic(),FroggerClient.froggerGameRI.getUpdate(gameNum).getUpdate(),arr);
				FroggerClient.froggerGameRI.update(gameNum, s);
			}
	 	    if (rightPressed) {
				Movement m = new Movement(froggerNum,3,0);
				ArrayList<Movement> arr;
				if(!FroggerClient.froggerGameRI.getUpdate(gameNum).mov.isEmpty()) {
					arr = new ArrayList<>(FroggerClient.froggerGameRI.getUpdate(gameNum).mov);
					arr.add(m);
				} else {
					arr = new ArrayList<>();
					arr.add(m);
				}
				State s = new State(FroggerClient.froggerGameRI.getUpdate(gameNum).getTraffic(),FroggerClient.froggerGameRI.getUpdate(gameNum).getUpdate(),arr);
				FroggerClient.froggerGameRI.update(gameNum, s);
			}

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
	public void menuKeyboardHandler() throws RemoteException {
		keyboard.poll();

		// Following 2 if statements allow capture space bar key strokes
		if (!keyboard.isPressed(KeyEvent.VK_SPACE)) {
			space_has_been_released = true;
		}

		if (!space_has_been_released)
			return;

		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			//System.out.println("AQUI");
			if(FroggerClient.froggerGameRI.getObservers().size() > 1){
				//System.out.println("Aqui 1");
				addFroggers();
				//System.out.println("Aqui 2");
				enable = true;
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
						//System.out.println("Aqui 3");
						for (int i = 0; i < FroggerClient.froggerGameRI.getObservers().size(); i++) {
							//System.out.println(FROGGER_START_ARRAY.get(i).toString());
							FROGGERS.get(froggerNum).setPosition(FROGGER_START_ARRAY.get(froggerNum+1));
						}
						//System.out.println("Aqui 4");
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
	public void finishLevelKeyboardHandler() throws RemoteException {
		keyboard.poll();
		if (keyboard.isPressed(KeyEvent.VK_SPACE)) {
			GameState = GAME_PLAY;
			audiofx.playGameMusic();
			initializeLevel(++GameLevel);
		}
	}

	/**
	 * w00t
	 */
	public void update(long deltaMs) {
			int c = FroggerClient.create;

		if(enable) {
			try {
				if (FroggerClient.froggerGameRI.getUpdate(gameNum).mov.size()>0) {
					System.out.println(FroggerClient.froggerGameRI.getUpdate(gameNum).mov.size());
					moveFroggers();
				}
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
		}

			switch (GameState) {
					case GAME_PLAY:
						if(enable) {
						try {
							froggerKeyboardHandler();
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
						wind.update(deltaMs);
						hwave.update(deltaMs);
						try{
							for (int i = 0; i < FroggerClient.froggerGameRI.getObservers().size(); i++) {
								FROGGERS.get(i).update(deltaMs);
							}
						}catch(RemoteException ignored){}
						audiofx.update(deltaMs);
						ui.update(deltaMs);

						try {
							cycleTraffic(deltaMs);
						} catch (RemoteException e) {
							throw new RuntimeException(e);
						}
						frogCol.testCollision(movingObjectsLayer);

						// Wind gusts work only when Frogger is on the river
						if (frogCol.isInRiver())
							wind.start(GameLevel);

						try{
							for (int i = 0; i < FroggerClient.froggerGameRI.getObservers().size(); i++) {
								wind.perform(FROGGERS.get(i), GameLevel, deltaMs);

								// Do the heat wave only when Frogger is on hot pavement
								if (frogCol.isOnRoad())
									hwave.start(FROGGERS.get(i), GameLevel);
								hwave.perform(FROGGERS.get(i), deltaMs, GameLevel);


								if (!FROGGERS.get(i).isAlive)
									particleLayer.clear();
							}
						}catch(RemoteException ignored){}

						goalmanager.update(deltaMs);

						if (goalmanager.getUnreached().size() == 0) {
							GameState = GAME_FINISH_LEVEL;
							audiofx.playCompleteLevel();
							particleLayer.clear();
						}

						FROGGERS.get(froggerNum).deltaTime += deltaMs;
						if (FROGGERS.get(froggerNum).deltaTime > 1000) {
							FROGGERS.get(froggerNum).deltaTime = 0;
							levelTimer--;
						}

						if (levelTimer <= 0)
							FROGGERS.get(froggerNum).die();

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
					} catch (RemoteException e) {
						throw new RuntimeException(e);
					}
					break;

				case GAME_FINISH_LEVEL:
					try {
						finishLevelKeyboardHandler();
					} catch (RemoteException e) {
						throw new RuntimeException(e);
					}
					break;
			}
	}

	private void moveFroggers() throws RemoteException {
		State s = FroggerClient.froggerGameRI.getUpdate(gameNum);
		int temp = s.mov.size()-1;
		Movement m = s.mov.get(temp);
		int frogNum = m.FroggerNum;
		int dir = m.Direction;
		int total = m.TotalDone+1;
		System.out.println("FN: "+frogNum+" D: "+dir+" T: "+total);

		switch(dir){
			case 0: FROGGERS.get(frogNum).moveDown(); break;
			case 1: FROGGERS.get(frogNum).moveUp(); break;
			case 2: FROGGERS.get(frogNum).moveLeft(); break;
			case 3: FROGGERS.get(frogNum).moveRight(); break;
		}

		s.mov.get(temp).setTotalDone(total);
		//System.out.println("S:"+ s.mov.get(0).TotalDone+" C:"+total);
		if(s.mov.get(temp).TotalDone == FroggerClient.froggerGameRI.getObservers().size()){
			System.out.println("eliminou");
			s.mov.remove(temp);
		}

		FroggerClient.froggerGameRI.update(gameNum, s);
	}


	/**
	 * Rendering game objects
	 */
	public void render(RenderingContext rc) {
		switch(GameState) {
		case GAME_FINISH_LEVEL:
		case GAME_PLAY:
			backgroundLayer.render(rc);

			try {
				for (int i = 0; i < FroggerClient.froggerGameRI.getObservers().size(); i++) {
					if (FROGGERS.get(i).isAlive) {
						movingObjectsLayer.render(rc);
						//frog.collisionObjects.get(0).render(rc);
						FROGGERS.get(i).render(rc);
					} else {
						FROGGERS.get(i).render(rc);
						movingObjectsLayer.render(rc);
					}
				}
			} catch (RemoteException ignored){}

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

	public AbstractBodyLayer<MovingEntity> getMovingObjectsLayer() {
		return movingObjectsLayer;
	}

	public void setMovingObjectsLayer(AbstractBodyLayer<MovingEntity> movingObjectsLayer) {
		Main.movingObjectsLayer = movingObjectsLayer;
	}

	public void addFroggers() throws RemoteException {
		for(int i = 0; i< FroggerClient.froggerGameRI.getObservers().size() + 2; i++){
			FROGGER_START_ARRAY.add(i, new Vector2D((WORLD_WIDTH * (i / (double)(FroggerClient.froggerGameRI.getObservers().size()+1))),WORLD_HEIGHT-32));
			System.out.println("Array pos " + i + ": " + FROGGER_START_ARRAY.get(i));
		}

		for(int x = 0; x < FroggerClient.froggerGameRI.getObservers().size(); x++){
			FROGGERS.add(x, new Frogger(this,FROGGER_START_ARRAY.get(x+1)));
			System.out.println("Frogger "+x+": "+FROGGERS.get(x).pos.getX());
		}

		frogCol = new FroggerCollisionDetection(FROGGERS.get(froggerNum));
		audiofx = new AudioEfx(frogCol,FROGGERS.get(froggerNum));

		//initializeLevel(1);
	}
}
