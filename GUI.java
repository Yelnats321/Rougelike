import java.awt.event.*;
import java.util.concurrent.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.awt.Graphics2D;

public class GUI implements KeyListener, Action{
  public enum State{
    GAME, INVENTORY, MAIN_MENU;
  }
  private final int WIDTH = 6, HEIGHT = 3;
  private final String INVENTORY_FILENAME = "inventory.png", INVENTORY_SELECTOR_FILENAME = "inventoryselector.png", INVENTORY_SELECTED_FILENAME = "selected.png", MAIN_MENU_FILENAME = "mainmenu.png";
  private final BufferedImage INVENTORY_FILE, INVENTORY_SELECTOR_FILE, INVENTORY_SELECTED_FILE, MAIN_MENU_FILE;
  private CountDownLatch waitLatch;
  private Entity player;
  private EntityManager entityManager;
  private CInventory inv;
  private CResources res;
  private State state = State.MAIN_MENU;
  private Renderer renderer;
  private Position selectedPos = new Position(0,0), equipedPos1, equipedPos2;
  private boolean enterPressed  = false;
  
  public GUI(){
    try{
      INVENTORY_FILE = ImageIO.read(new File(INVENTORY_FILENAME));
      INVENTORY_SELECTOR_FILE = ImageIO.read(new File(INVENTORY_SELECTOR_FILENAME));
      INVENTORY_SELECTED_FILE = ImageIO.read(new File(INVENTORY_SELECTED_FILENAME));
      MAIN_MENU_FILE = ImageIO.read(new File(MAIN_MENU_FILENAME));
    }catch(IOException e){
      throw new RuntimeException("GUI Files missing");
    }
    renderer = new Renderer(this);
  }
  public void update(){
    if(state == State.MAIN_MENU){
      act();
    }
    else{
      entityManager.update(renderer);
    }
  }
  
  //some sort of blocking function that waits until the player makes an input
  public void act(){
    waitLatch = new CountDownLatch(1);
    try{
      waitLatch.await();
    }catch(InterruptedException e){}
  }
  
  public State getState(){return state;}
  
  private boolean inBounds(Position d){
    if(state == State.INVENTORY)
      return(d.x >= 0 && d.x < WIDTH && d.y>=0 && d.y< HEIGHT);
    if(state == State.MAIN_MENU)
      return (d.y >= 0 && d.y <1 && d.x == 0);
    return false;
  }
  public void move(Direction d){
    if(inBounds(d.offset.add(selectedPos))){
      selectedPos = d.offset.add(selectedPos);
      renderer.repaint();
    }
  }
  public void newGame(){
    entityManager = new EntityManager(this);
    state = State.GAME;
      
    player = entityManager.getPlayer();
    inv = (CInventory)player.getComponent(CInventory.class);
    res = (CResources)player.getComponent(CResources.class);
  }
  public void select(){
    System.out.println("Selected");
    if(state == State.INVENTORY){
      InventoryItem item = inv.getItem(selectedPos.x+ selectedPos.y*WIDTH);
      if (item == null) return;
      if(item.type != InventoryItem.Type.MISC){
        if(selectedPos.equals(equipedPos1) || selectedPos.equals(equipedPos2)){
          inv.unequip(item);
          if(selectedPos.equals(equipedPos1)){
            equipedPos1 = null;
          }
          else{
            equipedPos2 = null;
          }
        }
        else{
          inv.equip(item);
          if(item.type == InventoryItem.Type.WEAPON){
            equipedPos1 = (Position)selectedPos.clone();
          }
          else 
            equipedPos2 = (Position)selectedPos.clone();
        }
        renderer.repaint();
        waitLatch.countDown();
      }
    }
    else if(state == State.MAIN_MENU){
      if(selectedPos.y ==0){
        newGame();
        waitLatch.countDown();
      }
    }
  }
  public void openInventory(){
    state = State.INVENTORY;
    selectedPos = new Position(0,0);
    renderer.repaint();
  }
  public void closeInventory(){
    state = State.GAME;
    renderer.repaint();
  }
  public void draw(Graphics2D g2d){
    if(state == State.INVENTORY){
      g2d.drawImage(INVENTORY_FILE, 20,40, null);
      g2d.drawImage(INVENTORY_SELECTOR_FILE, selectedPos.x*43 +60+20, selectedPos.y*41 + 200+40,null);
      for(int y = 0; y < HEIGHT; y++){
        for(int x = 0; x< WIDTH; x++){
          if(inv.getItem(x+y*WIDTH)!=null)
            inv.getItem(x+y*WIDTH).draw(g2d,x*43+66+20,y*41+205+40);
        }
      }
      if(equipedPos1 != null)
        g2d.drawImage(INVENTORY_SELECTED_FILE, equipedPos1.x*43 +66+20, equipedPos1.y*41 + 205+40,null);
      if(equipedPos2 != null)
        g2d.drawImage(INVENTORY_SELECTED_FILE, equipedPos2.x*43 +66+20, equipedPos2.y*41 + 205+40,null);
      
      g2d.setFont(new Font("TimesRoman", Font.PLAIN, 16));
      g2d.setColor(Color.WHITE);
      g2d.drawString("1", 109+20, 109+20+4);
      g2d.drawString(res.getHP() + "/" + res.getMaxHP(), 109+20, 109+20+4+16);
      g2d.drawString(res.getDefense()+"", 109+20, 109+20+4+16*2);
      g2d.drawString(res.getAttack()+"", 109+20, 109+20+4+16*3);
    }
    else if(state == State.MAIN_MENU){
      g2d.drawImage(MAIN_MENU_FILE, 0, 0, null);
    }
  }
  
  public void keyTyped(KeyEvent e){}
  
  public void keyReleased(KeyEvent e){
    if(e.getKeyCode() == KeyEvent.VK_ENTER){
      enterPressed=false;
      System.out.println("reeased");
    }
  }
  public void died(){
    state = State.MAIN_MENU;
    renderer.repaint();
  }
  public void keyPressed(KeyEvent e){
    
    if(waitLatch.getCount() == 0) return;
    switch(state){
      case GAME:
        CMoving mov = (CMoving)player.getComponent(CMoving.class);
        switch(e.getKeyCode()){
          case KeyEvent.VK_W:
            if(mov.move(Direction.UP) || mov.attack(Direction.UP))
            waitLatch.countDown();
            break;
          case KeyEvent.VK_A:
            if(mov.move(Direction.LEFT) || mov.attack(Direction.LEFT))
            waitLatch.countDown();
            break;
          case KeyEvent.VK_S:
            if(mov.move(Direction.DOWN) || mov.attack(Direction.DOWN))
            waitLatch.countDown();
            break;
          case KeyEvent.VK_D:
            if(mov.move(Direction.RIGHT) || mov.attack(Direction.RIGHT))
            waitLatch.countDown();
            break;
          case KeyEvent.VK_I:
            openInventory();
            break;
          default:
            System.out.println("Not sure about this input: "+e.getKeyChar());       
        }
        break;
      case INVENTORY:
        switch(Character.toUpperCase(e.getKeyChar())){
        case KeyEvent.VK_W:
          move(Direction.UP);
          break;
        case KeyEvent.VK_A:
          move(Direction.LEFT);
          break;
        case KeyEvent.VK_S:
          move(Direction.DOWN);
          break;
        case KeyEvent.VK_D:
          move(Direction.RIGHT);
          break;
        case KeyEvent.VK_ENTER:{
          if(!enterPressed){
            System.out.println("pressed");
            enterPressed = true;
            select();
          }

          break;
        }
        case KeyEvent.VK_I:
        case KeyEvent.VK_ESCAPE:
          closeInventory();
          break;
      }
        break;
      case MAIN_MENU:
        switch(Character.toUpperCase(e.getKeyChar())){
        case KeyEvent.VK_W:
          move(Direction.UP);
          break;
        case KeyEvent.VK_S:
          move(Direction.DOWN);
          break;
        case KeyEvent.VK_ENTER:
          select();
          break;
          
      }
    }
    //we should send down proper inputs to the entity manager, such as moving or using a spell or item
    //entityManager.setPlayerAction(whatever);
    //when a proper input is sent we count down
    
  }
}