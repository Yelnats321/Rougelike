import java.util.*;
import java.io.*;
public class DungeonGen{
  
  //size of the map
  private static int xsize = 0;
  private static int ysize = 0;

  
  //number of "objects" to generate on the map
  private static int objects = 0; 
  private static int chanceRoom = 75; 
  //our map
  private static int[] dungeon_map = {};
  private static boolean spawnedLich = false;
 
  
  //a list over tile types we're using
  final public static int tileUnused = 0;
  final public static int tileDirtWall = 1;
  final public static int tileDirtFloor = 2;
  final public static int tileStoneWall = 3; //not in use
  final public static int tileCorridor = 4;
  final public static int tileDoor = 5;
  final public static int tileUpStairs = 6;
  final public static int tileDownStairs = 7;
  
 
  
  static void createDungeon(int inx, int iny, int inobj, int level, boolean descended) {
    if(new File(level+".txt").exists()) return;
    if (inobj < 1) objects = 10;
    else objects = inobj;
    
    if (inx < 3) xsize = 3;
    else xsize = inx;
    
    if (iny < 3) ysize = 3;
    else ysize = iny;
    
    
    dungeon_map = new int[xsize * ysize];
    
    for (int y = 0; y < ysize; y++) {
      for (int x = 0; x < xsize; x++) {
        if (y == 0) setCell(x, y, tileStoneWall);
        else if (y == ysize-1) setCell(x, y, tileStoneWall);
        else if (x == 0) setCell(x, y, tileStoneWall);
        else if (x == xsize-1) setCell(x, y, tileStoneWall);
        else setCell(x, y, tileUnused);
      }
    }
    
    makeRoom(xsize/2, ysize/2, 8, 6, RandomNumber.getRand(0,3));
    
    int currentFeatures = 1; 
    for (int countingTries = 0; countingTries < 1000; countingTries++) {
      if (currentFeatures == objects) {
        break;
      }
      
      
      int newx = 0;
      int xmod = 0;
      int newy = 0;
      int ymod = 0;
      int validTile = -1;
      
      for (int testing = 0; testing < 1000; testing++) {
        newx = RandomNumber.getRand(1, xsize-1);
        newy = RandomNumber.getRand(1, ysize-1);
        validTile = -1;
        
        
        if (getCell(newx, newy) == tileDirtWall || getCell(newx, newy) == tileCorridor) {
          //check if we can reach the place
          if (getCell(newx, newy+1) == tileDirtFloor || getCell(newx, newy+1) == tileCorridor) {
            validTile = 0; //
            xmod = 0;
            ymod = -1;
          }
          else if (getCell(newx-1, newy) == tileDirtFloor || getCell(newx-1, newy) == tileCorridor) {
            validTile = 1; //
            xmod = +1;
            ymod = 0;
          }
          
          else if (getCell(newx, newy-1) == tileDirtFloor || getCell(newx, newy-1) == tileCorridor) {
            validTile = 2; //
            xmod = 0;
            ymod = +1;
          }
          
          else if (getCell(newx+1, newy) == tileDirtFloor || getCell(newx+1, newy) == tileCorridor) {
            validTile = 3; //
            xmod = -1;
            ymod = 0;
          }
          
          if (validTile > -1) {
            if (getCell(newx, newy+1) == tileDoor) //north
              validTile = -1;
            else if (getCell(newx-1, newy) == tileDoor)//east
              validTile = -1;
            else if (getCell(newx, newy-1) == tileDoor)//south
              validTile = -1;
            else if (getCell(newx+1, newy) == tileDoor)//west
              validTile = -1;
          }
          if (validTile > -1) break;
        }
      }
      
      if (validTile > -1) {
        int feature = RandomNumber.getRand(0, 100);
        if (feature <= chanceRoom) { //a new room
          if (makeRoom((newx+xmod), (newy+ymod), 8, 6, validTile)) {
            currentFeatures++; //add to our quota
            
            //then we mark the wall opening with a door
            setCell(newx, newy, tileDoor);
            
            //clean up infront of the door so we can reach it
            setCell((newx+xmod), (newy+ymod), tileDirtFloor);
          }
        }
        
        else if (feature >= chanceRoom) { //new corridor
          if (makeCorridor((newx+xmod), (newy+ymod), 6, validTile)) {
            //same thing here, add to the quota and a door
            currentFeatures++;
            setCell(newx, newy, tileDoor);
          }
        }
      }
    }
    
    int newx = 0;
    int newy = 0;
    int ways = 0; 
    int state = 0; 
    
    while (state != 10) {
      newx = RandomNumber.getRand(1, xsize-1);
      newy = RandomNumber.getRand(1, ysize-2); 
      ways = 4; //the lower the better
      
      //check if we can reach the spot
      if (getCell(newx, newy+1) == tileDirtFloor || getCell(newx, newy+1) == tileCorridor) {
        //north
        if (getCell(newx, newy+1) != tileDoor)
          ways--;
      }
      
      if (getCell(newx-1, newy) == tileDirtFloor || getCell(newx-1, newy) == tileCorridor) {
        //east
        if (getCell(newx-1, newy) != tileDoor)
          ways--;
      }
      
      if (getCell(newx, newy-1) == tileDirtFloor || getCell(newx, newy-1) == tileCorridor) {
        //south
        if (getCell(newx, newy-1) != tileDoor)
          ways--;
      }
      
      if (getCell(newx+1, newy) == tileDirtFloor || getCell(newx+1, newy) == tileCorridor) {
        //west
        if (getCell(newx+1, newy) != tileDoor)
          ways--;
      }
      
      if (state == 0) {
        if (ways == 0) {
          //we're in state 0, let's place a "upstairs" thing
          setCell(newx, newy, tileUpStairs);
          state = 1;
        }
      }
      
      else if (state == 1) {
        if (ways == 0 && getCell(newx, newy) != tileUpStairs) {
          //state 1, place a "downstairs"
          if(level != 30)
            setCell(newx, newy, tileDownStairs);
          state = 10;
        }
        
      }
    }
    
    writeDungeon(level, descended);
    
  }
  
  //setting a tile's type
  private static void setCell(int x, int y, int celltype) {
    dungeon_map[x + xsize * y] = celltype;
  }
  
  //returns the type of a tile
  private static int getCell(int x, int y) {
    return dungeon_map[x + xsize * y];
  }
  private static int getCell(Position p){
    return dungeon_map[p.x + xsize*p.y];
  }
  
  private static boolean makeCorridor(int x, int y, int lenght, int direction) {
    int len = RandomNumber.getRand(2, lenght);
    int floor = tileCorridor;
    int dir = 0;
    if (direction > 0 && direction < 4) dir = direction;
    
    int xtemp = 0;
    int ytemp = 0;
    
    // reject corridors that are out of bounds
    if (x < 0 || x > xsize) return false;
    if (y < 0 || y > ysize) return false;
    
    switch(dir) {
      
      case 0: //north
        xtemp = x;
        for (ytemp = y; ytemp > (y-len); ytemp--) {
          if (ytemp < 0 || ytemp > ysize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
        for (ytemp = y; ytemp > (y-len); ytemp--) {
          setCell(xtemp, ytemp, floor);
        }
        break;
        
      case 1: //east
        ytemp = y;
        
        for (xtemp = x; xtemp < (x+len); xtemp++) {
          if (xtemp < 0 || xtemp > xsize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
        
        for (xtemp = x; xtemp < (x+len); xtemp++) {
          setCell(xtemp, ytemp, floor);
        }
        break;
        
      case 2: // south
        xtemp = x;
        
        for (ytemp = y; ytemp < (y+len); ytemp++) {
          if (ytemp < 0 || ytemp > ysize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
        
        for (ytemp = y; ytemp < (y+len); ytemp++) {
          setCell(xtemp, ytemp, floor);
        }
        break;
        
      case 3: // west
        ytemp = y;
        
        for (xtemp = x; xtemp > (x-len); xtemp--) {
          if (xtemp < 0 || xtemp > xsize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
        
        for (xtemp = x; xtemp > (x-len); xtemp--) {
          setCell(xtemp, ytemp, floor);
        }
        break;
    }
    return true;
  }
  
  
  
  private static boolean makeRoom(int x, int y, int xlength, int ylength, int direction) {
    int xlen = RandomNumber.getRand(4, xlength);
    int ylen = RandomNumber.getRand(4, ylength);
    
    int floor = tileDirtFloor; 
    int wall = tileDirtWall; 
    
    int dir = 0;
    if (direction > 0 && direction < 4) dir = direction;
    
    switch(dir) {
      
      case 0: // north
        for (int ytemp = y; ytemp > (y-ylen); ytemp--) {
        if (ytemp < 0 || ytemp > ysize) return false;
        for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++) {
          if (xtemp < 0 || xtemp > xsize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false; //no space left...
        }
      }
        for (int ytemp = y; ytemp > (y-ylen); ytemp--) {
          for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++) {
            if (xtemp == (x-xlen/2)) setCell(xtemp, ytemp, wall);
            else if (xtemp == (x+(xlen-1)/2)) setCell(xtemp, ytemp, wall);
            else if (ytemp == y) setCell(xtemp, ytemp, wall);
            else if (ytemp == (y-ylen+1)) setCell(xtemp, ytemp, wall);
            else setCell(xtemp, ytemp, floor);
          }
        }
        
        break;
        
      case 1: // east
        
        for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++) {
        if (ytemp < 0 || ytemp > ysize) return false;
        for (int xtemp = x; xtemp < (x+xlen); xtemp++) {
          if (xtemp < 0 || xtemp > xsize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
      }
        
        for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++) {
          for (int xtemp = x; xtemp < (x+xlen); xtemp++) {
            if (xtemp == x) setCell(xtemp, ytemp, wall);
            else if (xtemp == (x+xlen-1)) setCell(xtemp, ytemp, wall);
            else if (ytemp == (y-ylen/2)) setCell(xtemp, ytemp, wall);
            else if (ytemp == (y+(ylen-1)/2)) setCell(xtemp, ytemp, wall);
            else setCell(xtemp, ytemp, floor);
          }
        }
        
        break;
        
      case 2: // south
        
        for (int ytemp = y; ytemp < (y+ylen); ytemp++) {
        if (ytemp < 0 || ytemp > ysize) return false;
        for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++) {
          if (xtemp < 0 || xtemp > xsize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
      }
        
        for (int ytemp = y; ytemp < (y+ylen); ytemp++) {
          for (int xtemp = (x-xlen/2); xtemp < (x+(xlen+1)/2); xtemp++) {
            if (xtemp == (x-xlen/2)) setCell(xtemp, ytemp, wall);
            else if (xtemp == (x+(xlen-1)/2)) setCell(xtemp, ytemp, wall);
            else if (ytemp == y) setCell(xtemp, ytemp, wall);
            else if (ytemp == (y+ylen-1)) setCell(xtemp, ytemp, wall);
            else setCell(xtemp, ytemp, floor);
          }
        }
        
        break;
        
      case 3: // west
        
        for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++) {
        if (ytemp < 0 || ytemp > ysize) return false;
        for (int xtemp = x; xtemp > (x-xlen); xtemp--) {
          if (xtemp < 0 || xtemp > xsize) return false;
          if (getCell(xtemp, ytemp) != tileUnused) return false;
        }
      }
        
        for (int ytemp = (y-ylen/2); ytemp < (y+(ylen+1)/2); ytemp++) {
          for (int xtemp = x; xtemp > (x-xlen); xtemp--) {
            if (xtemp == x) setCell(xtemp, ytemp, wall);
            else if (xtemp == (x-xlen+1)) setCell(xtemp, ytemp, wall);
            else if (ytemp == (y-ylen/2)) setCell(xtemp, ytemp, wall);
            else if (ytemp == (y+(ylen-1)/2)) setCell(xtemp, ytemp, wall);
            else setCell(xtemp, ytemp, floor);
          }
        }
        
        break;
    }
    return true;
  }
  private static void writeDungeon(int level, boolean descended){
    List<Position> posThing = new ArrayList<Position>();
    Position entrance = new Position(0,0);
    FileWriter fWriter = null;
    BufferedWriter writer = null;
    try {
      fWriter = new FileWriter(level+ ".txt");
      writer = new BufferedWriter(fWriter);
      writer.write((xsize-2)  + " " + (ysize-2));
      writer.newLine();
      for (int h = 0; h < ysize; h++){
        for (int l = 0; l < xsize; l++){
          if(h == 0 || h == ysize-1 || l == 0 || l == xsize-1){
          }
          else {
            writer.write(getCell(l, h) + " ");
            if(descended){
              if(getCell(l,h) == tileUpStairs){
                entrance = new Position(l,h);
              }
            }
            else if(getCell(l,h) == tileDownStairs)
              entrance = new Position(l,h);
            
            Position p = new Position(l,h);
            if(getCell(p) == tileDirtFloor ||
               getCell(p) == tileCorridor ||
               getCell(p) == tileDoor){
              if(RandomNumber.getRand(0, 100) <6+level/5){
                posThing.add(p);
              }
            }
          }
        }
        //  System.out.print(getCell(l,h) +" ");
        writer.newLine();
      }
      writer.write (posThing.size()+1+"");
      writer.newLine();
      writer.write(entrance.x-1 + " " +(entrance.y-1)+ " " + 0 +" " + 0);
      writer.newLine();
      System.out.println("started placing items");
      for(Position p : posThing){
        //enemy
        //subb one because we aren't writing the walls around
        if(RandomNumber.getRand(1, 5) > 1){
          int enemy = Entity.pickEnemy(level);
          if(level == 30 && !spawnedLich)
            enemy = Entity.enemyAmount()-1;
          if(enemy == Entity.enemyAmount()-1){
            if(!spawnedLich)
              spawnedLich = true;
            else enemy--;
          }
          writer.write(p.x-1 + " " + (p.y-1) +  " " +enemy + " " + 0);
          writer.newLine();
        }
        //item
        else{
          writer.write(p.x-1 + " " +( p.y-1) + " " +  InventoryItem.pickItem(level));
          writer.newLine();          
        }
      }
      writer.close();
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
}
