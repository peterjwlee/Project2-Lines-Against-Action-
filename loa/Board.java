
package loa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Formatter;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Direction.*;

/** Represents the state of a game of Lines of Action.
 *  @author Peter Lee
 */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;

    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** Directions array that holds all possible directions. */
    static final Direction[] DIRECTIONS =
    { Direction.N, Direction.S, Direction.E, Direction.W,
        Direction.NE, Direction.NW, Direction.SE, Direction.SW };


    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row-1][col-1]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is MxM.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {

        clear();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();
        _currentBoard = new Piece[M][M];
        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;
    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        _currentBoard = new Piece[M][M];
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        for (int r = 1; r <= _currentBoard.length; r += 1) {
            for (int c = 1;
                 c <= _currentBoard.length; c += 1) {
                this._currentBoard[r - 1][c - 1]
                        = board._currentBoard[r - 1][c - 1];
            }
        }
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 1 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        return _currentBoard[r - 1][c - 1];
    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /** Set the square at column C, row R to V, and make NEXT the next side
     *  to move, if it is not null. */
    void set(int c, int r, Piece v, Piece next) {
        _currentBoard[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moves.add(move);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        if (replaced != EMP) {
            set(c1, r1, EMP);
        }
        set(c1, r1, move.movedPiece());
        set(c0, r0, EMP);
        _turn = _turn.opposite();

    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        Piece movedPiece = move.movedPiece();
        set(c1, r1, replaced);
        set(c0, r0, movedPiece);
        _turn = _turn.opposite();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (_turn == EMP) {
            return false;
        } else if (move == null) {
            return false;
        } else if (pieceCountAlong(move) != move.length()) {
            return false;
        } else if (blocked(move)) {
            return false;
        }
        return move != null;
    }

   /** Function that checks if pieces are in bounds of the board.
    * @param i the pieces coordinates on board
    * @return i the integer coordinates*/
    boolean isInBounds(int i) {
        return i >= 1 && i <= M;
    }


    /** Return a sequence of all legal moves from this position. */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /** Return true if there is at least one legal move for the player
     *  on move. */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        if (piecesContiguous(BP) == 1 || piecesContiguous(WP) == 1) {
            return true;
        }
        return false;
    }

    /** Return true iff SIDE's pieces are continguous. */
    int piecesContiguous(Piece side) {
        int counter = 0;
        boolean[][] bitMap = new boolean[M][M];
        for (int i = 0; isInBounds(i + 1); i++) {
            for (int j = 0; isInBounds(j + 1); j++) {
                if (!bitMap[i][j]) {
                    bitMap[i][j] = true;
                    if (_currentBoard[i][j] == side) {
                        counter++;
                        checkNeighbors(i, j, bitMap, side);
                    }
                }
            }
        }
        return counter;
    }

    /** Recursive function to check neighbors in all directions.
     * @param i the bound of the rows
     * @param j the bound of the columns
     * @param bitMap boolean array that checks piece on board
     * @param side current player*/
    private void checkNeighbors(int i, int j, boolean[][] bitMap, Piece side) {
        for (Direction direction : DIRECTIONS) {
            int row = i + direction.dr;
            int col = j + direction.dc;
            if (isInBounds(row + 1)
                    && isInBounds(col + 1)
                    && !bitMap[row][col]) {
                bitMap[row][col] = true;
                if (_currentBoard[row][col] == side) {
                    checkNeighbors(row, col, bitMap, side);
                }
            }
        }
        return;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE. */
    private int pieceCountAlong(Move move) {
        int counter = 1;
        if (move.getCol0() == move.getCol1()) {
            Direction north = Direction.N;
            Direction south = Direction.S;
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), north);
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), south);
        } else if (move.getRow0() == move.getRow1()) {
            Direction west = Direction.W;
            Direction east = Direction.E;
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), west);
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), east);
        } else if ((move.getRow0()) + move.getCol0()
                == move.getRow1() + move.getCol1()) {
            Direction southWest = Direction.SW;
            Direction northEast = Direction.NE;
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), southWest);
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), northEast);
        } else if ((move.getRow0() - move.getCol0()
                == move.getRow1() - move.getCol1())) {
            Direction northWest = Direction.NW;
            Direction southEast = Direction.SE;
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), northWest);
            counter += pieceCountAlong(move.getCol0(),
                    move.getRow0(), southEast);
        }
        return counter;
    }

    /** Return the number of pieces in the line of action in direction DIR and
     *  containing the square at column C and row R. */
    private int pieceCountAlong(int c, int r, Direction dir) {
        int counter = -1;
        for (; isInBounds(c) && isInBounds(r); c += dir.dc, r -= dir.dr) {
            if (_currentBoard[r - 1][c - 1] != EMP) {
                counter += 1;
            }
        }
        return counter;
    }

    /** Returns the opposite direction.
     * @param directions the direction the moving piece */
    private Direction oppositeDirectionsIncr(Direction directions) {
        if (directions == N) {
            return S;
        }  else if (directions == S) {
            return N;
        }  else if (directions == W) {
            return E;
        }  else if (directions == E) {
            return W;
        }  else if (directions == SW) {
            return NE;
        }  else if (directions == NE) {
            return SW;
        }  else if (directions == NW) {
            return SE;
        }  else if (directions == SE) {
            return NW;
        }
        return directions;
    }

    /** Return true iff MOVE is blocked by an opposing piece or by a
     *  friendly piece on the target square. */
    private boolean blocked(Move move) {
        if (_currentBoard[move.getRow1() - 1][move.getCol1() - 1] == _turn) {
            return true;
        }
        if (move == null) {
            return true;
        } else if (move.getCol0()  == move.getCol1()) {
            Direction north = Direction.N;
            Direction south = Direction.S;
            if (move.getRow0()  > move.getRow1()) {
                return nextDirectionBlock(move, north);
            } else {
                return nextDirectionBlock(move, south);
            }
        } else if (move.getRow0() == move.getRow1()) {
            Direction east = Direction.E;
            Direction west = Direction.W;
            if (move.getCol0()  < move.getCol1()) {
                return nextDirectionBlock(move, east);
            } else {
                return nextDirectionBlock(move, west);
            }
        } else if (move.getRow0() + move.getCol0()
                == move.getRow1() + move.getCol1()) {
            Direction northEast = Direction.NE;
            Direction southWest = Direction.SW;
            if (move.getRow0() > move.getRow1()
                    && move.getCol0()  < move.getCol1()) {
                return nextDirectionBlock(move, northEast);
            } else {
                return nextDirectionBlock(move, southWest);
            }
        } else if (move.getRow0() - move.getCol0()
                == move.getRow1() - move.getCol1()) {
            Direction northWest = Direction.NW;
            Direction southEast = Direction.SE;
            if (move.getRow0()  < move.getRow1()
                    && move.getCol0()  < move.getCol1()) {
                return nextDirectionBlock(move, southEast);
            } else {
                return nextDirectionBlock(move, northWest);
            }
        } else {
            return true;
        }
    }

    /** Searches for the next direction in the board.
     * @param move the move in the game
     * @param directions the direction to next spot on board
     * @return false boolean if opposite piece does not exist*/
    private boolean nextDirectionBlock(Move move, Direction directions) {
        int row = move.getRow0(), col = move.getCol0();
        int counter = pieceCountAlong(move);
        for (; counter > 0; row -= directions.dr,
                col += directions.dc, counter--) {
            if (_currentBoard[row - 1][col - 1] == _turn.opposite()) {
                return true;
            }
        }
        return false;
    }

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** Two dimensional array that holds all pieces. */
    private Piece[][] _currentBoard;

    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {
        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;

        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1; _r = 1; _dir = N;
            incr();
        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }

            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
        }

        /** Advance to the next legal move. */
        private void incr() {
            for (int i = _r; isInBounds(i); i++) {
                for (int j = _c; isInBounds(j); j++) {
                    if (_currentBoard[i - 1][j - 1] == _turn) {
                        while (_dir != null) {
                            int spaces = 1;
                            spaces += pieceCountAlong(j, i, _dir);
                            spaces += pieceCountAlong(j, i,
                                    oppositeDirectionsIncr(_dir));
                            Move newMove = Move.create(j, i,
                                    j + spaces * _dir.dc ,
                                    i - spaces * _dir.dr , Board.this);
                            if (isLegal(newMove)) {
                                _r = i;
                                _c = j;
                                _move = newMove;
                                _dir = _dir.succ();
                                return;
                            }
                            _dir = _dir.succ();
                        }
                        _dir = _dir.N;
                    }
                }
                _c = 1;
            }
            _move = null;
        }
    }
}
