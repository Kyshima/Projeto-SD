package edu.ufp.inf.sd.rabbitmqservices.project.consumer;

public class Info {
    public static final int [][] games = new int[10][4];

    public static void main() {
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 4; j++)
            {
                games[i][j] = 0;
            }
        }
    }
}
