import java.util.*;
import java.io.*;
import java.awt.*;

public class GameMap {
  private final int width, height;
  private Tile tiles[][];
  public GameMap(String name, Entity player, EntityManager em){
    try{
      Scanner sc = new Scanner(new File(name));
      width = sc.nextInt();
      height = sc.nextInt();
      tiles = new Tile[width][height];
      for(int j =0; j< height; j++){
        for(int i = 0; i < width; i++){
          int data = sc.nextInt();
          if(data == 1) data = 219;
          else if(data == 2 || data==4) data = 46;
          else if(data == 5) data = 186;
          else if(data == 6) data = 60;
          else if(data == 7) data = 62;   
          tiles[i][j] = new Tile(data);
          CMoving.setMap(this);
          if(data == 60){
            player.addComponent(new CMoving(player, i, j));
            System.out.println("found it boss");
          }
        }
      }
      while(sc.hasNext()){
        int x = sc.nextInt();
        int y = sc.nextInt();
        int type = sc.nextInt();
        if(type <=10){
          Entity enemy = new Entity();
          enemy.addComponent(new CResources(enemy));
          enemy.addComponent(new CMoving(enemy,x,y));
          enemy.addComponent(new CAI(enemy, type*3));
          em.addEntity(enemy);
        }
      }
    }catch(IOException e){
      throw new RuntimeException(name+": map file missing");
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
  public Tile get(int x, int y){return get(new Position(x,y));}
  public Tile get(Position pos){return tiles[pos.x][pos.y];}
  public int getWidth(){return width;}
  public int getHeight(){return height;}
}
