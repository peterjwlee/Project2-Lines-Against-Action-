package loa;
import static loa.Board.*;
import static loa.Piece.*;
import static loa.Direction.*;
import java.util.Iterator;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Created by PeterLee on 11/12/15.
 */
public class BoardTest {

    @Test
    public void testIsContiguous() {
        Board b = new Board();
        b.set(3, 7, BP);
        b.set(3, 6, BP);
        b.set(3, 5, BP);
        b.set(3, 4, BP);
        b.set(3, 3, BP);
        b.set(3, 2, BP);
        assertEquals(1, b.piecesContiguous(BP));
        Board b2 = new Board();
        b2.set(2, 2, WP);
        b2.set(3, 2, WP);
        b2.set(4, 2, WP);
        b2.set(5, 2, WP);
        b2.set(6, 2, WP);
        b2.set(7, 2, WP);
        assertEquals(1, b2.piecesContiguous(WP));
    }

    @Test
    public void testGet() {
        Board b3 = new Board();
        assertEquals(b3.get(2, 8), BP);
        assertEquals(b3.get(8, 2), WP);
        b3.set(3, 4, BP);
        assertEquals(b3.get(3, 4), BP);
        b3.set(6, 6, WP);
        assertEquals(b3.get(6, 6), WP);
    }

    @Test
    public void isLegalTest() {
        Board b = new Board();
        Move move = Move.create(6, 8, 6, 6, b);
        b.makeMove(move);
        Move move2 = Move.create(1, 2, 3, 2, b);
        b.makeMove(move2);
        Move move3 = Move.create(4, 8, 7, 5 , b);
        assertEquals(true, b.isLegal(move3));
        Board b1 = new Board();
        Move move4 = Move.create(100, 10, 10, 10, b1);
        assertEquals(false, b1.isLegal(move4));
        Board b4 = new Board();
        b4.set(6, 6, WP);
        Move move5 = Move.create(6, 8, 6, 6, b4);
        assertEquals(false, b4.isLegal(move5));
        Board b5 = new Board();
        b5.set(6, 3, WP);
        Move move6 = Move.create(6, 1, 6, 4, b5);
        assertEquals(false, b5.isLegal(move6));
        Move move7 = Move.create(6, 1, 6, 3, b5);
        assertEquals(false, b5.isLegal(move7));
        Board b8 = new Board();
        b8.set(4, 3, WP);
        Move move11 = Move.create(1, 3, 4, 3, b8);
        assertEquals(false, b8.isLegal(move11));
        b8.set(7, 6, BP);
        Move move12 = Move.create(8, 6, 5, 6, b8);
        assertEquals(false, b8.isLegal(move12));
        Board b10 = new Board();
        b10.set(6, 6, WP);
        Move move13 = Move.create(4, 8, 7, 5, b10);
        assertEquals(false, b10.isLegal(move13));
        b10.set(3, 6, WP);
        Move move14 = Move.create(5, 8, 2, 5, b10);
        assertEquals(false, b10.isLegal(move14));
        Board b40 = new Board();
        Move move16 = Move.create(2, 8, 2, 7, b40);
        assertEquals(false, b40.isLegal(move16));
    }

    @Test
    public void takePieceTest() {
        Board b = new Board();
        b.set(4, 4, WP);
        Move move = Move.create(4, 1, 4, 4, b);
        b.makeMove(move);
        assertEquals(BP, b.get(4, 4));
        Board b1 = new Board();
        Move move1 = Move.create(3, 1, 1, 3, b1);
        b1.makeMove(move1);
        assertEquals(BP, b1.get(1, 3));
        Board b2 = new Board();
        Move move2 = Move.create(4, 8, 4, 6, b2);
        b2.makeMove(move2);
        Move move3 = Move.create(1, 6, 4, 6, b2);
        b2.makeMove(move3);
        assertEquals(WP, b2.get(4, 6));
        Board b4 = new Board();
        b4.set(3, 6, BP);
        Move move5 = Move.create(5, 8, 2, 5, b4);
        b4.makeMove(move5);
        assertEquals(BP, b4.get(2, 5));
    }

    @Test
    public void makeMoveTest() {
        Board b1 = new Board();
        Move move = Move.create(4, 8, 4, 6, b1);
        b1.makeMove(move);
        assertEquals(BP, b1.get(4, 6));
        Board b2 = new Board();
        b2.set(6, 7, BP);
        Move move1 = Move.create(7, 8, 4, 5, b2);
        b2.makeMove(move1);
        assertEquals(BP, b2.get(4, 5));
    }

    @Test
    public void incrTest() {
        Board b = new Board();
        Iterator<Move> boardIter = b.iterator();
        int counter = 0;
        while (boardIter.hasNext()) {
            boardIter.next();
            counter += 1;
        }
        assertEquals(36, counter);

    }


    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BoardTest.class));
    }
}
