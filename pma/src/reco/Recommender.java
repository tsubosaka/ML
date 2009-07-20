package reco;

public interface Recommender {
  public double predictRate(int user , int item);
}
