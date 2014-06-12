import java.util.*;

class Entity{
  private Map<Class, CBase> components = new HashMap<Class, CBase>();
  private static EntityManager manager;
  public boolean live = true;
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
      i.remove();
    } 
  }
}
