import edu.princeton.cs.algs4.*;
import java.util.*;
import java.util.function.*;
import java.lang.reflect.Method;

public class Board {
  private enum Dir{ UP, DOWN, LEFT, RIGHT };
  private final int dimension, manhattan, hamming;
  private final boolean isGoal;
  private final int[][] blocks;
  private final int rowOfBlank, colOfBlank;
  private final long hashCode;

  private static Map<Long, Board> blocksCache;

  private static Board newBoard(int[][] blocks, int dimension, int rowOfBlank, int colOfBlank, int hamming, int manhattan, boolean isGoal, long hashCode){
    if (blocksCache == null){
      blocksCache = new HashMap<Long, Board>();
    }
    if (blocksCache.containsKey(hashCode)){
      return blocksCache.get(hashCode);
    }

    Board bd = new Board(copyBlocks(blocks), dimension, rowOfBlank, colOfBlank, hamming, manhattan, isGoal, hashCode);
    blocksCache.put(hashCode, bd);
    return bd;
  }

  private static long fact(int n){
    long r = 1;
    for (int i = 1; i <= n; i++)
      r *= i;
    return r;
  }

  private static long calcHash(int [][] blocks){
    long r = 0;
    for (int row = 0; row < blocks.length; row++){
      for (int col = 0; col < blocks.length; col++){
        int i = row*blocks.length+col+1;
        int n = blocks[row][col];
        r += fact(i)*n;
      }
    }

    assert(r > 0);
    return r;
    // return reduce(blocks, 7*blocks.length+3, (t, row, col, n) -> t * 23 + n);
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

  static private int[] calcBlank(int[][] blocks){
    int[] point = new int[2];
    count(blocks, (row, col, n) -> {
      if (n == 0){
        point[0] = row;
        point[1] = col;
        return true;
      }
      return false;
    });

    return point;
  }

  // (where blocks[i][j] = block in row i, column j)
  public Board(int[][] blocks)           // construct a board from an n-by-n array of blocks
  {
    this(blocks, blocks.length, calcBlank(blocks)[0], calcBlank(blocks)[1], calcHamming(blocks), calcManhattan(blocks), calcIsGoal(blocks), calcHash(blocks));
    // throw error if noBlank
  }

  private Board(int[][] blocks, int dimension, int rowOfBlank, int colOfBlank, int hamming, int manhattan, boolean isGoal, long hashCode){
    this.blocks = blocks;
    this.dimension = dimension;
    this.rowOfBlank = rowOfBlank;
    this.colOfBlank = colOfBlank;
    this.hamming = hamming;
    this.manhattan = manhattan;
    this.isGoal = isGoal;
    this.hashCode = hashCode;

    assert(blocks[rowOfBlank][colOfBlank] == 0);
  }

  static private int[][] copyBlocks(int[][] blocks){
    int n = blocks.length;
    int[][] bl = new int[n][n];
    for (int row = 0; row < n; row++){
      for (int col = 0; col < n; col++){
        bl[row][col] = blocks[row][col];
      }
    }

    return bl;
  }
  public int dimension()                 // board dimension n
  {
    return dimension;
  }

  @FunctionalInterface
  public interface CellCallBack<T> {
    public T get(int row, int col, int n);
  }

  @FunctionalInterface
  public interface CellReduce<T> {
    public T get(T t, int row, int col, int n);
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

  static private int calcHamming(int[][] blocks)                   // number of blocks out of place
  {
    return count(blocks, (row, col, n) -> hamming(n, blocks.length, row, col)!=0);
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

  static private int calcManhattan(int[][] blocks)                 // sum of Manhattan distances between blocks and goal
  {
    return reduce(blocks, 0, (t, row, col, n) -> t + manhattan(n, blocks.length, row, col));
  }

  public int manhattan()                 // sum of Manhattan distances between blocks and goal
  {
    return manhattan;
  }

  static private boolean calcIsGoal(int[][] blocks){
    return all(blocks, (row, col, n) -> correctNumber(blocks.length, row, col) == n);
  }

  public boolean isGoal()                // is this board the goal board?
  {
    return isGoal;
  }

  public Board twin()                    // a board that is obtained by exchanging any pair of blocks
  {
    return null;
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

  private void exchange(int [][] bl, int row1, int col1, int row2 ,int col2){
    int tmp = bl[row1][col1];
    bl[row1][col1] = bl[row2][col2];
    bl[row2][col2] = tmp;
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
    int [][] bl = blocks;
    int n = blocks[row][col];
    assert(n != 0);
    assert(blocks[rowOfBlank][colOfBlank] == 0);

    int prevManhattan = manhattan(n, blocks.length, row, col);
    int prevHamming = hamming(n, blocks.length, row, col);
    exchange(bl, rowOfBlank, colOfBlank, row, col);
    int nextManhattan = manhattan(n, bl.length, rowOfBlank, colOfBlank);
    int nextHamming = hamming(n,  bl.length, rowOfBlank, colOfBlank);
    int newManhattan = manhattan - prevManhattan + nextManhattan;
    int newHamming = hamming - prevHamming + nextHamming;
    long newHash = calcHash(hashCode, dimension, n, row, col, rowOfBlank, colOfBlank);
    Board bd = newBoard(bl, bl.length, row, col, newHamming, newManhattan, newManhattan==0, newHash);
    exchange(blocks, rowOfBlank, colOfBlank, row, col);
    return bd;
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

  static String blocksString(int[][] blocks){
    StringBuilder s = new StringBuilder();
    s.append(blocks.length + "\n");
    s = reduce(blocks, s, (s_, row, col, n)->{
      s_.append(String.format("%2d ", n));
      if (col == blocks.length-1){
        s_.append("\n");
      }
      return s_;
    });
    return s.toString();
  }

  public String toString()               // string representation of this board (in the output format specified below)
  {
    return blocksString(blocks);
  }
}
