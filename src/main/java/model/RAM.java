package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RAM implements Serializable
{
  private static final long serialVersionUID = 1L;

  private Map<Integer, Integer> ram;
  
  public Map<Integer, Integer> getRam()
  {
    return ram;
  }

  public RAM()
  {
    ram = new HashMap<Integer, Integer>();
  }
  
  public void increment(int position)
  {
    if (!ram.containsKey(position))
    {
      ram.put(position, 0);
    }
    
    ram.put(position, ram.get(position) + 1);
  }
  
  public Set<Integer> getPositions()
  {
    return ram.keySet();
  }
  
  public int get(int position)
  {
    return ram.get(position);
  }
  
  public boolean containsKey(int position)
  {
    return ram.keySet().contains(position);
  }
}
