import edu.princeton.cs.algs4.*;
import java.util.*;

public class Solver {
  abstract private class AStar{
    abstract int heuristic_cost_estimate(BoardNode start, BoardNode goal);
    abstract int dist_between(BoardNode current, BoardNode neighbor);

    Deque<BoardNode> aStar4(BoardNode start, BoardNode goal){
      PriorityQueue<BoardNode> closedSet = new PriorityQueue<BoardNode>();
      PriorityQueue<BoardNode> closedSet2 = new PriorityQueue<BoardNode>();
      MinPQ<BoardNode> q = new MinPQ<BoardNode>();
      MinPQ<BoardNode> q2 = new MinPQ<BoardNode>();
      q.insert(start);
      q2.insert(goal);

      while (!q.isEmpty()){
        BoardNode current = q.delMin();
        BoardNode current2 = q2.delMin();
        if (current.board.equals(goal.board))
          return path(current);

        closedSet.add(current);
        closedSet2.add(current2);

        if (closedSet.contains(current2) || closedSet2.contains(current)){
          return path(current, current2);
        }

        for (BoardNode neighbor : current.neighbors()){
          if (!closedSet.contains(neighbor)){
            q.insert(neighbor);
          }
        }
        for (BoardNode neighbor : current2.neighbors()){
          if (!closedSet2.contains(neighbor)){
            q2.insert(neighbor);
          }
        }
      }

      return null;
    }

    Deque<BoardNode> aStar(BoardNode start, BoardNode goal){
      PriorityQueue<BoardNode> closedSet = new PriorityQueue<BoardNode>();
      // PriorityQueue<BoardNode> openSet = new PriorityQueue<BoardNode>();
      MinPQ<BoardNode> q = new MinPQ<BoardNode>();
      // openSet.add(start);
      q.insert(start);

      while (!q.isEmpty()){
        // BoardNode item = openSet.poll();
        BoardNode current = q.delMin();
        if (current.board.equals(goal.board))
          return path(current);

        closedSet.add(current);
        for (BoardNode neighbor : current.neighbors()){
          // if (!closedSet.contains(neighbor) && !openSet.contains(neighbor)){
          if (!closedSet.contains(neighbor)){
            if (current.prevNode() == null || !current.prevNode().board.equals(neighbor.board)){
              // openSet.add(neighbor);
              q.insert(neighbor);
            }
          }
        }
      }

      return null;
    }

    Deque<BoardNode> path(BoardNode current, BoardNode current2){
      BoardNode current22 = current2;
      Deque<BoardNode> total_path = new ArrayDeque<BoardNode>();
      total_path.addFirst(current2);
      while (current2.prevNode() != null){
        current2 = current2.prevNode();
        total_path.addFirst(current2);
      }
      if (!current.board.equals(current22.board)){
        total_path.addLast(current);
      }
      while (current.prevNode() != null){
        current = current.prevNode();
        total_path.addLast(current);
      }
      return total_path;
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
    private final int moves;
    private final int id;
    private final BoardNode prevNode;

    public BoardNode(BoardNode prevNode, Board board, int moves){
      this.prevNode = prevNode;
      this.board = board;
      this.id = _node_id++;
      this.moves = moves;
      if (id > 100000){
        throw new ArithmeticException();
      }
    }

    private Board goal(){
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

    public BoardNode prevNode(){
      return prevNode;
    }

    public Iterable<BoardNode> neighbors(){
      Deque<BoardNode> q = new ArrayDeque<BoardNode>();
      for (Board bd : board.neighbors()){
        q.add(new BoardNode(this, bd, moves+1));
      }
      return q;
    }

    private int priority(){
      return (moves + board.manhattan());
    }

    public int compareTo(BoardNode that){
      return priority() - that.priority();
    }

    public boolean equals(Object other)        // does this board equal y?
    {
      if (other == this) return true;
      if (other == null) return false;
      if (other.getClass() != this.getClass()) return false;
      BoardNode that = (BoardNode) other;
      if (id == that.id) return true;
      // if (moves != that.moves) return false;
      if (!board.equals(that.board)) return false;
      // if (board.hamming() != that.board.hamming()) return false;
      return true;
    }

    public String toString(){
      StringBuilder s = new StringBuilder();
      s.append("priority  = " + priority() + "\n");
      s.append("moves     = " + moves + "\n");
      s.append("manhattan = " + board.manhattan() + "\n");
      s.append(board.toString());
      return s.toString();
    }
  }

  private class AStarSolver extends AStar{
    int heuristic_cost_estimate(BoardNode start, BoardNode goal){
      return start.priority();
    }
    int dist_between(BoardNode current, BoardNode neighbor){
      return current.priority();
    }

    Deque<BoardNode> result;
    AStarSolver(Board start){
      BoardNode bn = new BoardNode(null, start, 0);
      result = aStar(bn, new BoardNode(null, bn.goal(), 0));
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
    return aStarSolver.result.size()-1;
  }

  public Iterable<Board> solution()      // sequence of boards in a shortest solution; null if unsolvable
  {
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

