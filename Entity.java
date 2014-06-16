import java.util.*;
import java.util.regex.*;
class Entity{
  private Map<Class, CBase> components = new HashMap<Class, CBase>();
  private static EntityManager manager;
  public boolean live = true;
  public final String name;
  public final int ID;
  public Entity(String n, int i){
    name =n;
    ID = i;
  }
  public static void setManager(EntityManager m){
    manager = m;
  }
  public void addComponent(CBase component){
    components.put(component.getClass(), component);
  }
  public CBase getComponent(Class ob){
    return components.get(ob);
  }
  public void kill(){
    live = false;
    manager.removeEntity(this);
    Iterator i = components.entrySet().iterator();
    while(i.hasNext()){
      CBase it = (CBase)((Map.Entry)i.next()).getValue();
      it.destroy();
    }
    components = null;
  }
  private static Map<Integer, EnemyInfo> enemys = new HashMap<Integer, EnemyInfo>();
  private static class EnemyInfo{
    public final String name;
    public final int icon;
    public final int level, speed, hp, damage, defense;
    public final String drops;
    public EnemyInfo(List<String> data){
      name = data.get(0);
      icon = TileData.toTile(data.get(1).charAt(0));
      level = Integer.parseInt(data.get(2));
      speed = Integer.parseInt(data.get(3));
      hp = Integer.parseInt(data.get(4));
      damage =Integer.parseInt(data.get(5));
      defense =Integer.parseInt(data.get(6));
      StringBuilder builder = new StringBuilder();
      for(int itr = 7; itr<data.size(); itr++){
        builder.append(data.get(itr) + " ");
      }
      drops = builder.toString();
    }
  }
  private static String[] enemystats = {
       //name           icon level speed hp dam def drops
    "Lichen                 l  1 300  4  1  0", 
    "\"Grid Bug\"           g  1 400  5  1  0", 
    "Rat                    r  1 300  6  2  0 Bronze  10", 
    "Dog                    d  3 250 10  4  1 Bronze  12", 
    "Wildcat                W  4 200  8  6  2 Steel    5 Iron    10 Bronze 15", 
    "\"Fedora Man\"         f  5 500 15  5  5 Black   15 Steel   10", 
    "Owl                    o  3 250 10  4  2 Steel    5 Iron     5 Bronze 20", 
    "Ogre                   O  7 350 25 10 10 Mithril  5 Black   20 Steel  10 Iron 10", 
    "\"Steve Bushcemi\"     e 10 225 30 13 20 Mithril 10 Black   20 Steel  20",
    "Dragon                 D 15 275 40 15 22 Mithril 30 Black   30",
    "\"Spooky Ghost\"       G 18 300 30 30 10 Adamant  5 Mithril 10",
    "Rouge                  R 18 150 20 30 50 Adamant  5 Mithril 15",
    "\"Mr. Mister Donald\"  T 20 175 30 40 10 Adamant 15 Mithril 15",
    "Fiend                  F 22 225 40 40 15 Adamant 20 Black   30",
    "Imp                    I 25 275 45 45 25 Adamant 15 Mithril 20",
    "\"Lich King\"          L 30 250 70 30 30 Amulet 100"};
  static{
    for(int i = 0; i < enemystats.length; i++){
      List<String> list = new ArrayList<String>();
      Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(enemystats[i]);
      while (m.find())
        list.add(m.group(1).replace("\"", "")); // Add .replace("\"", "") to remove surrounding quotes.
      
      enemys.put(i+1, new EnemyInfo(list));
    }
  }
  public static int enemyAmount(){return enemys.size();}
  private static Random randomizer = new Random(new Date().getTime());
  public static int pickEnemy(int level){
    int spread = RandomNumber.getNormalRand(1,30, level, 1);
    if(spread - level > 5) spread = level+5;
    for(Map.Entry<Integer, EnemyInfo> itr : enemys.entrySet()){
      if(itr.getValue().level >= spread){
        return itr.getKey();
      }
    }
    return 0;
  }
  public static Entity createEnemy(int enemyId, int currHP, int posx, int posy){
    EnemyInfo enemyInfo = enemys.get(enemyId);
    Entity enemy = new Entity(enemyInfo.name,enemyId);
    if(currHP == 0) currHP = enemyInfo.hp;
    enemy.addComponent(new CResources(enemy, enemyInfo.speed, currHP, enemyInfo.hp, enemyInfo.damage, enemyInfo.defense, enemyInfo.level, enemyInfo.drops));
    enemy.addComponent(new CMoving(enemy, posx,posy, enemyInfo.icon));
    enemy.addComponent(new CAI(enemy, enemyInfo.speed));
    return enemy;
  }
}
