public class Position{
  public int x, y;
  public Position(){
    this.x = 0;
    this.y = 0;
  }
  public Position(int x, int y){
    this.x = x;
    this.y = y;
  }
  public Position add(Position o){
    return new Position(x+o.x, y+o.y);
  }
  @Override
  protected Object clone(){
    return new Position(x,y);
  }
  @Override
  public boolean equals(Object o){
    if(o==null) return false;
    if(o.getClass() != Position.class) return false;
    Position p = (Position) o;
    return(p.x==x && p.y==y);
  }
}