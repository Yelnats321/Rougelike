import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Renderer extends JPanel/* implements ActionListener*/{
  private JFrame mainFrame;
  private final int WINDOW_X = 640, WINDOW_Y = 480;
  private Position offset = new Position(0,0);
  private static GameMap map;
  private static Entity player;
  private GUI gui;
  
  public static void setMap(GameMap m){
    map = m;
  }
  public static void setPlayer(Entity p){
    player = p;
  }
  public Renderer(GUI g){      
    gui = g;
    EntityManager.setRenderer(this);
    setDoubleBuffered(true);
    setBackground(Color.BLACK);
    this.setPreferredSize(new Dimension(WINDOW_X,WINDOW_Y));
    mainFrame = new JFrame("Roguelike");
    mainFrame.add(this);
    mainFrame.setResizable(false);
    mainFrame.setVisible(true);
    mainFrame.addKeyListener(g);
    mainFrame.pack();
    mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    mainFrame.addWindowListener(new WindowAdapter(){
      public void windowClosing(WindowEvent e){
        GameMap.clearMaps();
      }
    });
  }
  public void updateOffset(){
    final int tilesPerX = WINDOW_X/TileData.TILE_X, tilesPerY = WINDOW_Y/TileData.TILE_Y;
    final int width = map.getWidth(), height = map.getHeight();
    final int posX = ((CMoving)player.getComponent(CMoving.class)).getX(), posY = ((CMoving)player.getComponent(CMoving.class)).getY();
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
    //System.out.println("repainted");
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    if(gui.getState() != GUI.State.MAIN_MENU)
      map.draw(g2d, this);
    gui.draw(g2d);
  }
  
  public static void main(String [] args){
    GUI gui = new GUI();

    while(true){
      gui.update();
     // System.out.println("thing happened");
    }
  }
}
