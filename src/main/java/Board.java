import edu.princeton.cs.algs4.*;
import java.util.*;

public class Board {
  private final int dimension, manhattan, hamming;
  private final boolean isGoal;
  private final CharAryBlocks blocks;
  private final int rowOfBlank, colOfBlank;
  private final long hashCode;

  public Board(int[][] bl)           // construct a board from an n-by-n array of blocks
  {
    // this.blocks = bl.length <= 4 ? new LongBlocks(bl) : new CharAryBlocks(bl);
    this.blocks = new CharAryBlocks(bl);
    this.dimension = blocks.dimension();
    int[] blank = calcBlank();
    this.rowOfBlank = blank[0];
    this.colOfBlank = blank[1];
    this.hamming = calcHamming();
    this.manhattan = calcManhattan();
    this.isGoal = manhattan == 0;
    this.hashCode = calcHash();
  }
  public int dimension()                 // board dimension n
  {
    return dimension;
  }

  public int hamming()                   // number of blocks out of place
  {
    return hamming;
  }

  public int manhattan()                 // sum of Manhattan distances between blocks and goal
  {
    return manhattan;
  }

  public boolean isGoal()                // is this board the goal board?
  {
    return isGoal;
  }

  public Board twin()                    // a board that is obtained by exchanging any pair of blocks
  {
    int row1=0;
    int col1=0;
    int row2=0;
    int col2=1;
    if (rowOfBlank == 0){
      row1 = 1;
      row2 = 1;
    }

    exchange(row1, col1, row2, col2);
    Board bd = new Board(blocks.copyAsInt());
    exchange(row1, col1, row2, col2);

    return bd;
  }

  public boolean equals(Object other)        // does this board equal y?
  {
    if (other == this) return true;
    if (other == null) return false;
    if (other.getClass() != this.getClass()) return false;
    Board that = (Board) other;
    return hashCode == that.hashCode && manhattan == that.manhattan;
  }

  public Iterable<Board> neighbors()
  {
    Deque<Board> q = new ArrayDeque<Board>();
    if (rowOfBlank > 0){
      q.add(neighbor(rowOfBlank-1, colOfBlank));
    }
    if (rowOfBlank < dimension-1){
      q.add(neighbor(rowOfBlank+1, colOfBlank));
    }
    if (colOfBlank > 0){
      q.add(neighbor(rowOfBlank, colOfBlank-1));
    }
    if (colOfBlank < dimension-1){
      q.add(neighbor(rowOfBlank, colOfBlank+1));
    }
    return q;
  }

  public String toString()               // string representation of this board (in the output format specified below)
  {

    StringBuilder s = new StringBuilder();
    int n = dimension();
    s.append(n + "\n");
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        s.append(String.format("%2d ", blocks.get(i, j)));
      }
      s.append("\n");
    }
    return s.toString();
  }

  /*

  ######## private ########

  */

  private Board(CharAryBlocks blocks, int dimension, int rowOfBlank, int colOfBlank, int hamming, int manhattan, boolean isGoal, long hashCode){
    this.blocks = blocks;
    this.dimension = dimension;
    this.rowOfBlank = rowOfBlank;
    this.colOfBlank = colOfBlank;
    this.hamming = hamming;
    this.manhattan = manhattan;
    this.isGoal = isGoal;
    this.hashCode = hashCode;
  }

  private abstract class Blocks<T>{
    abstract int dimension();
    abstract int get(int row, int col);
    abstract void set(int row, int col, int n);
    abstract T copyT(T blocks);
    abstract T copyInt(int[][] blocks);
    abstract Blocks<T> dup();

    int[][] copyAsInt(){
      int[][] bl = new int[dimension][dimension];
      for (int row = 0; row < dimension; row++){
        for (int col = 0; col < dimension; col++){
          bl[row][col] = get(row, col);
        }
      }
      return bl;
    }
  }

  private class CharAryBlocks extends Blocks<char []>{
    private final char[] blocks;
    final int dimension;

    CharAryBlocks(int[][] blocks){
      this.dimension = blocks.length;
      this.blocks = copyInt(blocks);
    }

    CharAryBlocks(char[] blocks, int dimension){
      this.dimension = dimension;
      this.blocks = copyT(blocks);
    }

    int dimension(){
      return dimension;
    }

    int get(int row, int col){
      return blocks[row*dimension+col];
    }

    void set(int row, int col, int n){
      blocks[row*dimension+col] = (char)n;
    }

    char[] copyInt(int[][] blocks){
      int dimension = blocks.length;
      char[] bl = new char[dimension*dimension];
      for (int row = 0; row < dimension; row++){
        for (int col = 0; col < dimension; col++){
          bl[row*dimension+col] = (char)blocks[row][col];
        }
      }
      return bl;
    }

    char[] copyT(char[] blocks){
      char[] bl = new char[blocks.length];
      for (int i = 0; i < blocks.length; i++){
        bl[i] = blocks[i];
      }

      return bl;
    }

    CharAryBlocks dup(){
      return new CharAryBlocks(blocks, dimension);
    }
  }

  private int correctNumber(int dimension, int row, int col){
    return row==dimension-1 && col==dimension-1 ? 0 : row*dimension+col+1;
  }

  private int hamming(int n, int dimension, int row, int col){
    return (n != 0 && correctNumber(dimension, row, col) != n) ? 1 : 0;
  }

  private int calcHamming()                   // number of blocks out of place
  {
    int m = 0;
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        int n = blocks.get(row, col);
        m += hamming(n, blocks.dimension(), row, col);
      }
    }
    return m;
  }

  private void println(Object o){
    System.out.println(o);
  }

  private long calcHash(){
    long r = 7*blocks.dimension+3;
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        int n = blocks.get(row, col);
        r *= 23;
        r += n;
      }
    }

    return r;
  }

  private int[] calcBlank(){
    int[] point = new int[2];
    int dimension = blocks.dimension();
    for (int row = 0; row < dimension; row++){
      for (int col = 0; col < dimension; col++){
        int n = blocks.get(row, col);
        if (n == 0){
          point[0] = row;
          point[1] = col;
          return point;
        }
      }
    }
    return point;
  }

  private int manhattan(int n, int dimension, int row, int col){
    /*
       8 1 3
       4   2
       7 6 5

       row = 0
       col = 0
       return 3
       n = 8-1
       correct row of 7 -> 2
       n / 3
       correct col of 7 -> 1
       n % 3

*/
    if (n == 0){
      return 0;
    }

    int correntRowOfN = (n-1) / dimension;
    int correntColOfN = (n-1) % dimension;

    return Math.abs(row-correntRowOfN)+Math.abs(col-correntColOfN);
  }

  private boolean isInCorrectRow(int row, int col){
    return correctDistRow(row, col) == 0;
  }

  private boolean isInCorrectCol(int row, int col){
    return correctDistCol(row, col) == 0;
  }

  private int correctDistRow(int row, int col){
    int n = blocks.get(row, col);
    int correntRowOfN = (n-1) / dimension;
    return correntRowOfN - row;
  }

  private int correctDistCol(int row, int col){
    int n = blocks.get(row, col);
    int correntColOfN = (n-1) % dimension;
    return correntColOfN - col;
  }

  private int manhattanWithLinearConflict(int row, int col){
    int n = blocks.get(row, col);
    if (n == 0){
      return 0;
    }

    /*
       3 1 8    1 2 3
       7   2    4 5 6
       4 5 6    7 8
       */

    int distRow = 0;
    int distCol = 0;

    int lineConflict = 0;
    int correntRowOfN = (n-1) / dimension;
    int correntColOfN = (n-1) % dimension;
    distRow = correctDistRow(row, col);
    distCol = correctDistCol(row, col);
    if (distRow == 0){
      if (distCol > 0){
        // n = 3
        // row = 0
        // col = 0
        // correntRowOfN = 0
        // correntColOfN = 2
        // distRow = 0
        // distCol = 2-0

        // c = 0+1; c <= 2;
        for (int c = col+1; c <= correntColOfN; c++){
          if (isInCorrectRow(row, c)){ // 1 is in correntRowOfN
            if (correctDistCol(row, c) <= 0) // correctDistCol = -1
            {
              lineConflict += 1;
            }
          }
        }
      }
      if (distCol < 0){
        for (int c = col-1; c >= correntColOfN; c--){
          if (isInCorrectRow(row, c) && correctDistCol(row, c) >= 0){
            lineConflict += 1;
          }
        }
      }
    }
    if (distCol == 0){
      if (distRow > 0){
        for (int r = row+1; r <= correntRowOfN; r++){
          if (isInCorrectCol(r, col) && correctDistRow(r, col) <= 0){
            lineConflict += 1;
          }
        }
      }
      if (distRow < 0){
        for (int r = row-1; r >= correntRowOfN; r--){
          if (isInCorrectCol(r, col) && correctDistRow(r, col) >= 0){
            lineConflict += 1;
          }
        }
      }
    }

    return Math.abs(distRow)+Math.abs(distCol)+lineConflict;
  }

  private int calcManhattan() {
    int m = 0;
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        int n = blocks.get(row, col);
        m += manhattan(n, blocks.dimension(), row, col);
      }
    }
    return m;
  }

  private int calcManhattanWithLinearConflict() {
    int m = 0;
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        m += manhattanWithLinearConflict(row, col);
      }
    }
    return m;
  }

  private void exchange(int row1, int col1, int row2 ,int col2){
    int tmp = blocks.get(row1, col1);
    blocks.set(row1, col1, blocks.get(row2, col2));
    blocks.set(row2, col2, tmp);
  }

  private Board neighbor(int row, int col){
    int n = blocks.get(row, col);
    int prevHamming = hamming(n, dimension(), row, col);

    exchange(rowOfBlank, colOfBlank, row, col);

    int nextHamming = hamming(n,  dimension(), rowOfBlank, colOfBlank);
    int newManhattan = calcManhattanWithLinearConflict();
    int newHamming = hamming - prevHamming + nextHamming;
    long newHash = calcHash();

    Board bd = new Board(blocks.dup(), dimension(), row, col, newHamming, newManhattan, newManhattan==0, newHash);
    exchange(rowOfBlank, colOfBlank, row, col);

    return bd;
  }

  /*
  private Board neighbor(int row, int col){
    int n = blocks.get(row, col);

    int prevManhattan = manhattan(n, dimension(), row, col);
    int prevHamming = hamming(n, dimension(), row, col);

    exchange(rowOfBlank, colOfBlank, row, col);

    int nextManhattan = manhattan(n, dimension(), rowOfBlank, colOfBlank);
    int nextHamming = hamming(n,  dimension(), rowOfBlank, colOfBlank);
    int newManhattan = manhattan - prevManhattan + nextManhattan;
    int newHamming = hamming - prevHamming + nextHamming;
    long newHash = calcHash();

    Board bd = new Board(blocks.dup(), dimension(), row, col, newHamming, newManhattan, newManhattan==0, newHash);
    exchange(rowOfBlank, colOfBlank, row, col);

    return bd;
  }
  */
}
