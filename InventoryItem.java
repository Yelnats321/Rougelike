import java.util.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import java.awt.*;

public class InventoryItem{
  public enum Type{
    MISC, ARMOR, WEAPON;
  }
  public enum Quality{
    Bronze(new Color(205,127,50)), Iron(new Color(102,102,102)), Steel(new Color(204,204,204)), Black(new Color(230,230,230)), Mithril(new Color(130, 130, 146)), Adamant(new Color(102,204,0));
    public final Color color;
    public final float mod;
    public final static Quality[] vals = values();
    Quality(Color c){
      color =c;
      mod = (float)Math.pow(1.35,ordinal());
    }
  }
  
  public enum Weapons{
    Dirk(1.3f, 0.95f), Dagger(1f, 1.3f), Longsword(1.4f, 1.1f), Scimitar(1.3f, 1.1f), Warhammer(1.6f, 0.7f), Battleaxe(1.35f, 0.8f);
    private final float damage, speedMod;
    public final static Weapons[] vals = values();
    Weapons(float m, float sm){
      damage = m;
      speedMod =sm;
    }
  }
  
  public final String name;
  public final int weight, modifier;
  public final Type type;
  public final float speedMod;
  public final Quality quality;
  private final Weapons wep;
  
  private final int IMG_SIZE = 28;
  private static final String FILENAME = "weapons.png";
  private static final BufferedImage FILE;
  private static Map<String, InventoryItem> items = new HashMap<String, InventoryItem>();
  
  private InventoryItem(Quality q, Weapons wep){
    this.wep = wep;
    quality = q;
    name = q.name() + " " + wep.name();
    weight = 10;
    type = Type.WEAPON;
    modifier = (int)(5*wep.damage* q.mod);
    speedMod = wep.speedMod;
  }
  private InventoryItem(Quality q){
    wep = null;
    quality = q;
    name = q.name() + " Chestplate";
    weight = 15;
    type = Type.ARMOR;
    modifier = (int)(5*q.mod);
    speedMod = 1f;
  }
  public static InventoryItem get(String n){
    return items.get(n);
  }
  public void draw(Graphics2D g2d,int x, int y){
    if(wep != null){
      g2d.drawImage(FILE, x,y,x+IMG_SIZE,y+IMG_SIZE,
                    wep.ordinal()*IMG_SIZE,quality.ordinal()*IMG_SIZE,(wep.ordinal()+1)*IMG_SIZE,(quality.ordinal()+1)*IMG_SIZE, null);
    }
    else 
      g2d.drawImage(FILE, x,y,x+IMG_SIZE,y+IMG_SIZE,
                    6*IMG_SIZE,quality.ordinal()*IMG_SIZE,7*IMG_SIZE, (quality.ordinal()+1)*IMG_SIZE, null);
  }
  static{
    try{
      FILE = ImageIO.read(new File(FILENAME));
    }catch(IOException e){
      throw new RuntimeException("Item picture file missing");
    }
    for(int t = 0; t <Quality.vals.length; t++){
      for(int w = 0; w < Weapons.vals.length; w++){
        items.put(Quality.vals[t].name()+" " + Weapons.vals[w].name(), 
                  new InventoryItem(Quality.vals[t], Weapons.vals[w]));
      }
      items.put(Quality.vals[t].name() + " Chestplate", 
                new InventoryItem(Quality.vals[t] ));
    }
  }
}