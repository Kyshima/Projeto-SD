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
    public ArrayList<String> traffic;
    public ArrayList<String> update;

    public State(ArrayList<String> t){
        traffic = t;
    }

    public ArrayList<String> getTraffic() { return traffic; }
    public void setTraffic(ArrayList<String> t) { traffic = t; }

    public ArrayList<String> getUpdate() { return update; }

    public void setUpdate(ArrayList<String> update) { this.update = update; }
}
