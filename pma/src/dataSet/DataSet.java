package dataSet;

import reco.RatingIterator;

public abstract class DataSet {
  protected int itemNum;
  protected int userNum;
  public int getItemNum(){
    return itemNum;
  }
  public int getUserNum(){
    return userNum;
  }
  abstract public RatingIterator getTrainSet();
  abstract public RatingIterator getTestSet();
}
