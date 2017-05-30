import edu.princeton.cs.algs4.*;
import java.util.*;

public class Solver {
  abstract private class AStar{
    abstract int heuristic_cost_estimate(BoardNode start, BoardNode goal);
    abstract int dist_between(BoardNode current, BoardNode neighbor);

    Deque<BoardNode> aStar(BoardNode start, BoardNode goal){
      // The set of nodes already evaluated.
      // Set<BoardNode> closedSet = new HashSet<BoardNode>();
      PriorityQueue<BoardNode> closedSet = new PriorityQueue<BoardNode>();
      // The set of currently discovered nodes that are not evaluated yet.
      // Initially, only the start node is known.
      PriorityQueue<BoardNode> openSet = new PriorityQueue<BoardNode>();
      openSet.add(start);
      // For each node, which node it can most efficiently be reached from.
      // If a node can be reached from many nodes, cameFrom will eventually contain the
      // most efficient previous step.
      // Map<BoardNode, BoardNode> cameFrom = new TreeMap<BoardNode, BoardNode>(); //the empty map

      // For each node, the cost of getting from the start node to that node.
      Map<BoardNode, Integer> gScore = new TreeMap<BoardNode, Integer>(); //map with default value of Infinity
      // The cost of going from start to start is zero.
      gScore.put(start, 0);
      // For each node, the total cost of getting from the start node to the goal
      // by passing by that node. That value is partly known, partly heuristic.
      Map<BoardNode, Integer> fScore = new TreeMap<BoardNode, Integer>(); //map with default value of Infinity
      // For the first node, that value is completely heuristic.
      fScore.put(start, heuristic_cost_estimate(start, goal));

      while (!openSet.isEmpty()){
        BoardNode current = openSet.poll(); //the node in openSet having the lowest fScore[] value
        if (current.equals(goal))
          return reconstruct_path(current);

        // openSet.Remove(current)
        closedSet.add(current);
        // println(current.board);
        for (BoardNode neighbor : current.neighbors()){
          if (closedSet.contains(neighbor)){
            continue;		// Ignore the neighbor which is already evaluated.
          }
          // The distance from start to a neighbor
          int tentative_gScore = gScore.get(current) + dist_between(current, neighbor);
          if (!openSet.contains(neighbor)) // not in openSet	// Discover a new node
            openSet.add(neighbor);
          else if (gScore.containsKey(neighbor) && tentative_gScore >= gScore.get(neighbor)){
            continue;		// This is not a better path.
          }

          // This path is the best until now. Record it!
          // cameFrom.put(neighbor, current);
          gScore.put(neighbor, tentative_gScore);
          fScore.put(neighbor, gScore.get(neighbor) + heuristic_cost_estimate(neighbor, goal));
          // return 0;
        }
      }

      return null;
    }

    Deque<BoardNode> aStar2(BoardNode start, BoardNode goal){
      MinPQ<BoardNode> q = new MinPQ<BoardNode>();
      Set<BoardNode> closedSet = new TreeSet<BoardNode>();
      q.insert(start);
      // set.add(start);

      while (!q.isEmpty()){
        BoardNode current = q.delMin(); //the node in openSet having the lowest fScore[] value
        if (current.equals(goal))
          return reconstruct_path(current);

        closedSet.add(current);

        for (BoardNode neighbor : current.neighbors()){
          if (!closedSet.contains(neighbor)){
          // if (!openSet.contains(neighbor)) // not in openSet	// Discover a new node
            q.insert(neighbor);
          }
        }
      }

      return null;
    }

    Deque<BoardNode> aStar3(BoardNode start, BoardNode goal){
      PriorityQueue<BoardNode> closedSet = new PriorityQueue<BoardNode>();
      // PriorityQueue<BoardNode> openSet = new PriorityQueue<BoardNode>(Comparator.reverseOrder());
      PriorityQueue<BoardNode> openSet = new PriorityQueue<BoardNode>();
      MinPQ<BoardNode> q = new MinPQ<BoardNode>();
      openSet.add(start);
      q.insert(start);

      while (!openSet.isEmpty()){
        BoardNode current = openSet.poll();
        BoardNode item = q.delMin();
        if (!current.equals(item)){
          println(current);
          println(item);

        }
        assert(current.equals(item));
        if (current.board.equals(goal.board))
          return reconstruct_path(current);

        closedSet.add(current);
        for (BoardNode neighbor : current.neighbors()){
          if (closedSet.contains(neighbor)){
            continue;
          }
          if (!openSet.contains(neighbor)){
            openSet.add(neighbor);
            q.insert(neighbor);
          }
      // return 0 - (moves + board.hamming());
        }
      }

      return null;
    }


    Deque<BoardNode> reconstruct_path(BoardNode current){
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
      // Board initial = new Board(blocks);

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
      if (!board.equals(that.board)) return false;
      if (moves != that.moves) return false;
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
      result = aStar3(bn, new BoardNode(null, bn.goal(), 0));
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

