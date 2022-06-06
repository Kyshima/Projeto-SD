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

/**
 * 
 * @author rui
 */
public class State implements Serializable {
    public ArrayList<Movement> mov;

    public State(){
        mov = new ArrayList<>();
    }

    public State(ArrayList<Movement> mov) {
        this.mov = mov;
    }

    public ArrayList<Movement> getMov() { return mov; }
    public void setMov(ArrayList<Movement> mov) { this.mov = mov; }
}
