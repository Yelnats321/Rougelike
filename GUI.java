import java.awt.event.*;
import java.util.concurrent.*;
import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.awt.Graphics2D;

public class GUI implements KeyListener, Action{
  private enum State{
    GAME{
      @Override
      public void draw(Graphics2D g2d){
        gui.entityManager.getMap().draw(g2d);
      }
      @Override
      public void keyPressed(KeyEvent e){
        if(gui.waitLatch.getCount() == 0) return;
        switch(e.getKeyCode()){
          case KeyEvent.VK_W:
            if(gui.mov.move(Direction.UP) || gui.mov.attack(Direction.UP))
            gui.waitLatch.countDown();
            break;
          case KeyEvent.VK_A:
            if(gui.mov.move(Direction.LEFT) || gui.mov.attack(Direction.LEFT))
            gui.waitLatch.countDown();
            break;
          case KeyEvent.VK_S:
            if(gui.mov.move(Direction.DOWN) || gui.mov.attack(Direction.DOWN))
            gui.waitLatch.countDown();
            break;
          case KeyEvent.VK_D:
            if(gui.mov.move(Direction.RIGHT) || gui.mov.attack(Direction.RIGHT))
            gui.waitLatch.countDown();
            break;
          case KeyEvent.VK_PERIOD:
            if(e.isShiftDown()){
            if(gui.mov.move(Direction.BELOW))
              gui.waitLatch.countDown();
          }
            break;
          case KeyEvent.VK_COMMA:
            if(e.isShiftDown()){
            if(gui.mov.move(Direction.ABOVE))
              gui.waitLatch.countDown();
          }
            break;
          case KeyEvent.VK_I:
            INVENTORY.change();
            break;
          case KeyEvent.VK_SPACE:
            gui.waitLatch.countDown();
            break;
          case KeyEvent.VK_G:
            if(gui.entityManager.getMap().get(gui.mov.getPos()).itemAmount() >=1)
            PICKUP_MENU.change();
            break;     
        }
      }
      @Override
      protected void change(){
        gui.renderer.repaint();
        gui.state= this;
      }
    }, INVENTORY(){
      private final int WIDTH = 6, HEIGHT = 3;
      private Position selectedPos = new Position(0,0), equipedPos1, equipedPos2;
      private final String INVENTORY_FILENAME = "inventory.png", INVENTORY_SELECTOR_FILENAME = "inventoryselector.png",
        INVENTORY_SELECTED_FILENAME = "selected.png";
      private final  BufferedImage INVENTORY_FILE, INVENTORY_SELECTOR_FILE, INVENTORY_SELECTED_FILE;
      
      {
        try{
          INVENTORY_FILE = ImageIO.read(new File(INVENTORY_FILENAME));
          INVENTORY_SELECTOR_FILE = ImageIO.read(new File(INVENTORY_SELECTOR_FILENAME));
          INVENTORY_SELECTED_FILE = ImageIO.read(new File(INVENTORY_SELECTED_FILENAME));
        }catch(IOException e){
          throw new RuntimeException("GUI Files missing");
        }
      }
      
      private void select(){
        InventoryItem item = gui.inv.getItem(selectedPos.x+ selectedPos.y*WIDTH);
        if (item == null) return;
        if(item.type != InventoryItem.Type.MISC){
          if(selectedPos.equals(equipedPos1) || selectedPos.equals(equipedPos2)){
            gui.inv.unequip(item);
            if(selectedPos.equals(equipedPos1)){
              equipedPos1 = null;
            }
            else{
              equipedPos2 = null;
            }
          }
          else{
            gui.inv.equip(item);
            if(item.type == InventoryItem.Type.WEAPON){
              equipedPos1 = (Position)selectedPos.clone();
            }
            else 
              equipedPos2 = (Position)selectedPos.clone();
          }
          gui.renderer.repaint();
          gui.waitLatch.countDown();
        }
        else{
          if(gui.res.quaff(item)){
            gui.inv.removeItem(selectedPos.x+ selectedPos.y*WIDTH);
            gui.renderer.repaint();
            gui.waitLatch.countDown();
          }
        }
      }
      private void move(Direction d){
        if(inBounds(d.offset.add(selectedPos))){
          selectedPos = d.offset.add(selectedPos);
          gui.renderer.repaint();
        }
      }
      private boolean inBounds(Position d){
        return(d.x >= 0 && d.x < WIDTH && d.y>=0 && d.y< HEIGHT);
      }
      @Override
      public void draw(Graphics2D g2d){
        gui.entityManager.getMap().draw(g2d);
        g2d.drawImage(INVENTORY_FILE, 20,40, null);
        g2d.drawImage(INVENTORY_SELECTOR_FILE, selectedPos.x*43 +60+20, selectedPos.y*41 + 200+40,null);
        for(int y = 0; y < HEIGHT; y++){
          for(int x = 0; x< WIDTH; x++){
            if(gui.inv.getItem(x+y*WIDTH)!=null)
              gui.inv.getItem(x+y*WIDTH).draw(g2d,x*43+66+20,y*41+205+40);
          }
        }
        if(equipedPos1 != null)
          g2d.drawImage(INVENTORY_SELECTED_FILE, equipedPos1.x*43 +66+20, equipedPos1.y*41 + 205+40,null);
        if(equipedPos2 != null)
          g2d.drawImage(INVENTORY_SELECTED_FILE, equipedPos2.x*43 +66+20, equipedPos2.y*41 + 205+40,null);
        
        g2d.setFont(new Font("TimesRoman", Font.PLAIN, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString(gui.res.getLevel()+"", 109+20, 109+20+4);
        g2d.drawString(gui.res.getXP() + "/" + gui.res.getNeededXP(), 109+20+134, 109+20+4);
        g2d.drawString(gui.res.getHP() + "/" + gui.res.getMaxHP(), 109+20, 109+20+4+16);
        g2d.drawString(gui.res.getDefense()+"", 109+20, 109+20+4+16*2);
        g2d.drawString(gui.res.getAttack()+"", 109+20, 109+20+4+16*3);
      }
      @Override
      public void keyPressed(KeyEvent e){
        if(gui.waitLatch.getCount() == 0) return;
        switch(e.getKeyCode()){
          case KeyEvent.VK_A:
            move(Direction.LEFT);
            break;        
          case KeyEvent.VK_D:
            move(Direction.RIGHT);
            break;
          case KeyEvent.VK_W:
            move(Direction.UP);
            break;
          case KeyEvent.VK_S:
            move(Direction.DOWN);
            break;
          case KeyEvent.VK_I:
          case KeyEvent.VK_ESCAPE:
            GAME.change();
            break;
          case KeyEvent.VK_G:
            drop();
            break;
          case KeyEvent.VK_ENTER:
            select();
            break;
        }
      }
      @Override
      protected void change(){
        selectedPos = new Position();
        gui.renderer.repaint();
        gui.state= this;
      }
      
      
      private void drop(){
        InventoryItem item = gui.inv.getItem(selectedPos.x+ selectedPos.y*WIDTH);
        if (item == null) return;
        gui.entityManager.getMap().get(gui.mov.getPos()).addItem(item);
        if(selectedPos.equals(equipedPos1) || selectedPos.equals(equipedPos2)){
          gui.inv.unequip(item);
          if(selectedPos.equals(equipedPos1)){
            equipedPos1 = null;
          }
          else{
            equipedPos2 = null;
          }
        }
        gui.inv.removeItem(selectedPos.x+selectedPos.y*WIDTH);
        gui.waitLatch.countDown();
        gui.renderer.repaint();
      }
      
      public void clear(){
        System.out.println("cleared");
      }
    }, MAIN_MENU{
      private int selected = 0;
      private final String MAIN_MENU_FILENAME = "mainmenu.png";
      private final BufferedImage MAIN_MENU_FILE;
      {
        try{
          MAIN_MENU_FILE = ImageIO.read(new File(MAIN_MENU_FILENAME));
        }catch(IOException e){
          throw new RuntimeException("GUI Files missing");
        }
      }
      
      private boolean inBounds(int d){
        return (d >= 0 && d <1);
      }
      
      private void select(){
        if(selected ==0){
          newGame();
          gui.waitLatch.countDown();
        }
      }
      
      private void newGame(){
        GAME.change();
        GameMap.clearMaps();
        gui.entityManager = new EntityManager(gui);
        INVENTORY.clear();
        
        gui.player = gui.entityManager.getPlayer();
        gui.inv = (CInventory)gui.player.getComponent(CInventory.class);
        gui.res = (CResources)gui.player.getComponent(CResources.class);
        gui.mov = (CMoving)gui.player.getComponent(CMoving.class);
        gui.renderer.repaint();
      }
      
      @Override
      public void draw(Graphics2D g2d){
        g2d.drawImage(MAIN_MENU_FILE, 0, 0, null);
      }
      
      @Override
      public void keyPressed(KeyEvent e){
        switch(e.getKeyCode()){
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
      @Override
      protected void change(){
        selected = 0;
        gui.renderer.repaint();
        gui.state= this;
      }
      
      private void move(Direction d){
        if(inBounds(d.offset.y +selected)){
          selected += d.offset.y;
          gui.renderer.repaint();
        }
      }
      
      @Override
      public void update(){
        gui.act();
      }
    }, PICKUP_MENU{
      private int selected;
      private final String PICKUP_FILENAME="pickup.png";
      private final BufferedImage PICKUP_FILE;
      {
        try{
          PICKUP_FILE = ImageIO.read(new File(PICKUP_FILENAME));
        }catch(IOException e){
          throw new RuntimeException("GUI Files missing");
        }
      }
      private boolean inBounds(int s){
        return(s>=0 && s<gui.entityManager.getMap().get(gui.mov.getPos()).getItems().size());
      }
      
      private void select(){
        if(gui.entityManager.getMap().get(gui.mov.getPos()).pickup(selected, gui.inv)){
          if(!inBounds(selected))
            selected--;
          if(selected == -1)
            GAME.change();
          gui.waitLatch.countDown();
        }
        gui.renderer.repaint();
      }
      private void move(Direction d){
        if(inBounds(d.offset.y+selected)){
          selected = d.offset.y+selected;
          gui.renderer.repaint();
        }
      }
      @Override
      public void draw(Graphics2D g2d){
        gui.entityManager.getMap().draw(g2d);
        g2d.drawImage(PICKUP_FILE,20,20,null);
        for(int i = 0; i < gui.entityManager.getMap().get(gui.mov.getPos()).getItems().size(); i++){
          if(i == selected){
            g2d.setColor(Color.BLUE);
          }
          else
            g2d.setColor(Color.WHITE);
          g2d.drawString(gui.entityManager.getMap().get(gui.mov.getPos()).getItems().get(i).name, 100, 100+20*i);
        }
      }
      @Override
      public void keyPressed(KeyEvent e){
        if(gui.waitLatch.getCount() == 0) return;
        switch(e.getKeyCode()){
          case KeyEvent.VK_G:
          case KeyEvent.VK_ESCAPE:           
            GAME.change();
            break;
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
      @Override
      protected void change(){
        selected = 0;
        gui.renderer.repaint();
        gui.state= this;
      }
    };
    
    abstract public void draw(Graphics2D g2d);
    abstract protected void change();
    abstract public void keyPressed(KeyEvent e);
    public void update(){
      gui.entityManager.update();
    }
    protected void clear(){}
  }
  private CountDownLatch waitLatch;
  private Entity player;
  private EntityManager entityManager;
  private CInventory inv;
  private CResources res;
  private CMoving mov;
  private Renderer renderer;
  private State state = State.MAIN_MENU;
  private static GUI gui;
  
  public GUI(){
    renderer = new Renderer(this);
    gui = this;
  }
  public void act(){
    waitLatch = new CountDownLatch(1);
    try{
      waitLatch.await();
    }catch(InterruptedException e){}
  }
  
  public void keyTyped(KeyEvent e){}
  
  public void keyReleased(KeyEvent e){
  }
  public void keyPressed(KeyEvent e){
    state.keyPressed(e);
  }

  public void died(){
    GameMap.clearMaps();
    State.MAIN_MENU.change();
  }
  public void update(){
    state.update();
  }
  public void draw(Graphics2D g2d){
    state.draw(g2d);
  }
}