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
    public static AbstractBodyLayer<MovingEntity> traffic;
    public ArrayList<String> Teste;

    public State(AbstractBodyLayer<MovingEntity> t){
        traffic = t;
    }
    public State(ArrayList<String> t){
        Teste = t;
    }

    public  AbstractBodyLayer<MovingEntity> getTraffic() {
        return traffic;
    }
    public  void setTraffic(AbstractBodyLayer<MovingEntity> t) {
        traffic = t;
    }

    public ArrayList<String> getTeste() { return Teste; }
    public void setTeste(ArrayList<String> teste) { Teste = teste; }
    public void addTeste(String teste) { Teste.add(teste); }
}
