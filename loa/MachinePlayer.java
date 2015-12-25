
package loa;

import java.util.HashSet;
import java.util.Iterator;

/** An automated Player.
 *  @author Peter Lee */
class MachinePlayer extends Player {

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    /** The depth of my AI. */
    private final int setDepth = 4;

    /** Number to subtract. */
    private final int subNum = 10000;

    /** Number representation of the estimated capacity of the hash set. */
    private final int hashSetCapacity = 16;

    @Override
    Move makeMove() {
        /** A move made by the AI. */
        findBestMove(getBoard(), setDepth, Integer.MAX_VALUE - subNum);
        noSameMoves.clear();
        if (noSameMovesFinal.size() > hashSetCapacity) {
            noSameMovesFinal.clear();
        }
        noSameMovesFinal.add(_storeMove);
        System.out.println(side().abbrev().toUpperCase() + "::" + _storeMove);
        return _storeMove;
    }

    /** Field for the stored moves. */
    private Move _storeMove;

    /** Returns the move that gives the best move possible in the game.
     * @param board the game board
     * @param depth the height of the search
     * @param cutoff point where to end
     * @return move the best move */
    private int findBestMove(Board board,
                             int depth, double cutoff) {
        if (depth == 0) {
            return guessBestMove(board, cutoff);
        }
        Iterator<Move> boardIter = board.iterator();
        Move bestSofar;
        bestSofar = null;
        int move = Integer.MIN_VALUE;
        while (boardIter.hasNext()) {
            Move nextMove = boardIter.next();
            if (noSameMoves.contains(nextMove)) {
                continue;
            }
            noSameMoves.add(nextMove);
            board.makeMove(nextMove);
            if (depth == setDepth
                    && board.piecesContiguous(board.turn().opposite()) == 1) {
                bestSofar = nextMove;
                board.retract();
                move = Integer.MAX_VALUE;
                break;
            }
            int response;
            response = -findBestMove(board,
                        depth - 1, -move);
            board.retract();
            if (noSameMovesFinal.contains(nextMove)) {
                continue;
            }
            if (response > move) {
                bestSofar = nextMove;
                move = response;
                if (response >= cutoff) {
                    break;
                }
            }
        }
        _storeMove = bestSofar;
        return move;
    }

    /** Searches for the best value.
     * @param board the game board
     * @param cutoff point where to end
     * @return move integer of the best move*/
    private int guessBestMove(Board board, double cutoff) {
        Iterator<Move> boardIter = board.iterator();
        Move bestSoFar = null;
        int move = Integer.MIN_VALUE;
        while (boardIter.hasNext()) {
            Move nextMove = boardIter.next();
            if (noSameMoves.contains(nextMove)) {
                continue;
            }
            noSameMoves.add(nextMove);
            board.makeMove(nextMove);
            int moveVal = eval(board);
            board.retract();
            if (moveVal > move) {
                bestSoFar = nextMove;
                move = moveVal;
                if (moveVal >= cutoff) {
                    break;
                }
            }
        }
        _storeMove = bestSoFar;
        return move;
    }

    /** Evaluation function for the board.
     * @param board the current state of the game
     * @return counter the number evaluation of the board */
    private  int eval(Board board) {
        int counter = 0;
        if (board.piecesContiguous(board.turn()) == 1) {
            return Integer.MIN_VALUE;
        }
        for (int i = 1; isBounds(i); i++) {
            for (int j = 1; isBounds(j); j++) {
                if ((board.get(i, j) == board.turn())) {
                    int current = board.piecesContiguous(board.turn());
                    if (current > counter) {
                        counter = current;
                    }
                } else if (board.get(i, j) == board.turn().opposite()) {
                    int nextCurrent =
                            board.piecesContiguous(board.turn().opposite());
                    if (nextCurrent > counter) {
                        counter = nextCurrent;
                    }
                }
            }
        }
        return counter;
    }

    /** Checks if rows and columns are in bound of the board.
     * @param i the legal bounds within bounds
     * @return i integer of the legal bounds */
    private boolean isBounds(int i) {
        return i >= 1 && i <= 8;
    }

    /** Hash set that ensures no repeats of moves. */
    private HashSet<Move> noSameMoves = new HashSet<Move>();
    /** Hash set that ensures no repeats of final moves in the game. */
    private HashSet<Move> noSameMovesFinal = new HashSet<Move>();
}


