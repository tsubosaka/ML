package reco;

public class Rating {
  int movie;
  int user;
  byte rate;
  public Rating(int m , int u , byte r){
    movie = m;
    user = u;
    rate = r;
  }
  public int getMovie(){
    return movie;
  }
  public int getUser(){
    return user;
  }
  public int getRate(){
    return rate;
  }
  @Override
  public String toString() {
    return "<" + movie + "," + user+","+rate+">";
  }
}