import java.util.*;

public class RandomNumber{
  private static Random randomizer = new Random(new Date().getTime());
  
  public static int getRand(int min, int max) {
    
    int n = max - min + 1;
    int i = randomizer.nextInt(n);
    return min + i;
  }
  public static int getNormalRand(int min, int max, int mid, int dist){
    double spread = randomizer.nextGaussian()*dist+mid;
    if(spread < min) spread = min;
    if(spread > max) spread = max;
    return (int)Math.round(spread);
  }
  
}