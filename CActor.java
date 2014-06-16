import java.util.*;

class CActor extends CBase{
  private int speed;
  public int currSpeed;
  private Action action;
  private static List<CActor> actors = new LinkedList<CActor>();
  public CActor(Entity o, int s, Action a){
    super(o);
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
    actors.add(pos, this);
  }
  public void act(){
    action.act();
  }
  @Override
  protected void destroy(){
    actors.remove(this);
  }
  public int getSpeed(){ return speed;}
  public static List<CActor> getActors(){return actors;}
}