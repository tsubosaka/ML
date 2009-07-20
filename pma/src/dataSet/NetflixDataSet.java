package dataSet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import reco.NetflixRatingIterator;
import reco.Rating;
import reco.RatingArrayIterator;
import reco.RatingIterator;

class NetflixDataSet extends DataSet {
  NetflixDataSet() {
    super.itemNum = 17770;
    super.userNum = 480189;
  }
  RatingIterator testIter;
  @Override
  public RatingIterator getTestSet(){
    try{
      if(testIter == null){
        File trainFile = new File("data/netflix/probe.dat");      
        RatingIterator it = new NetflixRatingIterator(trainFile);
        List<Rating> rs = new ArrayList<Rating>();
        while(it.hasNext()){
          rs.add(it.next());
        }
        testIter = new RatingArrayIterator(rs.toArray(new Rating[0]));        
      }
      return testIter;
    }catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public RatingIterator getTrainSet() {
    File trainFile = new File("data/netflix/train.dat");
    try{
      RatingIterator it = new NetflixRatingIterator(trainFile);
      return it;
    }catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
