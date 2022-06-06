/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rmi.project.server;

import frogger.MovingEntity;
import jig.engine.physics.AbstractBodyLayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author rui
 */
public class State implements Serializable {
    public ArrayList<Movement> mov;
    public List<Boolean> alive = new ArrayList<Boolean>(Arrays.asList(new Boolean[10]));
    public int unreached;

    public State(){
        mov = new ArrayList<>();
        Collections.fill(alive, Boolean.TRUE);
        unreached = 0;
    }

    public State(ArrayList<Movement> mov, List<Boolean> alive, int u) {
        this.mov = mov;
        this.alive = alive;
        this.unreached = u;
    }
}
