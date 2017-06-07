import edu.princeton.cs.algs4.*;
import java.util.*;

public class Solver {
  private class PriorityComparator implements Comparator<BoardNode>{
    public int compare(BoardNode a, BoardNode b){
      return Integer.compare(a.priority,  b.priority);
    }
  }

  abstract private class AStar{
    Deque<BoardNode> aStar(BoardNode start, BoardNode goal, BoardNode goal2){
      Set<BoardNode> closedSet = new TreeSet<BoardNode>();
      MinPQ<BoardNode> q = new MinPQ<BoardNode>(new PriorityComparator());
      q.insert(start);

      while (!q.isEmpty()){
        BoardNode current = q.delMin();
        if (current.board.equals(goal.board))
          return path(current);
        if (current.board.equals(goal2.board))
          return null;

        closedSet.add(current);
        for (BoardNode neighbor : current.neighbors()){
          if (!closedSet.contains(neighbor)){
            if (!current.isCritical(neighbor)){
              q.insert(neighbor);
            }
          }
        }
      }

      return null;
    }

    Deque<BoardNode> path(BoardNode current){
      Deque<BoardNode> total_path = new ArrayDeque<BoardNode>();
      total_path.push(current);
      while (current.prevNode() != null){
        current = current.prevNode();
        total_path.add(current);
      }
      return total_path;
    }
  }

  private void println(Object o){
    System.out.println(o);
  }

  private static int _node_id;

  private class BoardNode implements Comparable<BoardNode>{
    private final Board board;
    private final int moves, priority;
    private final int id;
    private final BoardNode prevNode;

    public BoardNode(BoardNode prevNode, Board board, int moves){
      this.prevNode = prevNode;
      this.board = board;
      this.id = _node_id++;
      this.moves = moves;

      this.priority = (moves + board.manhattan());
    }

    public BoardNode prevNode(){
      return prevNode;
    }

    public Iterable<BoardNode> neighbors(){
      PriorityQueue<BoardNode> q = new PriorityQueue<BoardNode>(new PriorityComparator());
      for (Board bd : board.neighbors()){
        q.add(new BoardNode(this, bd, moves+1));
      }
      return q;
    }

    public boolean isCritical(BoardNode that){
      return prevNode() != null && prevNode().board.equals(that.board);

    }
    public int compareTo(BoardNode that){
      if (board.equals(that.board)){
        return 0;
      }
      return id - that.id;
    }

    public boolean equals(Object other)        // does this board equal y?
    {
      if (other == this) return true;
      BoardNode that = (BoardNode) other;
      return board.equals(that.board);
    }

    public String toString(){
      StringBuilder s = new StringBuilder();
      s.append("priority  = " + priority + "\n");
      s.append("moves     = " + moves + "\n");
      s.append("manhattan = " + board.manhattan() + "\n");
      s.append(board.toString());
      return s.toString();
    }
  }

  private class AStarSolver extends AStar{
    final Deque<BoardNode> result;

    private Board goal(Board board){
      int n = board.dimension();
      int[][] blocks = new int[n][n];

      for (int row = 0; row < n; row++){
        for (int col = 0; col < n; col++){
          blocks[row][col] = row*n+col+1;
        }
      }
      blocks[n-1][n-1] = 0;
      return new Board(blocks);
    }

    AStarSolver(Board start){
      BoardNode bd = new BoardNode(null, start, 0);
      BoardNode gl = new BoardNode(null, goal(start), 0);
      BoardNode gl2 = new BoardNode(null, goal(start).twin(), 0);
      result = aStar(bd, gl, gl2);
      // result = ida_star(bd, gl, gl2);
      // result = iddfs(bd, gl, gl2);
      // result = aStarDual(bd, gl, gl2);
      // result = aStarOpenSet(bd, gl, gl2);
      // result = aStarNoSet(bd, gl, gl2);
    }
  }

  private final AStarSolver aStarSolver;
  public Solver(Board initial)           // find a solution to the initial board (using the A* algorithm)
  {
    aStarSolver = new AStarSolver(initial);
  }
  public boolean isSolvable()            // is the initial board solvable?
  {
    return aStarSolver.result != null;
  }
  public int moves()                     // min number of moves to solve initial board; -1 if unsolvable
  {
    return isSolvable() ? aStarSolver.result.size()-1 : -1;
  }

  public Iterable<Board> solution()      // sequence of boards in a shortest solution; null if unsolvable
  {
    if (!isSolvable()){
      return null;
    }

    Deque<Board> q = new ArrayDeque<Board>();
    for (BoardNode node : aStarSolver.result){
      q.addFirst(node.board);
    }
    return q;
  }

  public static void main(String[] args) {
    // create initial board from file
    In in = new In(args[0]);
    int n = in.readInt();
    int[][] blocks = new int[n][n];
    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++)
        blocks[i][j] = in.readInt();
    Board initial = new Board(blocks);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable())
      StdOut.println("No solution possible");
    else {
      StdOut.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
        StdOut.println(board);
    }
  }
}

