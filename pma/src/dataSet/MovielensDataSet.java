package dataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import reco.Rating;
import reco.RatingArrayIterator;
import reco.RatingIterator;

class MovielensDataSet extends DataSet {
  final static private int SEED = 1111;
  Rating tests[];
  Rating trains[];
  void init(){
    try{
      File file = new File("data/movielens/ratings.dat");
      List<Rating> rlist = new ArrayList<Rating>();
      BufferedReader br = new BufferedReader(new FileReader(file));
      while(true){
        String line = br.readLine();
        if(line == null)break;
        String arr[] = line.split("::");
        int user = Integer.parseInt(arr[0]) - 1;
        int movie = Integer.parseInt(arr[1]) - 1;
        int rate = Integer.parseInt(arr[2]);
        rlist.add(new Rating(movie, user, (byte)rate));
      }
      Random rnd = new Random(SEED);
      Collections.shuffle(rlist, rnd);
      int trainNum = (rlist.size() * 99) / 100;
      int testNum = rlist.size() - trainNum;
      trains = new Rating[trainNum];
      tests = new Rating[testNum];
      for(int i = 0 ; i < trainNum ; i++){
        trains[i] = rlist.get(i);
      }
      for(int i = 0 ; i < testNum ; i++){
        tests[i] = rlist.get(trainNum + i);
      }
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  MovielensDataSet() {
    super.userNum = 6040;
    super.itemNum = 3952;
    init();
  }
  @Override
  public RatingIterator getTestSet() {
    return new RatingArrayIterator(tests);
  }

  @Override
  public RatingIterator getTrainSet() {
    return new RatingArrayIterator(trains);
  }
  
  public static void main(String[] args) throws Exception{
    new MovielensDataSet();
  }
}
