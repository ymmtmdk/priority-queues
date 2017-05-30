import edu.princeton.cs.algs4.*;
import java.util.*;

public class Board {
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

  private void testBlocks(){
    int[][] bl = new int[3][3];
    bl[0][0] = 1;
    bl[0][1] = 0;
    bl[1][0] = 2;
    bl[1][1] = 3;
    LongBlocks lb = new LongBlocks(bl);
    assert(1 == lb.get(0,0));
    lb.set(0,0,0);
    assert(0 == lb.get(0,0));
    lb.set(0,0,2);
    assert(2 == lb.get(0,0));
    assert(2 == lb.get(1,0));
    lb.set(1,0,5);
    assert(5 == lb.get(1,0));
    lb.set(1,1,15);
    assert(15 == lb.get(1,1));
    lb.set(2,2,6);
    assert(6 == lb.get(2,2));
  }

  private class LongBlocks extends Blocks<Long>{
    private long blocks;
    final int dimension;
    // private final CharAryBlocks cab;

    LongBlocks(int[][] blocks){
      this.dimension = blocks.length;
      assert(dimension <= 4);
      this.blocks = copyInt(blocks);
      this.cab = new CharAryBlocks(blocks);
    }

    LongBlocks(long blocks, int dimension){
      this.dimension = dimension;
      assert(dimension <= 4);
      this.blocks = copyT(blocks);
      // this.cab = new CharAryBlocks(copyAsInt());
    }

    int dimension(){
      return dimension;
    }

    long idx(int row, int col){
      return row*dimension+col;
    }

    int get(int row, int col){
      // println("get :"+str(blocks));
      // println("idx :"+idx(row,col));
      long n = (blocks >> (idx(row,col)*4)) & 0x0f;
      // println("n   :"+n);
      return (int)n;
    }

    String str(long n){
      return String.format("%64s", Long.toBinaryString(n));
    }

    void set(int row, int col, int n){
      assert(n<16);
      long l = n;
      // println("row :"+row);
      // println("col :"+col);
      // println("n   :"+n);
      // println("idx :"+idx(row,col));
      long mask = ~(0x0fL << idx(row,col)*4);
      long m = l << idx(row,col)*4;
      // println("befo:"+str(blocks));
      // println("mask:"+str(mask));
      // println("m   :"+str(m));
      blocks = (blocks & mask) | m;
      // println("afte:"+str(blocks));
      cab.set(row, col, n);

      if(cab.get(row,col) != get(row,col)){
      // println("m   :"+str(m));
      // println("get :"+get(row,col));
      // println("afte:"+str(blocks));
      }
      assert(get(row,col) == n);
      assert(cab.get(row,col) == get(row,col));
    }

    Long copyInt(int[][] blocks){
      int dimension = blocks.length;
      long r = 0;
      for (int row = 0; row < dimension; row++){
        for (int col = 0; col < dimension; col++){
          long l = blocks[row][col];
          r += (l << (idx(row,col)*4));
          // println(str(r));
        }
      }
      return r;
    }

    Long copyT(Long blocks){
      return blocks;
    }

    LongBlocks dup(){
      return new LongBlocks(blocks, dimension);
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

  private enum Dir{ UP, DOWN, LEFT, RIGHT };
  private final int dimension, manhattan, hamming;
  private final boolean isGoal;
  private final Blocks blocks;
  private final int rowOfBlank, colOfBlank;
  private final long hashCode;

  private static Map<Long, Board> boardCache;

  private static Board newBoard(Blocks blocks, int dimension, int rowOfBlank, int colOfBlank, int hamming, int manhattan, boolean isGoal, long hashCode){
    if (boardCache == null){
      boardCache = new HashMap<Long, Board>();
    }
    if (boardCache.containsKey(hashCode)){
      return boardCache.get(hashCode);
    }

    Board bd = new Board(blocks.dup(), dimension, rowOfBlank, colOfBlank, hamming, manhattan, isGoal, hashCode);
    // bd.testBlocks();
    boardCache.put(hashCode, bd);
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
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        int i = row*blocks.dimension()+col+1;
        int n = blocks.get(row, col);
        r += fact(i)*n;
      }
    }

    // assert(r > 0);
    return r;
    // return reduce(blocks, 7*blocks.length+3, (t, row, col, n) -> t * 23 + n);
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
    this.blocks = new LongBlocks(bl);
    this.dimension = blocks.dimension();
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
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        int n = blocks.get(row, col);
        m += hamming(n, blocks.dimension(), row, col);
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
    for (int row = 0; row < blocks.dimension(); row++){
      for (int col = 0; col < blocks.dimension(); col++){
        int n = blocks.get(row, col);
        m += manhattan(n, blocks.dimension(), row, col);
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
    int dimension = blocks.dimension();
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
    int row1=0;
    int col1=0;
    int row2=0;
    int col2=1;
    if (rowOfBlank == 0){
      row1 = 1;
      row2 = 1;
    }

    assert(blocks.get(rowOfBlank, colOfBlank) == 0);
    assert(blocks.get(row1, col1) != 0);
    assert(blocks.get(row2, col2) != 0);
    // println("ex: "+ rowOfBlank + ":" + colOfBlank + ":" + row1);
    exchange(row1, col1, row2, col2);
    Board bd = new Board(blocks.copyAsInt());
    exchange(row1, col1, row2, col2);
    // Board bd = exBoard(rowOfBlank, colOfBlank, row1, col1, row2, col2);
    // println(bd);
    assert(bd.blocks.get(rowOfBlank, colOfBlank) == 0);
    assert(bd.blocks.get(row1, col1) != 0);
    assert(bd.blocks.get(row2, col2) != 0);
    return bd;
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

  private void exchange(int row1, int col1, int row2 ,int col2){
    int tmp = blocks.get(row1, col1);
    blocks.set(row1, col1, blocks.get(row2, col2));
    blocks.set(row2, col2, tmp);
  }

  private Board exBoard(int row0, int col0, int row1, int col1, int row2, int col2){
    int n = blocks.get(row2, col2);

    int prevManhattan = manhattan(n, blocks.dimension(), row2, col2);
    int prevHamming = hamming(n, blocks.dimension(), row2, col2);
    exchange(row1, col1, row2, col2);
    int nextManhattan = manhattan(n, blocks.dimension(), row1, col1);
    int nextHamming = hamming(n,  blocks.dimension(), row1, col1);
    int newManhattan = manhattan - prevManhattan + nextManhattan;
    int newHamming = hamming - prevHamming + nextHamming;
    long newHash = calcHash(row2, col2, row1, col1);
    assert(newHash == calcHash());
    Board bd = newBoard(blocks, blocks.dimension(), row0, col0, newHamming, newManhattan, newManhattan==0, newHash);
    exchange(row1, col1, row2, col2);

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
    return exBoard(row, col, rowOfBlank, colOfBlank, row, col);
  }

  private long calcHash(int row1, int col1, int row2, int col2){
    /*
       1 2 6 24
       0,1,2,3

       0,2,1,3
       */

    long r = hashCode;
    int n = blocks.get(row1, col1);// 1
    int m = blocks.get(row2, col2);// 2
    int o = n - m;
    int i = row1*dimension+col1+1; // 2
    int j = row2*dimension+col2+1; // 3
    r += fact(i)*o; // fact(2)*1
    r -= fact(j)*o; // fact(1)*1
    return r;
  }

  public Iterable<Board> neighbors()
  {
    Deque<Board> q = new ArrayDeque<Board>();
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
    int n = blocks.dimension();
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
