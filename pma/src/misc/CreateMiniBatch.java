package misc;

import java.io.File;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class CreateMiniBatch {
  static void shuffle(long array[]){
    int n = array.length;
    long seed = Arrays.hashCode(array);
    Random rand = new Random(seed);
    for(int i = 0 ; i < n ; i++){
      int j = rand.nextInt(n - i) + i;
      long tmp = array[i];
      array[i] = array[j];
      array[j] = tmp;
    }
  }
  public static void main(String[] args) throws Exception{
    Scanner sc = new Scanner(new File("data/netflix/train.dat"));
    long array[] = new long[99072112];
    int cnt = 0;
    while(sc.hasNext()){
      int m = sc.nextInt();
      int n = sc.nextInt();
      if(m % 100 == 0)System.out.println(m);
      for(int i = 0 ; i < n ; i++){
        int u = sc.nextInt();
        int r = sc.nextInt();
        long l = m;
        l = (l << 32l) | (u << 3) | r;
        array[cnt++] = l;
      }
    }
    shuffle(array);    
    //separate Batch File
    int chunkNum = 10;
    for(int i = 0 ; i < chunkNum ; i++){
      PrintWriter out = new PrintWriter("data/netflix/minibatch10/train"+i+".dat");
      for(int j = i ; j < array.length ; j += chunkNum){
        long l = array[j];
        out.println(l);
      }
      out.close();
    }
  }
}