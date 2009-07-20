import java.io.File;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import dataSet.DataSet;
import dataSet.DataSetFactory;


import reco.ChunkedNetflixRatingIterator;
import reco.ChunkedPMF;
import reco.PMF;
import reco.Rating;
import reco.RatingIterator;
import reco.Recommender;


public class Main {
  static double fix(double val){
    if(val < 1.0)return 1.0;
    if(val > 5.0)return 5.0;
    return val;
  }
  
  public static double rmse(Recommender recommender , RatingIterator testSet){
    testSet.reset();
    double sumSquare = 0.0;
    int setSize = 0;
    double as = 0.0;
    while(testSet.hasNext()){
      Rating rate = testSet.next();
      double diff = rate.getRate() - recommender.predictRate(rate.getUser(), rate.getMovie());
      as += recommender.predictRate(rate.getUser(), rate.getMovie());
      sumSquare += diff * diff;
      setSize++;      
    }
    System.out.println(as / setSize);
    return sumSquare / setSize;
  }
  
  public static double mae(Recommender recommender , RatingIterator testSet){
    testSet.reset();
    double sumAbs = 0.0;
    int setSize = 0;
    while(testSet.hasNext()){
      Rating rate = testSet.next();
      double diff = rate.getRate() - recommender.predictRate(rate.getUser(), rate.getMovie());
      sumAbs += Math.abs(diff);
      setSize++;      
    }
    return sumAbs / setSize;
  }

  public static void main(String[] args) {
    try{
      DataSet ds = DataSetFactory.getInstance("netflix");
      RatingIterator testSet = ds.getTestSet();
      List<RatingIterator> iters = new ArrayList<RatingIterator>();
      for(int chunk = 0 ; chunk < 100 ; chunk++){
        File in = new File("data/netflix/minibatch/train"+chunk+".dat");
        ChunkedNetflixRatingIterator iter = new ChunkedNetflixRatingIterator(in);
        iters.add(iter);
      }
//      RatingIterator trainSet = ds.getTrainSet();
//      iters.add(trainSet);
      ChunkedPMF pmf = new ChunkedPMF(ds.getUserNum(), ds.getItemNum(), iters , 10 , 5.0e-3 , 1.0e-2, 1.0e-3);
//      PMF pmf = new PMF(ds.getUserNum(), ds.getItemNum(), trainSet, 
//          10, 1.0e-4, 0.001 , 0.0001);
      int repNum = 0;
      PrintWriter rmseOut = new PrintWriter("data/result/nf_rmse_d10.out");      
      PrintWriter maeOut = new PrintWriter("data/result/nf_mae_d10.out");
      long totalTime = 0;
      long time = System.currentTimeMillis();
      while(pmf.update()){
        ++repNum;
        if(repNum % 1  == 0){
          long tdiff = System.currentTimeMillis() - time;
          totalTime += tdiff;
          System.out.println(repNum+" "+tdiff+" "+(1.0 * totalTime / repNum));
          time = System.currentTimeMillis();
        }
        rmseOut.println(repNum+" "+rmse(pmf , testSet));
        rmseOut.flush();
        maeOut.println(repNum+" "+mae(pmf , testSet));
        maeOut.flush();
        if(repNum >= 20000)break;
      }
      rmseOut.close();
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}
