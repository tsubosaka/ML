package misc;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Trans {
  static final String probeFile = 
    "E:/netflix/download/probe.txt";
  static final String trainDir = 
    "E:/netflix/download/training_set";
	public static void main(String[] args) throws IOException{
	  Scanner sc = new Scanner(new File(probeFile));
	  int currentMovie = -1;
	  Set<Long> probeSet = new HashSet<Long>();
	  while(sc.hasNext()){
	    String str = sc.next();
	    if(str.endsWith(":")){
	      currentMovie = Integer.parseInt(str.substring(0, str.length() -1)) - 1;
	    }else{
	      long user = Long.parseLong(str);
	      long e = (user << 20L) | currentMovie;
	      probeSet.add(e);
	    }
	  }
	  int utable[] = new int[2649430];
	  Arrays.fill(utable, -1);
	  int cu = 0;
	  File tDir = new File(trainDir);
	  PrintWriter trainOut = new PrintWriter("data/train.dat");
	  PrintWriter probeOut = new PrintWriter("data/probe.dat");
	  for(File f : tDir.listFiles()){
	    sc = new Scanner(f);
	    String str = sc.nextLine();
	    int movie = Integer.parseInt(
	        str.substring(0, str.length() -1)) - 1;
	    List<Long> ts = new ArrayList<Long>();
	    List<Long> ps = new ArrayList<Long>();
	    while(sc.hasNext()){
	      str = sc.nextLine();
	      String arr[] = str.split(",");
	      long user = Long.parseLong(arr[0]);
	      int rate = Integer.parseInt(arr[1]);
        long e = (user << 20L) | movie;
        if(probeSet.contains(e)){
          if(utable[(int)user] < 0){
            utable[(int)user] = cu++;
          }
          user = utable[(int)user];
          ps.add((user << 3) + rate);
        }else{
          if(utable[(int)user] < 0){
            utable[(int)user] = cu++;
          }
          user = utable[(int)user];
          ts.add((user << 3) + rate);
        }
	    }
	    output(trainOut , ts , movie);
      output(probeOut , ps , movie);
	  }
	  System.out.println(cu);
	  trainOut.close();
	  probeOut.close();
	}
	static void output(PrintWriter out , List<Long> list , int m){
	  out.println(m+" "+list.size());
	  for(long l : list){
	    out.println((l >> 3) + " " + (l & 7));
	  }
	}
}
