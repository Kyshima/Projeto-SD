package edu.ufp.inf.sd.rmi.project.server;

import java.io.Serializable;

public class Movement implements Serializable {
    public int FroggerNum;

    /**
     * Baixo - 0
     * Cima - 1
     * Esquerda - 2
     * Direita - 3
     */
    public int Direction;

    public int TotalDone;

    public Movement(int froggerNum, int direction, int totalDone) {
        FroggerNum = froggerNum;
        Direction = direction;
        TotalDone = totalDone;
    }

    public Movement(int froggerNum, int direction) {
        FroggerNum = froggerNum;
        Direction = direction;
        TotalDone = 0;
    }

    public int getTotalDone() {
        return TotalDone;
    }

    public void setTotalDone(int totalDone) {
        TotalDone = totalDone;
    }
}
