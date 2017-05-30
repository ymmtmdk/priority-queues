import edu.princeton.cs.algs4.*;
import java.util.*;

public class Board {
  private class Blocks{
    private final char[] blocks;
    final int dimension;

    Blocks(int[][] blocks){
      this.dimension = blocks.length;
      this.blocks = copy(blocks);
    }

    Blocks(char[] blocks, int dimension){
      this.dimension = dimension;
      this.blocks = copy(blocks);
    }

    int get(int row, int col){
      return blocks[row*dimension+col];
    }

    void set(int row, int col, int n){
      blocks[row*dimension+col] = (char)n;
    }

    char[] copy(int[][] blocks){
      char[] bl = new char[dimension*dimension];
      for (int row = 0; row < dimension; row++){
        for (int col = 0; col < dimension; col++){
          bl[row*dimension+col] = (char)blocks[row][col];
        }
      }

      return bl;
    }

    char[] copy(char[] blocks){
      char[] bl = new char[blocks.length];
      for (int i = 0; i < blocks.length; i++){
        bl[i] = blocks[i];
      }

      return bl;
    }

    Blocks copy(){
      return new Blocks(blocks, dimension);
    }
  }

  private enum Dir{ UP, DOWN, LEFT, RIGHT };
  private final int dimension, manhattan, hamming;
  private final boolean isGoal;
  private final Blocks blocks;
  private final int rowOfBlank, colOfBlank;
  private final long hashCode;

  private static Map<Long, Board> blocksCache;

  private static Board newBoard(Blocks blocks, int dimension, int rowOfBlank, int colOfBlank, int hamming, int manhattan, boolean isGoal, long hashCode){
    if (blocksCache == null){
      blocksCache = new HashMap<Long, Board>();
    }
    if (blocksCache.containsKey(hashCode)){
      return blocksCache.get(hashCode);
    }

    Board bd = new Board(blocks.copy(), dimension, rowOfBlank, colOfBlank, hamming, manhattan, isGoal, hashCode);
    blocksCache.put(hashCode, bd);
    return bd;
  }

  private static long fact(int n){
    long r = 1;
    for (int i = 1; i <= n; i++)
      r *= i;
    return r;
  }

  private long calcHash(){
    long r = 0;
    for (int row = 0; row < blocks.dimension; row++){
      for (int col = 0; col < blocks.dimension; col++){
        int i = row*blocks.dimension+col+1;
        int n = blocks.get(row, col);
        r += fact(i)*n;
      }
    }

    assert(r > 0);
    return r;
    // return reduce(blocks, 7*blocks.length+3, (t, row, col, n) -> t * 23 + n);
  }

  /*
     @FunctionalInterface
     public interface CellCallBack<T> {
     public T get(int row, int col, int n);
     }

     @FunctionalInterface
     public interface CellReduce<T> {
     public T get(T t, int row, int col, int n);
     }

     private static <T> T reduce(int [][] bl, T t, CellReduce<T> ccb){
     for (int row = 0; row < bl.length; row++){
     for (int col = 0; col < bl.length; col++){
     t = ccb.get(t, row, col, bl[row][col]);
     }
     }
     return t;
     }

     static private int count(int[][] bl, CellCallBack<Boolean> ccb){
     return reduce(bl, 0, (t, row, col, n) ->
     t + (ccb.get(row, col, n) ? 1 : 0)
     );
     }

     static private boolean all(int[][] bl, CellCallBack<Boolean> ccb){
     for (int row = 0; row < bl.length; row++){
     for (int col = 0; col < bl.length; col++){
     if (!ccb.get(row, col, bl[row][col])){
     return false;
     }
     }
     }
     return true;
     }

*/
  private int[] calcBlank(){
    int[] point = new int[2];
    int dimension = blocks.dimension;
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
    /*
       count(blocks, (row, col, n) -> {
       if (n == 0){
       point[0] = row;
       point[1] = col;
       return true;
       }
       return false;
       });

*/
    return point;
  }

  // (where blocks[i][j] = block in row i, column j)
  public Board(int[][] bl)           // construct a board from an n-by-n array of blocks
  {
    this.blocks = new Blocks(bl);
    this.dimension = blocks.dimension;
    this.rowOfBlank = calcBlank()[0];
    this.colOfBlank = calcBlank()[1];
    this.hamming = calcHamming();
    this.manhattan = calcManhattan();
    this.isGoal = calcIsGoal();
    this.hashCode = calcHash();
  }

  private Board(Blocks blocks, int dimension, int rowOfBlank, int colOfBlank, int hamming, int manhattan, boolean isGoal, long hashCode){
    this.blocks = blocks;
    this.dimension = dimension;
    this.rowOfBlank = rowOfBlank;
    this.colOfBlank = colOfBlank;
    this.hamming = hamming;
    this.manhattan = manhattan;
    this.isGoal = isGoal;
    this.hashCode = hashCode;
  }

  public int dimension()                 // board dimension n
  {
    return dimension;
  }

  static private int correctNumber(int dimension, int row, int col){
    return row==dimension-1 && col==dimension-1 ? 0 : row*dimension+col+1;
  }

  public int hamming()                   // number of blocks out of place
  {
    return hamming;
  }

  static private int hamming(int n, int dimension, int row, int col){
    return (n != 0 && correctNumber(dimension, row, col) != n) ? 1 : 0;
  }

  private int calcHamming()                   // number of blocks out of place
  {
    int m = 0;
    for (int row = 0; row < blocks.dimension; row++){
      for (int col = 0; col < blocks.dimension; col++){
        int n = blocks.get(row, col);
        m += hamming(n, blocks.dimension, row, col);
      }
    }
    return m;
    // return count(blocks, (row, col, n) -> hamming(n, blocks.length, row, col)!=0);
  }

  static private void println(Object o){
    System.out.println(o);
  }

  private static int manhattan(int n, int dimension, int row, int col){
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

    //TODO compare to goal?
    int correntRowOfN = (n-1) / dimension;
    int correntColOfN = (n-1) % dimension;

    return Math.abs(row-correntRowOfN)+Math.abs(col-correntColOfN);
  }

  private int calcManhattan()                 // sum of Manhattan distances between blocks and goal
  {
    int m = 0;
    for (int row = 0; row < blocks.dimension; row++){
      for (int col = 0; col < blocks.dimension; col++){
        int n = blocks.get(row, col);
        m += manhattan(n, blocks.dimension, row, col);
      }
    }
    return m;
    // return reduce(blocks, 0, (t, row, col, n) -> t + manhattan(n, blocks.length, row, col));
  }

  public int manhattan()                 // sum of Manhattan distances between blocks and goal
  {
    return manhattan;
  }

  private boolean calcIsGoal(){
    int dimension = blocks.dimension;
    for (int row = 0; row < dimension; row++){
      for (int col = 0; col < dimension; col++){
        int n = blocks.get(row, col);
        if (correctNumber(dimension, row, col) != n){
          return false;
        }
      }
    }

    return true;
    // return all(blocks, (row, col, n) -> correctNumber(blocks.length, row, col) == n);
  }

  public boolean isGoal()                // is this board the goal board?
  {
    return isGoal;
  }

  public Board twin()                    // a board that is obtained by exchanging any pair of blocks
  {
    int row1=-100;
    int col1=-100;
    int row2=-100;
    int col2=-100;
    if (rowOfBlank == 0){
      row1 = 1;
      col1 = 0;
      row2 = 1;
      col2 = 1;
    }
    else{
      row1 = 0;
      col1 = 0;
      row2 = 0;
      col2 = 1;
    }

    assert(blocks.get(row1, col1) != 0);
    assert(blocks.get(row2, col2) != 0);
    return exBoard(rowOfBlank, colOfBlank, row1, col1, row2, col2);
  }

  public boolean equals(Object other)        // does this board equal y?
  {
    if (other == this) return true;
    if (other == null) return false;
    if (other.getClass() != this.getClass()) return false;
    Board that = (Board) other;
    if (hashCode == that.hashCode){
      // println("" + rowOfBlank +", "+ that.rowOfBlank);
      assert(rowOfBlank == that.rowOfBlank);
      assert(colOfBlank == that.colOfBlank);
      assert(hamming == that.hamming);
      assert(manhattan == that.manhattan);
      assert(isGoal == that.isGoal);
    }
    return hashCode == that.hashCode;
  }

  private void exchange(Blocks bl, int row1, int col1, int row2 ,int col2){
    int tmp = bl.get(row1, col1);
    bl.set(row1, col1, bl.get(row2, col2));
    bl.set(row2, col2, tmp);
  }

  private Board exBoard(int row0, int col0, int row1, int col1, int row2, int col2){
    int n = blocks.get(row2, col2);

    int prevManhattan = manhattan(n, blocks.dimension, row2, col2);
    int prevHamming = hamming(n, blocks.dimension, row2, col2);
    exchange(blocks, row1, col1, row2, col2);
    int nextManhattan = manhattan(n, blocks.dimension, row1, col1);
    int nextHamming = hamming(n,  blocks.dimension, row1, col1);
    int newManhattan = manhattan - prevManhattan + nextManhattan;
    int newHamming = hamming - prevHamming + nextHamming;
    long newHash = calcHash(hashCode, dimension, n, row2, col2, row1, col1);
    Board bd = newBoard(blocks, blocks.dimension, row2, col2, newHamming, newManhattan, newManhattan==0, newHash);
    exchange(blocks, row1, col1, row2, col2);

    return bd;
  }

  private Board neighbor(Dir dir){
    int row=-100;
    int col=-100;

    switch (dir){
      case UP:
        row = rowOfBlank - 1;
        col = colOfBlank;
        break;
      case DOWN:
        row = rowOfBlank + 1;
        col = colOfBlank;
        break;
      case LEFT:
        row = rowOfBlank;
        col = colOfBlank - 1;
        break;
      case RIGHT:
        row = rowOfBlank;
        col = colOfBlank + 1;
        break;
    }
    return exBoard(rowOfBlank, colOfBlank, rowOfBlank, colOfBlank, row, col);
  }

  static private long calcHash(long hashCode, int dimension, int n, int rowOfBlank, int colOfBlank, int row, int col){
    /*
       1 2 6 24
       0,1,2,3
       0+2+12+72

       1,0,2,3
       1+0+12+72
       */

    int i = row*dimension+col; // 0
    int j = rowOfBlank*dimension+colOfBlank; // 1
    hashCode -= fact(j+1)*n; // fact(2)*1
    hashCode += fact(i+1)*n; // fact(1)*1
    return hashCode;
  }

  public Iterable<Board> neighbors()
  {
    Deque q = new ArrayDeque<Board>();
    if (rowOfBlank > 0){
      q.add(neighbor(Dir.UP));
    }
    if (rowOfBlank < dimension-1){
      q.add(neighbor(Dir.DOWN));
    }
    if (colOfBlank > 0){
      q.add(neighbor(Dir.LEFT));
    }
    if (colOfBlank < dimension-1){
      q.add(neighbor(Dir.RIGHT));
    }
    return q;
  }

  private static String blocksString(Blocks blocks){
    StringBuilder s = new StringBuilder();
    int n = blocks.dimension;
    s.append(n + "\n");
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        s.append(String.format("%2d ", blocks.get(i, j)));
      }
      s.append("\n");
    }
    return s.toString();
    /*
       s = reduce(blocks, s, (s_, row, col, n)->{
       s_.append(String.format("%2d ", n));
       if (col == blocks.length-1){
       s_.append("\n");
       }
       return s_;
       });
       return s.toString();
       */
  }

  public String toString()               // string representation of this board (in the output format specified below)
  {
    return blocksString(blocks);
  }
}
