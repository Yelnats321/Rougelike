import java.util.*;
import java.io.*;
public class DungeonGen{
  
  //size of the map
  private static int xsize = 0;
  private static int ysize = 0;
  public static int level = 0;
  static int dungeon_objects = 0;
  static int x = 0;
  static int y = 0;
  
  //number of "objects" to generate on the map
  private static int objects = 0; 
  private static int chanceRoom = 65; 
  //our map
  private static int[] dungeon_map = {};
  
  //the old seed from the RNG is saved in this one
  private static long oldseed = 0;
  
  //a list over tile types we're using
  final private static int tileUnused = 0;
  final private static int tileDirtWall = 1; //not in use
  final private static int tileDirtFloor = 2;
  final private static int tileStoneWall = 3;
  final private static int tileCorridor = 4;
  final private static int tileDoor = 5;
  final private static int tileUpStairs = 6;
  final private static int tileDownStairs = 7;

  
  //misc. messages to print
  private static String msgXSize = "X size of dungeon: \t";
  private static String msgYSize = "Y size of dungeon: \t";
  private static String msgMaxObjects = "max # of objects: \t";
  private static String msgNumObjects = "# of objects made: \t";
  private static String msgHelp = "";
  private static String msgDetailedHelp = "";
  
  static void createDungeon(int inx, int iny, int inobj, int level) {
    
    if (inobj < 1) objects = 10;
    else objects = inobj;
    
    if (inx < 3) xsize = 3;
    else xsize = inx;
    
    if (iny < 3) ysize = 3;
    else ysize = iny;
    
    System.out.println(msgXSize + xsize);
    System.out.println(msgYSize + ysize);
    System.out.println(msgMaxObjects + objects);
    
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
    
    makeRoom(xsize/2, ysize/2, 8, 6, getRand(0,3));
    
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
        newx = getRand(1, xsize-1);
        newy = getRand(1, ysize-1);
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
        int feature = getRand(0, 100);
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
      for (int testing = 0; testing < 1000; testing++) {
        newx = getRand(1, xsize-1);
        newy = getRand(1, ysize-2); 
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
            break;
          }
        }
        
        else if (state == 1) {
          if (ways == 0) {
            //state 1, place a "downstairs"
            setCell(newx, newy, tileDownStairs);
            state = 10;
            break;
          }
        }
      }
    }
    
    System.out.println(msgNumObjects + currentFeatures);
    writeDungeon(level);
    
  }
  
  //setting a tile's type
  private static void setCell(int x, int y, int celltype) {
    dungeon_map[x + xsize * y] = celltype;
  }
  
  //returns the type of a tile
  private static int getCell(int x, int y) {
    return dungeon_map[x + xsize * y];
  }

  private static int getRand(int min, int max) {
// i readded the oldseed thing just so we dont get repeating seeds if we generate 2 levels simultanously(we shouldnt but its a just in case ting)
    Date now = new Date();
    long seed = now.getTime() + oldseed;
    oldseed = seed; 
    Random randomizer = new Random(seed);
    int n = max - min + 1;
    int i = randomizer.nextInt(n);
    if (i < 0) i = -i;
    
    //System.out.println("seed: " + seed + "\tnum:  " + (min + i));
    return min + i;
  }
  
  
  private static boolean makeCorridor(int x, int y, int lenght, int direction) {
    int len = getRand(2, lenght);
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
    int xlen = getRand(4, xlength);
    int ylen = getRand(4, ylength);
    
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
  public static void writeDungeon(int level)
  {
    FileWriter fWriter = null;
    BufferedWriter writer = null;
    try {
      fWriter = new FileWriter(level+ ".txt");
      writer = new BufferedWriter(fWriter);
      writer.write((ysize-2)  + " " + (xsize-2));
      writer.newLine();
      for (int h = 0; h < ysize; h++){
        for (int l = 0; l < xsize; l++){
          if(h == 0 || h == ysize-1 || l == 0 || l == xsize-1){
          }
          else writer.write(getCell(l, h) + " ");
          }
        //  System.out.print(getCell(l,h) +" ");
        writer.newLine();
      }
      writer.close();
    }catch (Exception e) {
    }
  }
  //
  public static void showDungeon(){
    for (int y = 0; y < ysize; y++){
      for (int x = 0; x < xsize; x++){
        switch(getCell(x, y)){
          case tileUnused:
            System.out.print(" ");
            break;
          case tileDirtWall:
            System.out.print("+");
            break;
          case tileDirtFloor:
            System.out.print(".");
            break;
          /*case tileStoneWall:
            System.out.print("O");
            break;*/
          case tileCorridor:
            System.out.print("#");
            break;
          case tileDoor:
            System.out.print("D");
            break;
          case tileUpStairs:
            System.out.print("<");
            break;
          case tileDownStairs:
            System.out.print(">");
            break;
          /*case tileChest:
            System.out.print("*");
            break;*/
        };
      }
      if (xsize <= 80) System.out.println();
    }
  }
  public static void main(String[] args){
    //initial stuff used in making the map
    x = 102; y = 102; dungeon_objects = 150;
    //convert a string to a int, if there's more then one arg
    if (args.length >= 1)
      dungeon_objects = Integer.parseInt(args[0]);
    if (args.length >= 2)
      x = Integer.parseInt(args[1]);
    
    if (args.length >= 3)
      y = Integer.parseInt(args[2]);
    DungeonGen generator = new DungeonGen();
    for(level = 0; level <= 5; level++)
    {
    generator.createDungeon(x, y, dungeon_objects, level);
    }
    //generator.showDungeon();
    // generator.writeDungeon(level);
  }
}
