import java.util.*;

public class EntityManager{
  //should the player have his own class that extends or has an entity inside of it?
  private Entity player;
  private static Renderer renderer;
  private GameMap map;
  private List<Entity> entities;
  private GUI gui;
  private int level = 1;
  public EntityManager(GUI gui){
    this.gui = gui;
    entities = new LinkedList<Entity>();
    Entity.setManager(this);
    CAI.setManager(this);
    CMoving.setManager(this);
    player = new Entity();
    player.addComponent(new CResources(player));
    player.addComponent(new CInventory(player));
    player.addComponent(new CActor(player, 10, gui));
    player.addComponent(new CLOS(player, 6));
    player.addComponent(new CMoving(player,0,0));
    DungeonGen.createDungeon(30,30,15,level);
    Renderer.setPlayer(player);
    changeMap(level+".txt");
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
      if(actor.owner.getComponent(CLOS.class) != null){
        ((CLOS)actor.owner.getComponent(CLOS.class)).update();
      }
      if(actor.owner == player){
        repaint();
      }
      actor.act();
      if(!player.live){
        System.out.println("died");
        gui.died();
        return;
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
  private void changeMap(String name){
    map = new GameMap(name, player, this);
    CMoving.setMap(map);
    CLOS.setMap(map);
    Renderer.setMap(map);
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
    DungeonGen.createDungeon(30,30,15,level);
    changeMap(level+".txt");
  }
  public void goUp(){
    map.writeMap(level+".txt");
    Iterator i = entities.iterator();
    while(i.hasNext()){
      i.next();
      i.remove();
    } 
    level--;
    DungeonGen.createDungeon(30,30,15,level);
    changeMap(level+".txt");
  }
  public List<Entity> getEntities(){return entities;}
  public GameMap getMap(){ return map;}
  public Entity getPlayer(){return player;}
}