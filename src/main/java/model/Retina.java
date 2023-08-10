package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Retina implements Serializable
{
  private static final long serialVersionUID = 1L;

  private Map<Integer, Integer> retina;
  
  public Retina(int inputLength)
  {
    retina = new HashMap<Integer, Integer>();
    for(int i = 0; i < inputLength; i++)
    {
      retina.put(i, i);
    }

    Random rnd = new Random();
    for(int i = 0; i < inputLength; i++)
    {
      int randomInd = rnd.nextInt(inputLength);
      
      int temp = retina.get(i);
      retina.put(i, retina.get(randomInd));
      retina.put(randomInd, temp);
    }
  }
  
  public List<Boolean> organize(List<Boolean> input)
  {
    List<Boolean> organizedInput = new ArrayList<Boolean>();
    
    for (int i = 0; i < retina.keySet().size(); i++)
    {
      organizedInput.add(input.get(retina.get(i)));
    }
    
    return organizedInput;
  }
  
  public List<Boolean> sort(List<Boolean> input)
  {
    Boolean[] sortedInput = new Boolean[input.size()];
    
    for(int i = 0; i < retina.keySet().size(); i++)
    {
      sortedInput[retina.get(i)] = input.get(i);
    }
    
    return Arrays.asList(sortedInput);
  }
}
