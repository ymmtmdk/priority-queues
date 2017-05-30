import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class StackTest{
  @Test
  public void test(){
    int[][] bl = new int[2][2];
    bl[0][0] = 1;
    bl[0][1] = 0;
    bl[1][0] = 2;
    bl[1][1] = 3;
    Board bd = new Board(bl);
    assertEquals("2\n 1  0 \n 2  3 \n", bd.toString());
    assertEquals("2\n 1  0 \n 3  2 \n", bd.twin().toString());
    assertEquals("2\n 1  0 \n 2  3 \n", bd.twin().twin().toString());
    new Solver(bd);
     // assertEquals("2\n 2  1 \n 3  0 \n", tw.toString())
  }
}
