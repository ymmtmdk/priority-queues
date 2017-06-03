import edu.princeton.cs.algs4.*;
import java.util.*;

public class Solver {
  private class PriorityComparator implements Comparator<BoardNode>{
    public int compare(BoardNode a, BoardNode b){
      return Integer.compare(a.priority,  b.priority);
    }
  }

  private class ReversePriorityComparator implements Comparator<BoardNode>{
    public int compare(BoardNode a, BoardNode b){
      return Integer.compare(b.priority,  a.priority);
    }
  }

  private class ZeroPriorityComparator implements Comparator<BoardNode>{
    public int compare(BoardNode a, BoardNode b){
      return 0;
    }
  }

  abstract private class AStar{
    /*
        node              current node
 g                 the cost to reach current node
 f                 estimated cost of the cheapest path (root..node..goal)
 h(node)           estimated cost of the cheapest path (node..goal)
 cost(node, succ)  step cost function
 is_goal(node)     goal test
 successors(node)  node expanding function, expand nodes ordered by g + h(node)

 procedure ida_star(root)
   bound := h(root)
   loop
     t := search(root, 0, bound)
     if t = FOUND then return bound
     if t = ∞ then return NOT_FOUND
     bound := t
   end loop
 end procedure

 function search(node, g, bound)
   f := g + h(node)
   if f > bound then return f
   if is_goal(node) then return FOUND
   min := ∞
   for succ in successors(node) do
     t := search(succ, g + cost(node, succ), bound)
     if t = FOUND then return FOUND
     if t < min then min := t
   end for
   return min
 end function

       */
    class NodeScore{
      final BoardNode node;
      final int score;
      NodeScore(BoardNode node, int score){
        this.node = node;
        this.score = score;
      }
    }

    Deque<BoardNode> ida_star(BoardNode root, BoardNode goal, BoardNode goal2){
      NodeScore bound = new NodeScore(root, root.priority);
      while(true){
        NodeScore t = search(root, 0, bound, goal);
        if (t == null) return null;
        if (t.score == 0) return path(t.node);
        bound = t;
      }
    }

    NodeScore search(BoardNode node, int g, NodeScore bound, BoardNode goal){
      int f = g + node.priority;
      if (f > bound.score) return new NodeScore(node, f);
      if (node.equals(goal)) return new NodeScore(node, 0);
      NodeScore min = new NodeScore(null, 100000);
      for (BoardNode succ : node.neighbors()){
        if (node.isCritical(succ)){
          continue;
        }
        NodeScore t = search(succ, g + succ.priority - node.priority, bound, goal);
        if (t.score == 0) return t;
        if (t.score < min.score) min = t;
      }
      return min;
    }

    /*
       function IDDFS(node)
       for (depth = 0; ; depth++)
       found = DLS(node, depth)
       if (found != NULL) then
       return found
       function DLS(node, depth)
       if (IS_GOAL(node)) then
       return node
       if (depth > 0) then
       for each (child in EXPAND(node))
       found = DLS(child, depth - 1)
       if (found != NULL) then
       return found
       return NULL
       */

    Deque<BoardNode> iddfs(BoardNode start, BoardNode goal, BoardNode goal2){
      for (int depth = 0; ; depth++){
        BoardNode found = dls(start, depth, goal, goal2);
        if (found != null){
          if (found.equals(goal2)){
            return null;
          }
          return path(found);
        }
      }
    }

    BoardNode dls(BoardNode node, int depth, BoardNode goal, BoardNode goal2){
      if (node.equals(goal) || node.equals(goal2)){
        return node;
      }
      if (depth <= 0){
        return null;
      }
      if (node.priority > depth){
        return null;
      }

      for (BoardNode neighbor : node.neighbors()){
        if (!node.isCritical(neighbor)){
          BoardNode found = dls(neighbor, depth-1, goal, goal2);
          if (found != null){
            return found;
          }
        }
      }
      return null;
    }
    Deque<BoardNode> aStarDual(BoardNode start, BoardNode goal, BoardNode goal2){
      TreeSet<BoardNode> closedSet = new TreeSet<BoardNode>();
      TreeSet<BoardNode> closedSet2 = new TreeSet<BoardNode>();
      PriorityQueue<BoardNode> q = new PriorityQueue<BoardNode>(new ZeroPriorityComparator());
      PriorityQueue<BoardNode> q2 = new PriorityQueue<BoardNode>(new ZeroPriorityComparator());
      q.add(start);
      q2.add(goal);

      while (!q.isEmpty() && !q2.isEmpty()){
        BoardNode current = q.poll();
        BoardNode current2 = q2.poll();

        if (current.board.equals(goal2.board))
          return null;
        if (current2.board.equals(goal2.board))
          return null;

        closedSet.add(current);
        closedSet2.add(current2);

        if (closedSet.contains(current2)){
          for (BoardNode n : closedSet){
            if (n.equals(current2)){
              return path(n, current2);
            }
          }
        }
        if (closedSet2.contains(current)){
          for (BoardNode n : closedSet2){
            if (n.equals(current)){
              return path(current, n);
            }
          }
        }

        for (BoardNode neighbor : current.neighbors()){
          if (!closedSet.contains(neighbor) && !closedSet2.contains(neighbor)){
            if (current.prevNode() == null || !current.prevNode().board.equals(neighbor.board)){
              q.add(neighbor);
            }
          }
        }
        for (BoardNode neighbor : current2.neighbors()){
          if (!closedSet2.contains(neighbor) && !closedSet.contains(neighbor)){
            if (current2.prevNode() == null || !current2.prevNode().board.equals(neighbor.board)){
              q2.add(neighbor);
            }
          }
        }
      }

      return null;
    }

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

    Deque<BoardNode> aStarOpenSet(BoardNode start, BoardNode goal, BoardNode goal2){
      // PriorityQueue<BoardNode> closedSet = new PriorityQueue<BoardNode>();
      PriorityQueue<BoardNode> openSet = new PriorityQueue<BoardNode>();
      Set<BoardNode> closedSet = new TreeSet<BoardNode>();
      // Set<BoardNode> openSet = new TreeSet<BoardNode>();
      MinPQ<BoardNode> q = new MinPQ<BoardNode>(new PriorityComparator());
      openSet.add(start);
      q.insert(start);

      while (!q.isEmpty()){
        BoardNode item = openSet.poll();
        BoardNode current = q.delMin();
        if (current.board.equals(goal.board))
          return path(current);
        if (current.board.equals(goal2.board))
          return null;

        closedSet.add(current);
        for (BoardNode neighbor : current.neighbors()){
          if (!closedSet.contains(neighbor) && !openSet.contains(neighbor)){
            // if (!closedSet.contains(neighbor)){
            if (current.prevNode() == null || !current.prevNode().board.equals(neighbor.board)){
              openSet.add(neighbor);
              q.insert(neighbor);
            }
          }
        }
      }

      return null;
    }

    Deque<BoardNode> aStarNoSet(BoardNode start, BoardNode goal, BoardNode goal2){
      MinPQ<BoardNode> q = new MinPQ<BoardNode>(new PriorityComparator());
      q.insert(start);

      while (!q.isEmpty()){
        BoardNode current = q.delMin();
        if (current.board.equals(goal.board))
          return path(current);
        if (current.board.equals(goal2.board))
          return null;

        for (BoardNode neighbor : current.neighbors()){
          if (current.prevNode() == null || !current.prevNode().board.equals(neighbor.board)){
            q.insert(neighbor);
          }
        }
      }

      return null;
    }

    Deque<BoardNode> aStar5(BoardNode start, BoardNode goal){
      PriorityQueue<BoardNode> closedSet = new PriorityQueue<BoardNode>();
      MinPQ<BoardNode> q = new MinPQ<BoardNode>(new PriorityComparator());
      q.insert(start);

      while (!q.isEmpty()){
        BoardNode current = q.delMin();
        if (current.board.equals(goal.board))
          return path(current);
        closedSet.add(current);
        for (BoardNode neighbor : current.neighbors()){
          if (!closedSet.contains(neighbor)){
            if (current.prevNode() == null || !current.prevNode().board.equals(neighbor.board)){
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
      total_path.addLast(current);
      while (current.prevNode() != null){
        current = current.prevNode();
        total_path.addLast(current);
      }
      while (current2.prevNode() != null){
        current2 = current2.prevNode();
        total_path.addFirst(current2);
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
    private final int moves, priority;
    private final int id;
    private final BoardNode prevNode;
    boolean isForward;

    public BoardNode(BoardNode prevNode, Board board, int moves){
      this.prevNode = prevNode;
      this.board = board;
      this.id = _node_id++;
      this.moves = moves;
      if (id > 200000){
        // throw new ArithmeticException();
      }

      this.priority = (moves + board.manhattan());
      this.isForward = true;
      // this.priority = 0;
    }

    public BoardNode prevNode(){
      return prevNode;
    }

    public Iterable<BoardNode> neighbors(){
      PriorityQueue<BoardNode> q = new PriorityQueue<BoardNode>(new PriorityComparator());
      // Deque<BoardNode> q = new ArrayDeque<BoardNode>();
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
      // return priority - that.priority;
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

