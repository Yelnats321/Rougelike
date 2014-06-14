//everything that has a position on the map will use this, aka 
//everything basically not including map elements themselves
import java.util.*;

class CMoving extends CBase{
  private Position pos;
  private boolean collideable;
  private static GameMap map;
  private static EntityManager entityManager;

  public CMoving(Entity o, int x, int y){
    this(o, x, y, true);
  }
  public CMoving(Entity o,int x, int y, boolean col){
    super(o);
    pos = new Position(x, y);
    collideable = col;
    if(map!=null)
      map.setEntity(pos, owner);
  }
  @Override
  public void destroy(){
    map.setEntity(pos, null);
  }
  public static void setMap(GameMap m){
    map = m;
  }
  public static void setManager(EntityManager e){
    entityManager = e;
  }

  public boolean checkMove(Direction d){
    if(!map.inBounds(pos.add(d.offset))) return false;
    return(map.get(pos.add(d.offset)).canWalk());
  }
  public boolean canAttack(Direction d){
    if(!map.inBounds(pos.add(d.offset))) return false;
    return(!map.get(pos.add(d.offset)).tileType.isCollideable() && map.get(pos.add(d.offset)).getEntity() != null && map.get(pos.add(d.offset)).getEntity().getComponent(CResources.class) != null);
  }
  public boolean move(Direction d){
    if(d == Direction.ABOVE || d == Direction.BELOW){
      if(d==Direction.ABOVE && map.get(pos.x,pos.y).tileType.ID == GameMap.STAIRSUP){
        entityManager.goDown();
      }
      else if(d==Direction.BELOW && map.get(pos.x,pos.y).tileType.ID == GameMap.STAIRSDOWN){
        entityManager.goUp();
      }
    }
    if(checkMove(d)){
      map.setEntity(pos, null);
      pos = pos.add(d.offset);
      map.setEntity(pos, owner);
      return true;
    }
    return false;
  }
  public boolean attack(Direction d){
    CResources res = (CResources)owner.getComponent(CResources.class);
    if(canAttack(d)){
      ((CResources)map.get(pos.add(d.offset)).getEntity().getComponent(CResources.class)).damage(res.getAttack());
      return true;
    }
    return false;
  }
  public boolean isCollideable(){
    return collideable;
  }
  public int getX(){return pos.x;}
  public int getY(){return pos.y;}
  public Position getPos(){return pos;}
  public void setPos(Position p){pos = p;}
}