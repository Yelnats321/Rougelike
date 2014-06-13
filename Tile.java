import java.awt.Graphics2D;

//A cell is a component of the map that holds whatever is on it and has a base type, it is mutable
public class Tile {
  public final TileData tileType;
  private Entity entity;
  private boolean discovered = false, visible = false;
  public Tile(int ID){
    tileType = TileData.getTile(ID);
  }
  public void draw(Graphics2D g2d,Position offset, int x, int y){
    if (visible){
      tileType.draw(g2d,offset, x, y, true);
      if(entity != null)
        TileData.getTile(2).draw(g2d,offset, x, y,true);
    }
    else if(discovered){
      tileType.draw(g2d,offset, x, y);
    }
    else
      TileData.getTile(0).draw(g2d,offset, x,y);
  }
  public boolean canWalk(){
    //add something if there is an entity here too
    return(!tileType.isCollideable() && (entity == null || entity.getComponent(CMoving.class)==null || !((CMoving)entity.getComponent(CMoving.class)).isCollideable()));
  }
  public boolean isOpaque(){
    return(tileType.isOpaque());
  }
  public void setEntity(Entity e){
    entity =e;
  }
  public Entity getEntity(){
    return entity;
  }
  public void discover(){
    discovered = true;
    visible = true;
  }
  public void resetVisible(){
    visible = false;
  }
}
