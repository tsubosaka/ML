import java.io.PrintWriter;

import dataSet.DataSet;
import dataSet.DataSetFactory;


import reco.PMF;
import reco.Rating;
import reco.RatingIterator;


public class Main {
  static double fix(double val){
    if(val < 1.0)return 1.0;
    if(val > 5.0)return 5.0;
    return val;
  }
  
  public static double rmse(PMF pmf , RatingIterator testSet){
    testSet.reset();
    double sumSquare = 0.0;
    int setSize = 0;
    while(testSet.hasNext()){
      Rating rate = testSet.next();
      double diff = rate.getRate() - fix(pmf.predictRate(rate.getUser(), rate.getMovie()));
      sumSquare += diff * diff;
      setSize++;      
    }
    return sumSquare / setSize;
  }
  public static double mae(PMF pmf , RatingIterator testSet){
    testSet.reset();
    double sumAbs = 0.0;
    int setSize = 0;
    while(testSet.hasNext()){
      Rating rate = testSet.next();
      double diff = rate.getRate() - fix(pmf.predictRate(rate.getUser(), rate.getMovie()));
      sumAbs += Math.abs(diff);
      setSize++;      
    }
    return sumAbs / setSize;
  }

  public static void main(String[] args) {
    try{
      DataSet ds = DataSetFactory.getInstance("netflix");
      RatingIterator testSet = ds.getTestSet();
      RatingIterator trainSet = ds.getTrainSet();
      PMF pmf = new PMF(ds.getUserNum(), ds.getItemNum(), trainSet, 
          10, 1.0e-4, 0.001 , 0.0001);
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
        rmseOut.println(repNum+" "+rmse(pmf , testSet)+" "+rmse(pmf , trainSet));
        rmseOut.flush();
        maeOut.println(repNum+" "+mae(pmf , testSet)+" "+mae(pmf , trainSet));
        maeOut.flush();
        if(repNum >= 500)break;
      }
      rmseOut.close();
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}
