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

package frogger;
import edu.ufp.inf.sd.rmi.project.client.FroggerClient;
import edu.ufp.inf.sd.rmi.project.server.FroggerGameRI;
import edu.ufp.inf.sd.rmi.project.server.State;
import jig.engine.util.Vector2D;

import java.rmi.RemoteException;
import java.util.List;

public class Goal extends MovingEntity {

	public static Main g;
	public boolean isReached = false;
	public boolean isBonus = false;
	
	public Goal(int loc) {
		super(Main.SPRITE_SHEET + "#goal");
		position = new Vector2D(32*(1+2*loc), 32);
		collisionObjects.add(new CollisionObject("colSmall", position));
		sync(position);
		setFrame(0);
	}

	public Goal(Vector2D pos) {
		super(Main.SPRITE_SHEET + "#goal");
		position = pos;
		collisionObjects.add(new CollisionObject("colSmall", position));
		sync(position);
		setFrame(0);		
	}
	
	public void reached() throws RemoteException {
		isReached = true;
		FroggerGameRI fr = FroggerClient.froggerGameRI;
		int num = g.gameNum;
		State os = fr.getUpdate(num);
		List<Boolean> b = os.alive;
		int r = FroggerClient.froggerGameRI.getUpdate(g.gameNum).unreached;
		State s = new State(FroggerClient.froggerGameRI.getUpdate(g.gameNum).mov, b, r - 1);
		FroggerClient.froggerGameRI.update(g.gameNum, s);
		setFrame(1);
	}
	
	public void setBonus(boolean b) {
		if (b) {
			isBonus = true;
			setFrame(2);
		} else {
			isBonus = false;
			setFrame(0);
		}
	}
	
	public void update(long deltaMs) {
		;
	}
}