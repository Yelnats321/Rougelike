import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Renderer extends JPanel/* implements ActionListener*/{
  private JFrame mainFrame;
  private final int WINDOW_X = 640, WINDOW_Y = 480;
  private Position offset = new Position(0,0);
  private EntityManager manager;
  private GUI gui;
  
  
  public Renderer(EntityManager m, GUI g){      
    manager = m;
    gui = g;
    setDoubleBuffered(true);
    this.setPreferredSize(new Dimension(WINDOW_X,WINDOW_Y));
    mainFrame = new JFrame("Roguelike");
    mainFrame.add(this);
    mainFrame.setResizable(false);
    mainFrame.setVisible(true);
    mainFrame.addKeyListener(g);
    mainFrame.pack();
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
  public void updateOffset(){
    final int tilesPerX = WINDOW_X/TileData.TILE_X, tilesPerY = WINDOW_Y/TileData.TILE_Y;
    final int width = manager.getMap().getWidth(), height = manager.getMap().getHeight();
    final int posX = ((CMoving)manager.getPlayer().getComponent(CMoving.class)).getX(), posY = ((CMoving)manager.getPlayer().getComponent(CMoving.class)).getY();
    if(width <=tilesPerX)
      offset.x = WINDOW_X/2- width*TileData.TILE_X/2; 
    else{
      if(posX < tilesPerX/2)
        offset.x = 0;
      else if(posX > width - tilesPerX/2)
        offset.x = (tilesPerX/2 - width)*TileData.TILE_X+WINDOW_X/2;
      else
        offset.x = WINDOW_X/2-posX*TileData.TILE_X;
    }
    
    if(height <=tilesPerY)
      offset.y = WINDOW_Y/2- height*TileData.TILE_Y/2;    
    else{
      if(posY < tilesPerY/2)
        offset.y = 0;
      else if(posY > height - tilesPerY/2)
        offset.y = (tilesPerY/2 - height)*TileData.TILE_Y+WINDOW_Y/2;
      else
        offset.y = WINDOW_Y/2-posY*TileData.TILE_Y;
    }
  }
  public Position getOffset(){return offset;}
  
  @Override
  public void paintComponent(Graphics g){
    System.out.println("repainted");
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    manager.getMap().draw(g2d, this);
    gui.draw(g2d);
  }
  
  public static void main(String [] args){
    EntityManager manager = new EntityManager();
    Entity.setManager(manager);
    manager.changeMap("map.txt");
    manager.getPlayer().addComponent(new CMoving(manager.getPlayer(),6 ,5));
    manager.getPlayer().addComponent(new CResources(manager.getPlayer()));
    manager.getPlayer().addComponent(new CLOS(manager.getPlayer(), 6));
    manager.getPlayer().addComponent(new CInventory(manager.getPlayer()));
    CAI.setManager(manager);
    GUI gui = new GUI();
    Renderer rend = new Renderer(manager, gui);
    gui.setRenderer(rend);
    gui.setPlayer(manager.getPlayer());
    manager.getPlayer().addComponent(new CActor(manager.getPlayer(), 10, gui));
    
    Entity enemy1 = new Entity();
    enemy1.addComponent(new CMoving(enemy1, 6,6));
    enemy1.addComponent(new CAI(enemy1, 9));
    enemy1.addComponent(new CResources(enemy1));
    manager.addEntity(enemy1);
    
    
    while(true){
      manager.update(rend);
      System.out.println("thing happened");
    }
  }
}
