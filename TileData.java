import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.awt.Graphics2D;

//Tiles are base types read from a file
public class TileData {
  private final int ID;
  private  boolean collideable = false, opaque = false;
  
  private static Map<Integer, TileData> tiles = new HashMap<Integer, TileData>();
  private static final String FILENAME = "tiles.txt", IMG_FILENAME = "curses_square_16x16.png", DARK_IMG_FILENAME = "darkcurse.png";
  private static final BufferedImage IMG_FILE, DARK_IMG_FILE;
  public static final int TILE_X = 16, TILE_Y = 16;
  private static final int TILES_PER_X = 16, TILES_PER_Y = 16;
  
  private TileData(int ID){
    this.ID = ID;
  }
  public void draw(Graphics2D g2d,Position offset, int x, int y){
    draw(g2d,offset,x,y,false);
  }
  public void draw(Graphics2D g2d, Position offset, int x, int y, boolean dark){
    int xPos = ID%TILES_PER_X;
    int yPos = ID/TILES_PER_Y;
    g2d.drawImage(dark?IMG_FILE:DARK_IMG_FILE,offset.x+ x*TILE_X,offset.y+ y*TILE_Y,offset.x+ (x+1)*TILE_X, offset.y+(y+1)*TILE_Y,
                  xPos*TILE_X, yPos*TILE_Y, (xPos+1)*TILE_X, (yPos+1)*TILE_Y,null);
  }
  

  
  static{
    //load tiles here
    try{
      IMG_FILE = ImageIO.read(new File(IMG_FILENAME));
      DARK_IMG_FILE = ImageIO.read(new File(DARK_IMG_FILENAME));
    }catch(IOException e){
      throw new RuntimeException("Tileset file missing");
    }
    tiles.put(0, new TileData(0));
    tiles.get(0).opaque = true;
    tiles.get(0).collideable = true;
    tiles.put(46, new TileData(46));
    tiles.put(60, new TileData(60));
    tiles.put(62, new TileData(62));
    tiles.put(186, new TileData(186));
    tiles.get(186).opaque = true;
    tiles.put(219, new TileData(219));
    tiles.get(219).opaque = true;
    tiles.get(219).collideable = true;
    tiles.put(2, new TileData(2));
  }
  public static TileData getTile(Integer ID){
    return tiles.get(ID);
  }
  public boolean isCollideable(){
    return collideable;
  }
  public boolean isOpaque(){
    return opaque;
  }
}
