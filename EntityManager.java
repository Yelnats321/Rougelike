import java.util.*;

public class EntityManager{
  //should the player have his own class that extends or has an entity inside of it?
  private Entity player;
  private static Renderer renderer;
  private GameMap map;
  private List<Entity> entities;
  private GUI gui;
  private int level = 1;
  private boolean changedMap = false;
  public EntityManager(GUI gui){
    this.gui = gui;
    entities = new LinkedList<Entity>();
    Entity.setManager(this);
    CAI.setManager(this);
    CMoving.setManager(this);
    player = new Entity("Player",0);
    player.addComponent(new CResources(player));
    player.addComponent(new CInventory(player));
    player.addComponent(new CActor(player, 250, gui));
    player.addComponent(new CLOS(player, 6));
    CMoving mov = new CMoving(player,0,0,64);
    player.addComponent(mov);
    Renderer.setPlayer(player);
    changeMap(level+".txt", true);
  }
  public static void setRenderer(Renderer r){
    renderer = r;
  }
  private void repaint(){
    map.resetVisible();
    for(Tile i : ((CLOS)player.getComponent(CLOS.class)).getVisible()){
      i .discover();
    }
    renderer.updateOffset();
    renderer.repaint();
  }
  public void update(){
    //this should do the player's action, then whatever actions any other entity has to do based on speed and such
    LinkedList<CActor> moveList = generateList();
    for(CActor actor: moveList){
      if(!actor.owner.live)continue;
      if(actor.owner.getComponent(CLOS.class) != null){
        ((CLOS)actor.owner.getComponent(CLOS.class)).update();
      }
      if(actor.owner == player){
        repaint();
      }
      actor.act();
      if(!player.live){
        System.out.println("You died");
        gui.died();
        return;
      }
      if(changedMap){
        changedMap = false;
        break;
      }
    }
  }
  public void addEntity(Entity e){
    entities.add(e);
  }
  public void removeEntity(Entity e){
    entities.remove(e);
  }
  private LinkedList<CActor> generateList(){
    LinkedList<CActor> list = new LinkedList<CActor>();
    //finds lowest value in array and subtracts that from each integer in array
    while(true){
      Iterator<CActor> actors = CActor.getActors().listIterator(0);
      CActor actor = actors.next();
      int subtract = actor.currSpeed;
      while(actors.hasNext()){
        actor = actors.next();
        if(actor.currSpeed < subtract){
          subtract = actor.currSpeed;
        }
      }
      actors = CActor.getActors().listIterator(0);
      while(actors.hasNext()){
        actor= actors.next();
        actor.currSpeed -= subtract;
        
        //if a value in array hits 0, that item would move and this is added to the list of moves
        if(actor.currSpeed == 0){
          actor.currSpeed = actor.getSpeed();
          list.add(actor);
        }
      }
      if(((CActor)player.getComponent(CActor.class)).currSpeed == ((CActor)player.getComponent(CActor.class)).getSpeed()){
        return list;
      }
    }
  }
  private void changeMap(String name, boolean desc){
    changedMap =  true;
    CActor.getActors().clear();
    CActor.getActors().add((CActor)player.getComponent(CActor.class));
    entities.clear();
    int dx = RandomNumber.getRand(30+level,30+level*2), dy= RandomNumber.getRand(30+level,30+level*2);
    DungeonGen.createDungeon(dx,dy,dx*dy/90,level, desc);
    map = new GameMap(name, player, this);
    CLOS.setMap(map);
    Renderer.setMap(map);
    CResources.setMap(map);
    ((CMoving)player.getComponent(CMoving.class)).move(Direction.NONE);
    ((CLOS)player.getComponent(CLOS.class)).update();
    repaint();
  }
  public void goDown(){
    map.writeMap(level+".txt");
    Iterator i = entities.iterator();
    while(i.hasNext()){
      i.next();
      i.remove();
    } 
    level++;
    changeMap(level+".txt",true);
    System.out.println("Descended the stairs to level "+level);
  }
  public boolean goUp(){
    if(level == 1)
      return false;
    map.writeMap(level+".txt");
    Iterator i = entities.iterator();
    while(i.hasNext()){
      i.next();
      i.remove();
    } 
    level--;
    changeMap(level+".txt",false);
    System.out.println("Ascended the stairs to level "+level);
    return true;
  }
  public List<Entity> getEntities(){return entities;}
  public GameMap getMap(){ return map;}
  public Entity getPlayer(){return player;}
}