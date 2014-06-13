import java.util.*;

public class EntityManager{
  //should the player have his own class that extends or has an entity inside of it?
  private Entity player;
  private GameMap map;
  private List<Entity> entities;
  private GUI gui;
  public EntityManager(GUI gui){
    this.gui = gui;
    entities = new LinkedList<Entity>();
    Entity.setManager(this);
    CAI.setManager(this);
    player = new Entity();
    DungeonGen.createDungeon(30,30,15,1);
    DungeonGen.writeDungeon(1);
    changeMap("1.txt");
    player.addComponent(new CResources(player));
    player.addComponent(new CInventory(player));
    player.addComponent(new CActor(player, 10, gui));
    player.addComponent(new CLOS(player, 6));
    Renderer.setPlayer(player);
  }

  public void update(Renderer renderer){
    //this should do the player's action, then whatever actions any other entity has to do based on speed and such
    LinkedList<CActor> moveList = generateList();
    for(CActor actor: moveList){
      if(actor.owner.getComponent(CLOS.class) != null){
        ((CLOS)actor.owner.getComponent(CLOS.class)).update();
      }
      if(actor.owner == player){
        map.resetVisible();
        for(Tile i : ((CLOS)player.getComponent(CLOS.class)).getVisible()){
          i .discover();
        }
        renderer.updateOffset();
        renderer.repaint();
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
  public LinkedList<CActor> generateList(){
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
  public void changeMap(String name){
    map = new GameMap(name, player, this);
    CLOS.setMap(map);
    Renderer.setMap(map);
  }
  
  public List<Entity> getEntities(){return entities;}
  public GameMap getMap(){ return map;}
  public Entity getPlayer(){return player;}
}