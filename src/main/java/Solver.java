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
      Map<BoardNode, BoardNode> cameFrom = new TreeMap<BoardNode, BoardNode>(); //the empty map

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
          return reconstruct_path(cameFrom, current);

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
          cameFrom.put(neighbor, current);
          gScore.put(neighbor, tentative_gScore);
          fScore.put(neighbor, gScore.get(neighbor) + heuristic_cost_estimate(neighbor, goal));
          // return 0;
        }
      }

      return null;
    }

    Deque<BoardNode> reconstruct_path(Map<BoardNode, BoardNode> cameFrom, BoardNode current){
      Deque<BoardNode> total_path = new ArrayDeque<BoardNode>();
      total_path.push(current);
      // total_path := [current]
      while (cameFrom.containsKey(current)){
        // while current in cameFrom.Keys:
        current = cameFrom.get(current);
        // current := cameFrom[current]
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
    BoardNode(Board board, int moves){
      this.board = board;
      this.id = _node_id++;
      this.moves = moves;
      if (id > 130000){
        throw new ArithmeticException();
      }
    }

    Board goal(){
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

    public Iterable<BoardNode> neighbors(){
      Deque<BoardNode> q = new ArrayDeque<BoardNode>();
      for (Board bd : board.neighbors()){
        q.add(new BoardNode(bd, moves+1));
      }
      return q;
    }

    private int priority(){
      return 0 - (moves*3 + board.hamming()*2 + board.manhattan());
      // return 0 - (moves + board.manhattan());
    }

    public int compareTo(BoardNode that){
      int n = that.priority() - this.priority();
      if (n == 0){
        if (board.equals(that.board)){
          return 0;
        }
        else{
          return this.id - that.id;
        }
      }else{
        return n;
      }
    }
    public boolean equals(Object other)        // does this board equal y?
    {
      if (other == this) return true;
      if (other == null) return false;
      if (other.getClass() != this.getClass()) return false;
      BoardNode that = (BoardNode) other;
      if (id == that.id) return true;
      if (!board.equals(that.board)) return false;
      // if (moves != that.moves) return false;
      // if (board.hamming() != that.board.hamming()) return false;
      return true;
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
      BoardNode bn = new BoardNode(start, 0);

      result = aStar(bn, new BoardNode(bn.goal(), 0));
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
/*
   function A*(start, goal)
// The set of nodes already evaluated.
closedSet := {}
// The set of currently discovered nodes that are not evaluated yet.
// Initially, only the start node is known.
openSet := {start}
// For each node, which node it can most efficiently be reached from.
// If a node can be reached from many nodes, cameFrom will eventually contain the
// most efficient previous step.
cameFrom := the empty map

// For each node, the cost of getting from the start node to that node.
gScore := map with default value of Infinity
// The cost of going from start to start is zero.
gScore[start] := 0
// For each node, the total cost of getting from the start node to the goal
// by passing by that node. That value is partly known, partly heuristic.
fScore := map with default value of Infinity
// For the first node, that value is completely heuristic.
fScore[start] := heuristic_cost_estimate(start, goal)

while openSet is not empty
current := the node in openSet having the lowest fScore[] value
if current = goal
return reconstruct_path(cameFrom, current)

openSet.Remove(current)
closedSet.Add(current)
for each neighbor of current
if neighbor in closedSet
continue		// Ignore the neighbor which is already evaluated.
// The distance from start to a neighbor
tentative_gScore := gScore[current] + dist_between(current, neighbor)
if neighbor not in openSet	// Discover a new node
openSet.Add(neighbor)
else if tentative_gScore >= gScore[neighbor]
continue		// This is not a better path.

// This path is the best until now. Record it!
cameFrom[neighbor] := current
gScore[neighbor] := tentative_gScore
fScore[neighbor] := gScore[neighbor] + heuristic_cost_estimate(neighbor, goal)

return failure

function reconstruct_path(cameFrom, current)
total_path := [current]
while current in cameFrom.Keys:
current := cameFrom[current]
total_path.append(current)
return total_path

public class Solver {
abstract private class AStar{
abstract int heuristic_cost_estimate(BoardNode start, BoardNode goal);
abstract int dist_between(BoardNode current, BoardNode neighbor);

Deque<BoardNode> aStar(BoardNode start, BoardNode goal){
// The set of nodes already evaluated.
Set<BoardNode> closedSet = new HashSet<BoardNode>();
// The set of currently discovered nodes that are not evaluated yet.
// Initially, only the start node is known.
PriorityQueue<BoardNode> openSet = new PriorityQueue<BoardNode>();
openSet.add(start);
// For each node, which node it can most efficiently be reached from.
// If a node can be reached from many nodes, cameFrom will eventually contain the
// most efficient previous step.
Map<BoardNode, BoardNode> cameFrom = new HashMap<BoardNode, BoardNode>(); //the empty map

// For each node, the cost of getting from the start node to that node.
Map<BoardNode, Integer> gScore = new HashMap<BoardNode, Integer>(); //map with default value of Infinity
// The cost of going from start to start is zero.
gScore.put(start, 0);
// For each node, the total cost of getting from the start node to the goal
// by passing by that node. That value is partly known, partly heuristic.
Map<BoardNode, Integer> fScore = new HashMap<BoardNode, Integer>(); //map with default value of Infinity
// For the first node, that value is completely heuristic.
fScore.put(start, heuristic_cost_estimate(start, goal));

while (!openSet.isEmpty()){
  BoardNode current = openSet.poll(); //the node in openSet having the lowest fScore[] value
  if (current.equals(goal))
    return reconstruct_path(cameFrom, current);

  // openSet.Remove(current)
  closedSet.add(current);
  for (BoardNode neighbor : current.neighbors()){
    if (closedSet.contains(neighbor))
      continue;		// Ignore the neighbor which is already evaluated.
    // The distance from start to a neighbor
    int tentative_gScore = gScore.get(current) + dist_between(current, neighbor);
    if (!openSet.contains(neighbor)) // not in openSet	// Discover a new node
      openSet.add(neighbor);
    else if (tentative_gScore >= gScore.get(neighbor))
      continue;		// This is not a better path.

    // This path is the best until now. Record it!
    cameFrom.put(neighbor, current);
    gScore.put(neighbor, tentative_gScore);
    fScore.put(neighbor, gScore.get(neighbor) + heuristic_cost_estimate(neighbor, goal));
    // return 0;
  }
}

return null;
}

Deque<BoardNode> reconstruct_path(Map<BoardNode, BoardNode> cameFrom, BoardNode current){
  Deque<BoardNode> total_path = new ArrayDeque<BoardNode>();
  total_path.push(current);
  // total_path := [current]
  while (cameFrom.containsKey(current)){
    // while current in cameFrom.Keys:
    current = cameFrom.get(current);
    // current := cameFrom[current]
    total_path.add(current);
  }
  return total_path;
}
}

private class BoardNode{
  public Iterable<BoardNode> neighbors(){
    return null;
  }
  // abstract public int compareTo(BoardNode that);
}

private class BoardNode extends Node{
  BoardNode(Board board){

  }
  public int compareTo(BoardNode other){
    return 0;
  }
  public Iterable<Node> neighbors(){
    return null;
  }
}

private class AStarSolver extends AStar{
  int heuristic_cost_estimate(BoardNode start, BoardNode goal){
    return 0;
  }
  int dist_between(BoardNode current, BoardNode neighbor){
    return 1;
  }

  AStarSolver(Board board){
  }
}
public Solver(Board initial)           // find a solution to the initial board (using the A* algorithm)
{
}
public boolean isSolvable()            // is the initial board solvable?
{
  return false;
}
public int moves()                     // min number of moves to solve initial board; -1 if unsolvable
{
  return 0;
}
public Iterable<Board> solution()      // sequence of boards in a shortest solution; null if unsolvable
{
  return null;
}
// public static void main(String[] args) // solve a slider puzzle (given below)
// public static void main(String[] args) // unit tests (not graded)
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
/*
   function A*(start, goal)
// The set of nodes already evaluated.
closedSet := {}
// The set of currently discovered nodes that are not evaluated yet.
// Initially, only the start node is known.
openSet := {start}
// For each node, which node it can most efficiently be reached from.
// If a node can be reached from many nodes, cameFrom will eventually contain the
// most efficient previous step.
cameFrom := the empty map

// For each node, the cost of getting from the start node to that node.
gScore := map with default value of Infinity
// The cost of going from start to start is zero.
gScore[start] := 0
// For each node, the total cost of getting from the start node to the goal
// by passing by that node. That value is partly known, partly heuristic.
fScore := map with default value of Infinity
// For the first node, that value is completely heuristic.
fScore[start] := heuristic_cost_estimate(start, goal)

while openSet is not empty
current := the node in openSet having the lowest fScore[] value
if current = goal
return reconstruct_path(cameFrom, current)

openSet.Remove(current)
closedSet.Add(current)
for each neighbor of current
if neighbor in closedSet
continue		// Ignore the neighbor which is already evaluated.
// The distance from start to a neighbor
tentative_gScore := gScore[current] + dist_between(current, neighbor)
if neighbor not in openSet	// Discover a new node
openSet.Add(neighbor)
else if tentative_gScore >= gScore[neighbor]
continue		// This is not a better path.

// This path is the best until now. Record it!
cameFrom[neighbor] := current
gScore[neighbor] := tentative_gScore
fScore[neighbor] := gScore[neighbor] + heuristic_cost_estimate(neighbor, goal)

return failure

function reconstruct_path(cameFrom, current)
total_path := [current]
while current in cameFrom.Keys:
current := cameFrom[current]
total_path.append(current)
return total_path
*/
