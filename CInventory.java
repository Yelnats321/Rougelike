public class CInventory extends CBase{
  private final int SIZE = 18;
  private final int MAXWEIGHT = 50;
  private int currWeight;
  private InventoryItem equippedArm = null, equippedWep;
  private InventoryItem[] items = new InventoryItem[SIZE];
  
  public CInventory (Entity o){
    super(o);
    addItem(InventoryItem.get("Adamant Dirk"));
    addItem(InventoryItem.get("Steel Chestplate"));
  }
  
  public boolean addItem(InventoryItem item){
    if(item.weight + currWeight > MAXWEIGHT){
      //add some sort of renderer printer
      System.out.println("Can't pick up, too heavy");
      return false;
    }
    for(int i = 0; i < SIZE; i++){
      if(items[i] == null){
        items[i] = item;
        return true;
      }
    }
    System.out.println("Can't pick up item, not enough space");
    return false;
  }
  public void equip(InventoryItem item){
    CResources res = (CResources)owner.getComponent(CResources.class);
    if(item.type == InventoryItem.Type.MISC) return;
    if(item.type == InventoryItem.Type.WEAPON){
      if(equippedWep != null)
        res.unequipWep(equippedWep);
      res.equipWep(item);
      equippedWep = item;
    }
    else{
      if(equippedArm !=null)
        res.unequipArmor(equippedArm);
      res.equipArmor(item);
      equippedArm = item;
    }
  }
  public void unequip(InventoryItem item){
    CResources res = (CResources)owner.getComponent(CResources.class);
    if(item.type == InventoryItem.Type.MISC) return;
    if(item.type == InventoryItem.Type.WEAPON){
      res.unequipWep(equippedWep);
      equippedWep = null;
    }
    else{
      res.unequipArmor(equippedArm);
      equippedArm = null;
    }
  }
  public InventoryItem getItem(int x){ return items[x];}
}