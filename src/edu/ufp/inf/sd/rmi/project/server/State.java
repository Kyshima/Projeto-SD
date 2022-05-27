/**
 * <p>Title: Projecto SD</p>
 * <p>Description: Projecto apoio aulas SD</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: UFP </p>
 * @author Rui Moreira
 * @version 2.0
 */
package edu.ufp.inf.sd.rmi.project.server;

import frogger.MovingEntityFactory;

import java.io.Serializable;

/**
 * 
 * @author rui
 */
public class State implements Serializable {
    private MovingEntityFactory[] moving;

    public State(MovingEntityFactory[] moving) {
        this.moving = moving;
    }

    public MovingEntityFactory[] getMoving() {
        return moving;
    }

    public void setMoving(MovingEntityFactory[] moving) {
        this.moving = moving;
    }
}
