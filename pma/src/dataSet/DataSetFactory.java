package dataSet;

public class DataSetFactory {
  public static DataSet getInstance(String dataSetName){
    if(dataSetName.equalsIgnoreCase("netflix")){
      return new NetflixDataSet();
    }else if(dataSetName.equals("movielens")){
      return new MovielensDataSet();
    }
    return null;
  }
}
