package reco;

public class RatingArrayIterator implements RatingIterator {
  Rating ratings[];
  int index;
  public RatingArrayIterator(Rating[] rs) {
    ratings = rs;
    index = 0;
  }
  @Override
  public void reset() {
    index = 0;
  }

  @Override
  public boolean hasNext() {
    return index < ratings.length;
  }

  @Override
  public Rating next() {
    return ratings[index++];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}