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
import frogger.MovingEntityFactory;
import jig.engine.physics.AbstractBodyLayer;

import java.io.Serializable;

/**
 * 
 * @author rui
 */
public class State implements Serializable {
    public static AbstractBodyLayer<MovingEntity> traffic;

    public State(AbstractBodyLayer<MovingEntity> t){
        traffic = t;
    }

    public  AbstractBodyLayer<MovingEntity> getTraffic() {
        return traffic;
    }

    public  void setTraffic(AbstractBodyLayer<MovingEntity> traffic) {
        State.traffic = traffic;
    }


}
