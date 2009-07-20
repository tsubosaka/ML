package reco;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class NetflixRatingIterator implements RatingIterator {
  BufferedReader br;
  String line;
  boolean mstart;
  int curMovie;
  int userNum;
  int userIndex;
  File inputFile;
  public NetflixRatingIterator(File file) throws IOException{
    br = new BufferedReader(new FileReader(file) , 1 << 25);
    inputFile = file;
    line = readLine();
    mstart = true;
  }
  @Override
  public void reset(){
    try{
      if(br != null){
        br.close();
      }
      br = new BufferedReader(new FileReader(inputFile));
      line = readLine();
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  @Override
  public boolean hasNext() {
    if(line == null){
      try{
        br.close();
        br = null;
      }catch (Exception e) {
        e.printStackTrace();
      }
      return false;
    }
    return true;
  }
  private String readLine(){
    try{
      return br.readLine();
    }catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  
  long parse(char[] line){
    long l1 = 0;
    int i = 0;
    for( ; Character.isDigit(line[i]) ; i++){
      l1 = l1 * 10 + (line[i] - '0');
    }
    i++; //skip space
    long l2 = 0;
    for( ; i < line.length && Character.isDigit(line[i]) ; i++){
      l2 = l2 * 10 + (line[i] - '0');
    }
    return (l1 << 32l) |  l2;
  }
  
  @Override
  public Rating next() {
    if(mstart){
      long l = parse(line.toCharArray());
      curMovie = (int)(l >> 32l);
      userNum = (int)(l & 0xffffffff);
      line = readLine();
      userIndex = 0;
      mstart = false;
    }
    long l = parse(line.toCharArray());
    int u = (int)(l >> 32l);
    int r = (int)(l & 0xffffffff);
    Rating rate = new Rating(curMovie, u, (byte)r);
    userIndex++;
    if(userIndex == userNum){
      mstart = true;
    }
    line = readLine();
    return rate;
  }
  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}