import edu.princeton.cs.algs4.*
import org.junit.Test
import org.junit.Assert.*
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*

class TestPQ{
  @Test fun test(){
    val q = MaxPQ<Int>(1000)
    /* val q = edu.princeton.cs.algs4.MaxPQ<Int>(1000) */
    assertEquals(0, q.size())
    assertEquals(true, q.isEmpty())
    q.insert(1)
    assertEquals(1, q.size())
    assertEquals(false, q.isEmpty())
    assertEquals(1, q.delMax())
    q.insert(2)
    q.insert(3)
    assertEquals(3, q.delMax())
    assertEquals(2, q.delMax())
    q.insert(2)
    q.insert(3)
    q.insert(1)
    q.insert(3)
    assertEquals(3, q.delMax())
    assertEquals(3, q.delMax())
    assertEquals(2, q.delMax())
    assertEquals(1, q.delMax())
  }
}

object Util{
  fun board(src: String): Board{
    val inf = In(java.util.Scanner(src))
    return board(inf)
  }

  fun blocks(inf: In): Array<IntArray>{
    val n = inf.readInt()
    val blocks = Array<IntArray>(n){ IntArray(n){0}}

    for (i in 0..n-1){
      for (j in 0..n-1){
        blocks[i][j] = inf.readInt()
      }
    }
    return blocks
  }

  fun blocks(src: String): Array<IntArray>{
    val inf = In(java.util.Scanner(src))
    val n = inf.readInt()
    val blocks = Array<IntArray>(n){ IntArray(n){0}}

    for (i in 0..n-1){
      for (j in 0..n-1){
        blocks[i][j] = inf.readInt()
      }
    }
    return blocks
  }

  fun board(inf: In): Board{
    return Board(blocks(inf))
  }

  fun solver(inf: In): Solver{
    /* doPrivateMethod(Board::class, "fact", 1) */
    return Solver(board(inf))
  }

  fun doPrivateMethod(target: Any, field: String, n: Int): Any{
    /*
    val c = Util.javaClass
    val method = c.getDeclaredMethod("f")
    method.setAccessible(true)
    method.invoke(Util)
      java.lang.reflect.Method method = Board.class.getMethod(name, int.class);
      method.setAccessible(true);
      method.invoke(null, n);
    */
    val c = target.javaClass
    val method = c.getMethod(field, Int.javaClass)
    method.setAccessible(true)
    return method.invoke(null, n)
  }

  private fun f(){
    println("f")
  }
}

class TestBoard{
  val board1 = """
  2
  1  2
  3  0
  """

  val board2 = """
  3
  8 1 3
  4 0 2
  7 6 5
  """

  val board3 = """
  4
  1 2 3 4
  5 6 7 8
  9 10 11 12
  13 14 15 0
  """

  @Test fun testToString(){
    /* val brd = board(simpleBoard()) */
    /* val str = "2\n 1  2\n 3  0\n" */
    /* assertEquals(str, brd.toString()) */
  }

  @Test fun testEquals(){
    val b1 = Util.board(board1)
    val b2 = Util.board(board1)
    assertEquals(b1, b2)
    assertNotEquals(b1, null)
    assertNotEquals(b1, 1)
  }

  @Test fun testHamming(){
    assertEquals(0, Util.board(board1).hamming())
    assertEquals(5, Util.board(board2).hamming())
  }

  @Test fun testManhattan(){
    assertEquals(0, Util.board(board1).manhattan())
    assertEquals(10, Util.board(board2).manhattan())
  }

  @Test fun testIsGoard(){
    assertEquals(true, Util.board(board1).isGoal())
    assertEquals(false, Util.board(board2).isGoal())
  }

  @Test fun testHashCode(){
    /* Board.fa("calcHash", Util.blocks(board1)) */
    assertEquals(23, Board.calcHash(Util.blocks(board1)))
    assertEquals(20922789887999, Board.calcHash(Util.blocks(board3)))
  }
}

class TestSolver{
  @Test fun testSolver(){
    val sl = Util.solver(In("8puzzle/puzzle2x2-00.txt"))
    assertEquals(true, sl.isSolvable())
    assertEquals(0, sl.moves())
    for (n in 1..6){
      assertEquals(n, Util.solver(In("8puzzle/puzzle2x2-%02d.txt".format(n))).moves())
    }
    for (n in 1..6){
      assertEquals(n, Util.solver(In("8puzzle/puzzle3x3-%02d.txt".format(n))).moves())
    }
    for (n in 1..2){
      assertEquals(n, Util.solver(In("8puzzle/puzzle4x4-%02d.txt".format(n))).moves())
    }
    println(Board.fi("fact", 10))
    println(Board.fi("fact", 10))
  }

  @Test fun callMain(){
    Solver.main(arrayOf("8puzzle/puzzle2x2-00.txt"))
    Solver.main(arrayOf("8puzzle/puzzle2x2-01.txt"))
    Solver.main(arrayOf("8puzzle/puzzle3x3-00.txt"))
    Solver.main(arrayOf("8puzzle/puzzle04.txt"))
    Solver.main(arrayOf("8puzzle/puzzle4x4-01.txt"))
    /* Solver.main(arrayOf("8puzzle/puzzle4x4-44.txt")) */
  }
}
