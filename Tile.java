import java.awt.Graphics2D;
import java.util.*;

//A cell is a component of the map that holds whatever is on it and has a base type, it is mutable
public class Tile {
  public final TileData tileType;
  private List<InventoryItem> items = new ArrayList<InventoryItem>();
  private Entity entity;
  private boolean discovered = false, visible = false;
  public Tile(int ID){
    tileType = TileData.getTile(ID);
  }
  public void draw(Graphics2D g2d,Position offset, int x, int y){
    if (visible){
      if(entity != null)
        TileData.getTile(((CMoving)entity.getComponent(CMoving.class)).getImg()).draw(g2d,offset, x, y,true);
      else if(items.size() !=0)
        TileData.getTile(42).draw(g2d,offset, x, y, true);
      else
        tileType.draw(g2d,offset, x, y, true);
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
  public int itemAmount(){return items.size();}
  public void addItem(String quality){
    if(quality.equals("Health")){
      items.add(InventoryItem.get("Health Potion"));
      return;
    }
    else if (quality.equals("Amulet")){
      items.add(InventoryItem.get("Amulet of Yendor"));
      return;
    }
    int rand = RandomNumber.getRand(0, InventoryItem.Weapons.vals.length+3);
    //weapons
    if(rand < InventoryItem.Weapons.vals.length){
      System.out.println("Dropped a " +quality + " " + InventoryItem.Weapons.vals[rand].name());
      items.add(InventoryItem.get(quality + " " + InventoryItem.Weapons.vals[rand].name()));
    }
    //chestplate
    else{
      items.add(InventoryItem.get(quality + " Chestplate"));
      System.out.println("Dropped a " +quality + " Chestplate");
    }
  }
  public void addItem(InventoryItem item){
    items.add(item);
  }
  public boolean pickup(int pos, CInventory c){
    if(c.addItem(items.get(pos))){
      items.remove(pos);
      return true;
    }
    return false;
  }
  public List<InventoryItem> getItems(){return items;}
  public void discover(){
    discovered = true;
    visible = true;
  }
  public void resetVisible(){
    visible = false;
  }
  public boolean isDiscovered(){return discovered;}
}
