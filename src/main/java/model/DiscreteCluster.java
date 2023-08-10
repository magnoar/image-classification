package model;

import java.awt.Point;
import java.util.ArrayList;

public class DiscreteCluster extends Cluster implements Comparable<Cluster>
{
  private ArrayList<Point> pontos;
  private StringBuffer listaPontos = null;
  private DiscreteCluster simetrico;
  private int cor;
  
  public DiscreteCluster(int cor)
  {
    pontos = new ArrayList<Point>();
    this.cor = cor;
  }
  
  public String toString()
  {    
    return Integer.toHexString(this.cor).substring(2) + "->" + 
    		(this.listaPontos == null ? "()" : this.listaPontos);
  }
  
  public void setSimetrico(DiscreteCluster simetrico)
  {
    this.simetrico = simetrico;
  }
  
  public int getCor()
  {
    return cor;
  }

  public DiscreteCluster getSimetrico()
  {
    return simetrico;
  }
  
  public ArrayList<Point> getPontos()
  {
    return pontos;
  }

}