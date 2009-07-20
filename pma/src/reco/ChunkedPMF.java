package reco;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChunkedPMF implements Recommender{
  int userNum;
  int itemNum;
  int factorNum;
  double learningRate;
  double momentum = 1.0;
  double userReg;
  double itemReg;
  List<RatingIterator> iters;
  double userMatrix[][];
  double um_next[][];
  double um_grad[][];
  double itemMatrix[][];  
  double im_next[][];
  double im_grad[][];
  Random rand;
  public ChunkedPMF(int userNum , int itemNum , List<RatingIterator> iters , int factorNum , double learningRate , double ureg , double ireg ){
    this.userNum = userNum;
    this.itemNum = itemNum;
    this.factorNum = factorNum;
    this.learningRate = learningRate;
    this.userReg = ureg;
    this.itemReg = ireg;
    this.iters = iters;
    userMatrix = new double[userNum][factorNum];
    itemMatrix = new double[itemNum][factorNum];
    rand = new Random(this.hashCode());
    initMatrix(userMatrix);
    initMatrix(itemMatrix);    
    um_next = new double[userNum][factorNum];
    im_next = new double[itemNum][factorNum];
    um_grad = new double[userNum][factorNum];
    im_grad = new double[itemNum][factorNum];
  }
  
  public ChunkedPMF(int userNum , int itemNum , List<RatingIterator> iters){
    this(userNum , itemNum , iters , 10 , 1.0e-3 , 1.0e-3 , 1.0e-4);
  }
  
  void initMatrix(double matrix[][]){
    for(int i = 0 ; i < matrix.length ; i++){
      for(int j = 0 ; j < matrix[i].length ; j++){
        matrix[i][j]  = Math.random();
      }
    }
  }
  
  @Override
  public double predictRate(int user , int item){
    double p = product(userMatrix[user] , itemMatrix[item]);
//    return p;
    return p * (K - 1) + 1.0;
  }
  
  double product(double users[] , double items[]){
    double ret = 0.0;
    for(int f = 0 ; f < factorNum ; f++){
      ret += users[f] * items[f];
    }
//    return ret;
    return 1.0 / (1.0 + Math.exp(- ret));
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
  final static double K = 5.0;
  double t(double x){
//    return x;
    return (x - 1.0) / (K - 1);
  }
  public double likelihood(double users[][] , double items[][] , RatingIterator iter){
    double ret = 0.0;
    iter.reset();
    while(iter.hasNext()){
      Rating r = iter.next();
      double rdiff = t(r.rate) - product(users[r.user], items[r.movie]);
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
    int cnt = 0;
    Collections.shuffle(iters , rand);
    boolean converge = true;
    for(RatingIterator iter : iters){
      zeroFill(im_grad); zeroFill(um_grad);
      iter.reset();
      while(iter.hasNext()){
        Rating r = iter.next();
        double product = product(userMatrix[r.user], itemMatrix[r.movie]);
        double rdiff = t(r.rate) - product;
        for(int f = 0 ; f < factorNum ; f++){
          um_grad[r.user][f] += - rdiff *  itemMatrix[r.movie][f] + userReg * userMatrix[r.user][f] * (1 - product) * product;
          im_grad[r.movie][f] += - rdiff * userMatrix[r.user][f] + itemReg * itemMatrix[r.movie][f] * (1 - product) * product;
        }
      }
//      double cur_like = likelihood(userMatrix, itemMatrix , iter);
      double lr = this.learningRate;
      for(int test = 0 ; test < 1 ; test++){
        test_update(um_grad, im_grad , lr);
        next();
        converge = false;
        return true;
//        double next_like = likelihood(um_next, im_next , iter);
//        System.out.println(cur_like +" "+next_like+" "+lr+" "+cnt);
//        if(next_like > cur_like){
//          next();
//          converge = false;
//          break;
////          return true;
//        }else{
//          lr *= .5;
//        }
      }
      System.out.println(cnt);
      cnt++;
    }
//    Collections.reverse(iters);
    return !converge;
  }
  void next(){
    for(int i = 0 ; i < userNum ; i++){
      System.arraycopy(um_next[i], 0, userMatrix[i], 0, factorNum);
    }
    for(int i = 0 ; i < itemNum ; i++){
      System.arraycopy(im_next[i], 0, itemMatrix[i], 0, factorNum);      
    }
  }
              
  void test_update(double u_grad[][] , double i_grad[][] , double lr){
    for(int i = 0 ; i < userNum ; i++){
      for(int f = 0 ; f < factorNum ; f++){
        um_next[i][f] = userMatrix[i][f] - lr * u_grad[i][f];
      }
    }
    for(int i = 0 ; i < itemNum ; i++){
      for(int f = 0 ; f < factorNum ; f++){
        im_next[i][f] = itemMatrix[i][f] - lr * i_grad[i][f];
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