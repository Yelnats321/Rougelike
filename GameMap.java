import java.util.*;
import java.io.*;
import java.awt.*;

public class GameMap {
  private final int width, height;
  public final static int STAIRSUP= 60, STAIRSDOWN=62, WALL = 219, FLOOR = 46, DOOR=186;
  private Tile tiles[][];
  public GameMap(String name, Entity player, EntityManager em){
    CMoving.setMap(this);
    try{
      Scanner sc = new Scanner(new File(name));
      width = sc.nextInt();
      height = sc.nextInt();
      tiles = new Tile[width][height];
      for(int j =0; j< height; j++){
        for(int i = 0; i < width; i++){
          int data = sc.nextInt();
          if(data == DungeonGen.tileDirtWall) data = WALL;
          else if(data == DungeonGen.tileDirtFloor || data==DungeonGen.tileCorridor) data = FLOOR;
          else if(data == DungeonGen.tileDoor) data = DOOR;
          else if(data == DungeonGen.tileUpStairs) data = STAIRSUP;
          else if(data == DungeonGen.tileDownStairs) data = STAIRSDOWN;   
          tiles[i][j] = new Tile(data);
        }
      }
      while(sc.hasNext()){
        int x = sc.nextInt();
        int y = sc.nextInt();
        int type = sc.nextInt();
        int currHp = sc.nextInt();
        if(type == 0){
          ((CMoving)player.getComponent(CMoving.class)).setPos(new Position(x,y));
        }
        else if(type <=Entity.enemyAmount()){
          em.addEntity(Entity.createEnemy(type, currHp,x,y));
        }
      }
      sc.close();
    }catch(IOException e){
      throw new RuntimeException(name+": map file missing");
    }
  }
  public void writeMap(String name){
    try {
      BufferedWriter writer = new BufferedWriter( new FileWriter(name));
      writer.write(width  + " " + height);
      writer.newLine();
      for (int h = 0; h < height; h++){
        for (int l = 0; l < width; l++){
          writer.write(get(l,h).tileType.ID + " ");
        }
        writer.newLine();
      }
      for(CActor actor : CActor.getActors()){
        CMoving mov = (CMoving) actor.owner.getComponent(CMoving.class);
        CResources heichp = (CResources) actor.owner.getComponent(CResources.class);
        if(mov == null) continue;
        writer.write(mov.getX() + " " +mov.getY() + " " + actor.owner.ID + " "+ heichp.getHP());
        writer.newLine();
      }
      //  System.out.print(getCell(l,h) +" ");
      writer.newLine();
      
      
      writer.close();
    }catch (Exception e) {
    }
  }
  public void setEntity(Position pos, Entity e){
    tiles[pos.x][pos.y].setEntity(e);
  }
  /*public void setEntity(int x, int y, Entity e){
   setEntity(new Position(x,y),e);
   }*/
  
  public void draw(Graphics2D g2d, Renderer renderer){
    Position off = renderer.getOffset();
    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        get(x, y).draw(g2d,off, x, y);
      }
    }
  }
  public boolean inBounds(Position pos){
    return (pos.x >= 0 && pos.y>=0 && pos.x< width && pos.y <height);
  }
  public boolean inBounds(int x, int y){
    return inBounds(new Position(x,y));
  }
  public void resetVisible(){
    for(int y = 0; y < height; y++){
      for(int x = 0; x < width; x++){
        get(x, y).resetVisible();
      }
    }
  }
  public static void clearMaps(){
    try{
      for(int i = 1; i <=30; i++){
        File f = new File(i+".txt");
        f.delete();
      }
    }catch(Exception ex){
    }
  }
  public Tile get(int x, int y){return tiles[x][y];}
  public Tile get(Position pos){return get(pos.x, pos.y);}
  public int getWidth(){return width;}
  public int getHeight(){return height;}
}
