import java.util.*;
class CLOS extends CBase{
  private List<Tile> visibleTiles;
  private int radius;
  private static GameMap map;
  public CLOS(Entity o, int r){
    super(o);
    radius = r;
  }
  public static void setMap(GameMap m){
    map = m;
  }
  public void update(){
    visibleTiles = new ArrayList<Tile>();
    CMoving mov = (CMoving)owner.getComponent(CMoving.class);
    List<Position> tilePos = makeOffsetCircle(mov.getPos());
    for(Position i:tilePos){
      if(map.inBounds(i.x,i.y) && checkLine(mov.getPos(), i))
        visibleTiles.add(map.get(i));
    }
  }
  //only works for straight lines or diagonal, not anything else like (3,12)
  private void makeLine(int x0, int y0, int x1, int y1, List<Position> points){
    for(int x = x0; x <= x1; x++){
      for(int y = y0; y<=y1;y++){
        points.add(new Position(x,y));
      }
    }
  }
  private List<Position> makeOffsetCircle(Position pos){
    int x = radius, y = 0;
    int radiusError = 1-x;
    List<Position> points = new LinkedList<Position>();
    int x0 = pos.x, y0 = pos.y;
    while(x >= y){
      makeLine(-x + x0, y + y0, x + x0, y + y0, points);
      //      makeLine(-x + x0, y + y0, points);
      
      makeLine(-y + x0, x + y0, y + x0, x + y0, points);
      //      makeLine(-y + x0, x + y0, points);
      
      makeLine(-x + x0, -y + y0, x + x0, -y + y0, points);
      //      makeLine(x + x0, -y + y0, points);
      
      makeLine(-y + x0, -x + y0, y + x0, -x + y0, points);
      //      makeLine(y + x0, -x + y0, points);
      y++;
      if (radiusError<0){
        radiusError += 2 * y + 1;
      }
      else{
        x--;
        radiusError+= 2 * (y - x + 1);
      }
    }
    return points;
  }
  
  private boolean checkLine(Position start, Position end){
    int x = start.x;
    int y = start.y;
    int x2 = end.x;
    int y2 = end.y;
    
    int w = x2 - x ;
    int h = y2 - y ;
    int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
    if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
    if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
    if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
    int longest = Math.abs(w) ;
    int shortest = Math.abs(h) ;
    if (!(longest>shortest)) {
      longest = Math.abs(h) ;
      shortest = Math.abs(w) ;
      if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
      dx2 = 0 ;            
    }
    int numerator = longest >> 1 ;
    for (int i=0;i<=longest;i++) {
      if(map.get(x, y).isOpaque()){
        if(x == x2 && y == y2)
          return true;
        if(!(x==start.x && y==start.y))
          return false;
      }
      numerator += shortest ;
      if (!(numerator<longest)) {
        numerator -= longest ;
        x += dx1 ;
        y += dy1 ;
      } else {
        x += dx2 ;
        y += dy2 ;
      }
    }
    return true;
  }
  
  public List<Tile> getVisible(){return visibleTiles;}
}