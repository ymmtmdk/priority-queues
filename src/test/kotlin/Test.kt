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

class TestBoard{
  @Test fun test(){
  }

  fun board(src: String): Board{
    val inf = In(java.util.Scanner(src))
    return board(inf)
  }

  fun board(inf: In): Board{
    val n = inf.readInt()
    val blocks = Array<IntArray>(n){ IntArray(n){0}}

    for (i in 0..n-1){
      for (j in 0..n-1){
        blocks[i][j] = inf.readInt()
      }
    }

    return Board(blocks)
  }

  fun solver(inf: In): Solver{
    return Solver(board(inf))
  }

  @Test fun testToString(){
    /* val brd = board(simpleBoard()) */
    /* val str = "2\n 1  2\n 3  0\n" */
    /* assertEquals(str, brd.toString()) */
  }

  @Test fun testEquals(){
    val b1 = board(simpleBoard())
    val b2 = board(simpleBoard())
    assertEquals(b1, b2)
    assertNotEquals(b1, null)
    assertNotEquals(b1, 1)
  }

  fun simpleBoard(): String{
    val src = """
    2
    1  2
    3  0
    """
    return src
  }

  @Test fun testHamming(){
    val src = """
    3
    8 1 3
    4 0 2
    7 6 5
    """

    val brd = board(src)
    assertEquals(0, board(simpleBoard()).hamming())
    assertEquals(5, brd.hamming())

  }
  @Test fun testIsGoard(){
    val brd = board(simpleBoard())
    assertEquals(true, brd.isGoal())
  }

  @Test fun testSolver(){
    val sl = solver(In("8puzzle/puzzle2x2-00.txt"))
    assertEquals(true, sl.isSolvable())
    assertEquals(0, sl.moves())
    for (n in 1..6){
      assertEquals(n, solver(In("8puzzle/puzzle2x2-%02d.txt".format(n))).moves())
    }
    for (n in 1..6){
      assertEquals(n, solver(In("8puzzle/puzzle3x3-%02d.txt".format(n))).moves())
    }
    for (n in 1..16){
      assertEquals(n, solver(In("8puzzle/puzzle4x4-%02d.txt".format(n))).moves())
    }
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
