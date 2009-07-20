package reco;
import java.util.Arrays;
import java.util.Random;

public class PMF {
  int userNum;
  int itemNum;
  int factorNum;
  double learningRate;
  double userReg;
  double itemReg;
  RatingIterator iter;
  double userMatrix[][];
  double um_next[][];
  double um_grad[][];
  double itemMatrix[][];  
  double im_next[][];
  double im_grad[][];
  Random rand;
  double cur_like;
  public PMF(int userNum , int itemNum , RatingIterator iter , int factorNum , double learningRate , double ureg , double ireg ){
    this.userNum = userNum;
    this.itemNum = itemNum;
    this.factorNum = factorNum;
    this.learningRate = learningRate;
    this.userReg = ureg;
    this.itemReg = ireg;
    this.iter = iter;
    userMatrix = new double[userNum][factorNum];
    itemMatrix = new double[itemNum][factorNum];
    rand = new Random(this.hashCode());
    initMatrix(userMatrix);
    initMatrix(itemMatrix);    
    um_next = new double[userNum][factorNum];
    im_next = new double[itemNum][factorNum];
    um_grad = new double[userNum][factorNum];
    im_grad = new double[itemNum][factorNum];
    cur_like = likelihood(userMatrix, itemMatrix);
  }
  
  public PMF(int userNum , int itemNum , RatingIterator iter){
    this(userNum , itemNum , iter , 10 , 1.0e-3 , 1.0e-3 , 1.0e-4);
  }
  
  void initMatrix(double matrix[][]){
    for(int i = 0 ; i < matrix.length ; i++){
      for(int j = 0 ; j < matrix[i].length ; j++){
        matrix[i][j]  = rand.nextDouble();
      }
    }
  }
  
  public double predictRate(int user , int item){
    return product(userMatrix[user] , itemMatrix[item]);
  }
  
  double product(double users[] , double items[]){
    double ret = 0.0;
    for(int f = 0 ; f < factorNum ; f++){
      ret += users[f] * items[f];
    }
    return ret;
  }
  
  double norm(double matrix[][]){
    double norm = 0.0;
    for(int i = 0 ; i < matrix.length ; i++){
      for(int f = 0 ; f < matrix[i].length ; f++){
        norm += matrix[i][f] * matrix[i][f];
      }
    }
    return norm;
  }
  
  public double likelihood(double users[][] , double items[][]){
    double ret = 0.0;
    iter.reset();
    while(iter.hasNext()){
      Rating r = iter.next();
      double rdiff = r.rate - product(users[r.user], items[r.movie]);
      ret += rdiff * rdiff;
    }
    double reg = userReg * norm(users) + itemReg * norm(items);
    return -(ret + reg);
  }
  
  private void zeroFill(double matrix[][]){
    for(int i = 0 ; i < matrix.length ; i++)
      Arrays.fill(matrix[i], 0);
  }
  
  public boolean update(){
    zeroFill(im_grad); zeroFill(um_grad);
    int cnt = 0;
    iter.reset();
    while(iter.hasNext()){
      cnt++;
      Rating r = iter.next();
      double rdiff = r.rate - product(userMatrix[r.user], itemMatrix[r.movie]);
      for(int f = 0 ; f < factorNum ; f++){
        um_grad[r.user][f] += - rdiff *  itemMatrix[r.movie][f] + userReg * userMatrix[r.user][f];
        im_grad[r.movie][f] += - rdiff * userMatrix[r.user][f] + itemReg * itemMatrix[r.movie][f];
      }
    }
    while(this.learningRate > 1e-10){
      test_update(um_grad, im_grad);
      double next_like = likelihood(um_next, im_next);
      System.out.println(cur_like+" "+next_like+" "+learningRate);      
      if(next_like > cur_like){
        next();
        this.learningRate *= 1.25;
        this.cur_like = next_like;
        return true;
      }else{
        this.learningRate *= 0.5;
      }
    }
    return false;
  }
  void next(){
    for(int i = 0 ; i < userNum ; i++){
      System.arraycopy(um_next[i], 0, userMatrix[i], 0, factorNum);
    }
    for(int i = 0 ; i < itemNum ; i++){
      System.arraycopy(im_next[i], 0, itemMatrix[i], 0, factorNum);      
    }
  }
              
  void test_update(double u_grad[][] , double i_grad[][]){
    for(int i = 0 ; i < userNum ; i++){
      for(int f = 0 ; f < factorNum ; f++){
        um_next[i][f] = userMatrix[i][f] - learningRate * u_grad[i][f];
      }
    }
    for(int i = 0 ; i < itemNum ; i++){
      for(int f = 0 ; f < factorNum ; f++){
        im_next[i][f] = itemMatrix[i][f] - learningRate * i_grad[i][f];
      }
    }
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + factorNum;
    result = prime * result + Arrays.hashCode(itemMatrix);
    result = prime * result + itemNum;
    long temp;
    temp = Double.doubleToLongBits(learningRate);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(userReg);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(itemReg);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }
}