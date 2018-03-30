package sample;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

public class Game implements Pieces{
    public Square[][] board;
    public Player[] players;

    ArrayList<Square> attackedByBlack;
    ArrayList<Square> attackedByWhite;

    Game(Player[] players){
        this.players = players;
        attackedByBlack = new ArrayList<>();
        attackedByWhite = new ArrayList<>();
        board = new Square[8][8];
        for (int i =0; i < 8; i++){
            for (int j =0; j < 8; j++){
                Square sqr = new Square(i, j);

                if ((i%2 == 0 && j%2==0)||(i%2 != 0 && j%2 != 0)){
                    sqr.color = "White";
                }else {
                    sqr.color = "Black";
                }

                board[i][j] = sqr;
            }
        }
        startBoard(this.players);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 2; j++) {
                if (players[0].color == "Black"){
                    players[0].myPieces.add(board[j][i].pieceOnMe);
                } else {
                    players[1].myPieces.add(board[j][i].pieceOnMe);
                }

            }
        }
        for (int i = 0; i < 8; i++) {
            for (int j = 6; j < 8; j++) {
                if (players[0].color == "White"){
                    players[0].myPieces.add(board[j][i].pieceOnMe);
                } else {
                    players[1].myPieces.add(board[j][i].pieceOnMe);
                }

            }
        }
    }
    // move is not piece based
    // leaving it in the driver class Game
    public void movePiece(Player player, Piece piece, int rank, int file){
        // check that the square clicked has a piece
        if  (piece != null){
            // check that it is your turn
            if (player.myTurn){
                if (player.color.equals(piece.color)){
                    if (piece.legalMoves.contains(board[rank][file])){
                        // make move
                        Piece taken;
                        // check if en passent
                        if (piece.pieceName.equals("P") && file != piece.currFile && board[rank][file].isVacant){
                            if (piece.color.equals("Black")){
                                taken = board[rank - 1][file].pieceOnMe;
                            } else {
                                taken = board[rank + 1][file].pieceOnMe;
                            }
                        } else {
                            taken = board[rank][file].pieceOnMe;
                        }
                        board[rank][file].pieceOnMe = piece;
                        board[rank][file].isVacant = false;
                        board[piece.currRank][piece.currFile].pieceOnMe = null;
                        board[piece.currRank][piece.currFile].isVacant = true;
                        int oldRank = piece.currRank;
                        int oldFile = piece.currFile;
                        piece.currRank = rank;
                        piece.currFile = file;
                        // check if king is in check after move
                        this.accept(new ChessBoardMoveVisitor(), this, player);

                        if (player.inCheck){
                            // revert to previous position
                            piece.currRank = oldRank;
                            piece.currFile = oldFile;

                            board[piece.currRank][piece.currFile].pieceOnMe = piece;
                            board[piece.currRank][piece.currFile].isVacant = false;

                            if (taken != null){
                                board[taken.currRank][taken.currFile].pieceOnMe = taken;
                            } else {
                                board[rank][file].pieceOnMe = null;
                                board[rank][file].isVacant = true;
                            }

                            // TODO alert
                            System.out.println("Not Legal Move");
                        } else {
                            piece.hasMoved = true;
                            piece.lastMove = abs(oldRank - piece.currRank);
                            if (taken !=  null) {
                                player.piecesCaptured.add(taken);
                                this.getOpponent(piece.color).myPieces.remove(taken);
                            }

                            // not my turn anymore
                            player.myTurn = false;
                            // other guy's turn
                            this.getOpponent(piece.color).myTurn = true;

                            // TODO update server side board
                        }


                        // TODO re-render board()

                    } else {
                        // TODO alert
                        System.out.println("Not Legal Move");
                    }
                } else {
                    // TODO alert
                    System.out.println("not your color");
                }

            } else {
                // TODO alert
                System.out.println("not " + player.name + "'s turn");
            }

        } else {
            // TODO alert
            System.out.println("no piece there");
        }
    }


    public void startBoard(Player[] players){
        // randomly assign one player as white
        int rand = new Random().nextInt(2);
        players[rand].color = "White";
        players[rand].myTurn = true;

        // populate board
        // black starts at top of board
        board[0][0].pieceOnMe = new Rook(0,0,"Black");
        board[0][1].pieceOnMe = new Knight(0,1, "Black");
        board[0][2].pieceOnMe = new Bishop(0,2, "Black");
        board[0][3].pieceOnMe = new Queen(0,3, "Black");
        board[0][4].pieceOnMe = new King(0,4, "Black");
        board[0][5].pieceOnMe = new Bishop(0,5, "Black");
        board[0][6].pieceOnMe = new Knight(0,6, "Black");
        board[0][7].pieceOnMe = new Rook(0,7, "Black");

        board[0][0].isVacant = false;
        board[0][1].isVacant = false;
        board[0][2].isVacant = false;
        board[0][3].isVacant = false;
        board[0][4].isVacant = false;
        board[0][5].isVacant = false;
        board[0][6].isVacant = false;
        board[0][7].isVacant = false;

        //pawns
        for(int i=0;i<8;i++){
            board[1][i].pieceOnMe = new Pawn(1,i, "Black");
            board[1][i].isVacant = false;
        }

        // white at bottom
        board[7][0].pieceOnMe = new Rook(7,0, "White");
        board[7][1].pieceOnMe = new Knight(7,1, "White");
        board[7][2].pieceOnMe = new Bishop(7,2, "White");
        board[7][3].pieceOnMe = new Queen(7,3, "White");
        board[7][4].pieceOnMe = new King(7,4, "White");
        board[7][5].pieceOnMe = new Bishop(7,5, "White");
        board[7][6].pieceOnMe = new Knight(7,6, "White");
        board[7][7].pieceOnMe = new Rook(7,7, "White");

        board[7][0].isVacant = false;
        board[7][1].isVacant = false;
        board[7][2].isVacant = false;
        board[7][3].isVacant = false;
        board[7][4].isVacant = false;
        board[7][5].isVacant = false;
        board[7][6].isVacant = false;
        board[7][7].isVacant = false;

        //pawns
        for(int i=0;i<8;i++){
            board[6][i].pieceOnMe = new Pawn(6,i, "White");
            board[6][i].isVacant = false;
        }

        for (Player p : players){
            if (p.color.equals("White")){
                p.myKing = board[7][4].pieceOnMe;
            } else {
                p.myKing = board[0][4].pieceOnMe;
            }
        }

    }

    void printBoard(){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].pieceOnMe != null){
                    System.out.print(board[i][j].pieceOnMe.pieceName + "\t");
                } else{
                    System.out.print("O\t");
                }

            }
            System.out.print('\n');
        }
        System.out.print('\n');
    }

    public void attack(Square square, String attacker){
        if (attacker.equals("White")){
            if (!attackedByWhite.contains(square)){
                attackedByWhite.add(square);
            }
        } else {
            if (!attackedByBlack.contains(square)){
                attackedByBlack.add(square);
            }
        }
    }

    public ArrayList<Square> getOpponentTargets(String myColor){
        if (myColor.equals("White")){
            return attackedByBlack;
        } else {
            return attackedByWhite;
        }
    }

    Player getOpponent(String color){
        if (players[0].color.equals(color)){
            return players[1];
        } else {
            return players[0];
        }
    }

    Player getPlayer(String color){
        if (players[0].color.equals(color)){
            return players[0];
        } else {
            return players[1];
        }
    }


    @Override
    public void accept(PieceVisitor pieceVisitor, Game game, Player player){
        pieceVisitor.visit(this, player);

        player.accept(pieceVisitor, game, player);
    }

    public boolean checkTempMove(Player player, Piece piece, int rank, int file){
        // make move
        boolean check = true;
        Piece taken;
        // check if en passent
        if (piece.pieceName.equals("P") && file != piece.currFile && board[rank][file].isVacant){
            if (piece.color.equals("Black")){
                taken = board[rank - 1][file].pieceOnMe;
            } else {
                taken = board[rank + 1][file].pieceOnMe;
            }
        } else {
            taken = board[rank][file].pieceOnMe;
        }
        board[rank][file].pieceOnMe = piece;
        board[rank][file].isVacant = false;
        board[piece.currRank][piece.currFile].pieceOnMe = null;
        board[piece.currRank][piece.currFile].isVacant = true;
        int oldRank = piece.currRank;
        int oldFile = piece.currFile;
        piece.currRank = rank;
        piece.currFile = file;
        // check if king is in check after move
        this.accept(new ChessBoardMoveVisitor(), this, getOpponent(piece.color));

        if (player.inCheck){
            check = false;
        }
        // revert to previous position
        piece.currRank = oldRank;
        piece.currFile = oldFile;
        board[piece.currRank][piece.currFile].pieceOnMe = piece;
        board[piece.currRank][piece.currFile].isVacant = false;

        if (taken != null){
            board[taken.currRank][taken.currFile].pieceOnMe = taken;
        } else {
            board[rank][file].pieceOnMe = null;
            board[rank][file].isVacant = true;
        }

        return check;
    }
}
