package sample;

import java.util.ArrayList;

public class Knight extends Piece{
    Knight(int rank, int file){
        worth = 3;
        pieceName = "N";
        currRank = rank;
        currFile = file;
    }
    ArrayList<Square> getLegalMoves(Game game){
        ArrayList<Square> movesList = new ArrayList<>();
        // forward move

        return movesList;
    }
}
