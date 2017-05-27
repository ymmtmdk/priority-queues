public class MaxPQ<Key extends Comparable<Key>>
{
  /*
               1
       2               3
   4       5       6       7
 8   9  10  11  12  13  14  15

     */
  private Key[] pq;
  private int N;
  public MaxPQ(int capacity)
  {
    pq = (Key[]) new Comparable[capacity+1];
  }

  public boolean isEmpty()
  {
    return N == 0;
  }

  public int size(){
    return N;
  }

  public void insert(Key key){
    pq[++N] = key;
    swim(N);
  }

  public Key delMax()
  {
    Key key = pq[1];
    pq[1] = null;
    exch(1, N--);
    sink(1);
    return key;
  }

  private void swim(int k){
    while(k > 1 && less(k/2, k)){
      exch(k/2, k);
      k /= 2;
    }
  }

  private void sink(int parent)
  {
    while (parent*2 <= N){
      int children = parent*2;
      if (children < N && less(children, children+1)) children++;
      if (!less(parent, children)) break;
      exch(parent, children);
      parent = children;
    }
  }

  private boolean less(int i, int j)
  {
    return pq[i].compareTo(pq[j]) < 0;
  }

  private void exch(int i, int j)
  {
    Key t = pq[i];
    pq[i] = pq[j];
    pq[j] = t;
  }
}

