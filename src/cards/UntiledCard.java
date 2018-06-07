package cards;

import java.util.Random;

public class UntiledCard {

    private Vector[][] vectors;
    private int row;
    private int col;
    private Random rand = new Random();

    public UntiledCard(int row, int col){
        this.row = row;
        this.col = col;
        this.vectors = new Vector[row][col];
        rand.setSeed(System.currentTimeMillis());
    }

    public void fillCard(){
        for(int i = 0; i < row; i++){
            int X = 0;
            int Y = 0;
            for(int j = 0; j < col; j++){
                //int X = rand.nextInt(21)-10;
                //int Y = rand.nextInt(21)-10;;
                 vectors[i][j] = new Vector(++X,++Y);
            }
        }
    }

    public Vector[][] getVectors() {
        return vectors;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < vectors.length; i++){
            for(int j = 0; j < vectors[i].length; j++) builder.append(vectors[i][j].toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
