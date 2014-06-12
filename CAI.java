//the AI class, mostly just a placeholder right now so that the AI at least do dsomething
class CAI extends CBase implements Action{
  public CAI(Entity o, int s){
    super(o);
    o.addComponent(new CActor(o, s, this));
  }
  private static EntityManager entityManager;
  public static void setManager(EntityManager e){
    entityManager = e;
  }
  public void act(){
    if(entityManager == null) throw new RuntimeException("Entity manager not set for AI");
    if(owner.getComponent(CMoving.class) == null) return;
    final CMoving moving = (CMoving)owner.getComponent(CMoving.class);
    final CMoving playerMoving = (CMoving)entityManager.getPlayer().getComponent(CMoving.class);
    //very basic, will be replaced
    final int diffX = moving.getX() - playerMoving.getX();
    final int diffY = moving.getY() - playerMoving.getY();
    if(Math.abs(diffX) > Math.abs(diffY)){
      if(diffX > 0)
        moving.move(Direction.LEFT);
      else
        moving.move(Direction.RIGHT);
    }
    else{
      if(diffY >0)
        moving.move(Direction.UP);
      else
        moving.move(Direction.DOWN);
    }
  }
}