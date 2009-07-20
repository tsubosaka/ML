package reco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class ChunkedNetflixRatingIterator implements RatingIterator {
  SoftReference<Rating[]> cacheRatings;
  int index;
  Rating rs[];
  File inputFile;
  public ChunkedNetflixRatingIterator(File in) {
    inputFile = in;
    cacheRatings = new SoftReference<Rating[]>(null);
  }
  private void load(){
    try{
      BufferedReader br = new BufferedReader(new FileReader(inputFile));
      List<Rating> rlist = new ArrayList<Rating>();
      while(true){
        String line = br.readLine();
        if(line == null)break;
        long l = Long.parseLong(line);
        int m = (int) (l >> 32l);
        int u = ((int) (l & 0xffffffffl)) >> 3;
        byte r = (byte) (l & 7);     
        rlist.add(new Rating(m, u, r));
      }
      rs = rlist.toArray(new Rating[0]);     
    }catch (Exception e) {
      e.printStackTrace();
    }    
    cacheRatings = new SoftReference<Rating[]>(rs);
  }
  @Override
  public void reset() {
    rs = cacheRatings.get();
    if(rs == null){
      load();
    }
    index = 0;
  }

  @Override
  public boolean hasNext() {
    if(index == rs.length){
      rs = null;
      return false;
    }
    return true;
  }

  @Override
  public Rating next() {
    return rs[index++];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
