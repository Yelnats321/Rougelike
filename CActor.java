import java.util.*;

class CActor extends CBase{
  private int speed;
  public int currSpeed;
  private Action action;
  private static LinkedList<CActor> actors = new LinkedList<CActor>();
  public CActor(Entity o, int s, Action a){
    super(o);
    if(s <= 0){
      s = 1;
      System.out.println("Tried to initialize actor with a speed of less than 1");
    }
    speed = s;
    currSpeed = s;
    action = a;
    ListIterator<CActor> iter = actors.listIterator(0);
    int pos = 0;
    //put it behind the last thing with the same speed
    while(iter.hasNext()){
      if(iter.next().speed > speed){
        break;
      }
      pos++;
    }
    System.out.println(pos);
    actors.add(pos, this);
  }
  public void act(){
    action.act();
  }
  public int getSpeed(){ return speed;}
  public static LinkedList<CActor> getActors(){return actors;}
}