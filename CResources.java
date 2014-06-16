class CResources extends CBase{
  private static GameMap map;
  private int maxHP = 10,  baseDefense = 0, baseAttack =5, baseSpeed, level = 1, xp= 0;
  private int currDefense, currAttack, currSpeed,currHP;
  private String drops = "";
  private static int expTable[] = {0,10, 20, 35, 50, 70, 100, 140, 200, 300, 450, 600, 800, 1000, 1300, 1700, 2200, 2800, 3500};
  public int getExpDrop(){
    return expTable[level]/(level/3+1);
  }
  public void equipArmor(InventoryItem i){
  //  if(i == null) return;
    currDefense =baseDefense + i.modifier;
  }
  
  public void unequipArmor(InventoryItem i){
   // if(i == null) return;
    currDefense=baseDefense;
  }
  
  public void equipWep(InventoryItem i){
    currAttack =baseAttack + i.modifier;
    currSpeed = (int)(baseSpeed*i.speedMod);
  }
  
  public void unequipWep(InventoryItem i){
    currAttack=baseAttack;
    currSpeed = baseSpeed;
  }
  public void damage(CResources dam){
    System.out.println(owner.name + " took " +(int)(100f/(currDefense+100)*dam.getAttack()) + " damage from " +dam.owner.name);
    currHP -= (int)(100f/(currDefense+100)*dam.getAttack());
    if(currHP <= 0){
      dam.xp+=getExpDrop();
      System.out.println("Gained " + getExpDrop()+" xp");
      dam.checkLevel();
      owner.kill();
    }
  }
  public void checkLevel(){
    if(xp >= expTable[level]){
      xp-= expTable[level];
      level++;
      baseDefense += level/3+1;
      currDefense += level/3+1;
      baseAttack += level/3+1;
      currAttack += level/3+1;
      maxHP += level/2+1;
      currHP +=level/2+1;
    }
  }
  public int getHP(){return currHP;}
  public int getMaxHP(){return maxHP;}
  public int getDefense(){return currDefense;}
  public int getAttack(){return currAttack;}
  public int getXP(){return xp;}
  public int getNeededXP(){return expTable[level];}
  public int getLevel(){return level;}
  public static void setMap(GameMap m){
    map = m;
  }
  
  @Override
  public void destroy(){
    String [] tokens = drops.split("\\s+");
    if(tokens.length <= 1) return;
    for(int i = 0; i < tokens.length; i+=2){
      int number = RandomNumber.getRand(1, 100);
      if(number <= Integer.parseInt(tokens[i+1])){
        map.get(((CMoving)owner.getComponent(CMoving.class)).getPos()).addItem(tokens[i]);
      }
    }
  }
  
  public CResources(Entity o){
    super(o);
    currDefense = baseDefense;
    currAttack = baseAttack;
    currSpeed = baseSpeed;
    currHP = maxHP;
  }
  
   // (enemyInfo.speed, currHP, enemyInfo.hp, enemyInfo.damage, enemyInfo.defense, enemyInfo.level));
  public CResources(Entity o, int sp, int HP, int mHP, int bAtk, int bDef, int lvl, String dr){
    super(o);
    baseSpeed = sp;
    maxHP = mHP;
    baseAttack = bAtk;
    baseDefense = bDef;
    level = lvl;
    currDefense = baseDefense;
    currAttack = baseAttack;
    currSpeed = baseSpeed;
    currHP = maxHP;
    if(dr != null)
      drops = dr;
  }
}