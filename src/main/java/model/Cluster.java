package model;

import java.awt.Point;
import java.util.ArrayList;

public class Cluster
{
  private ArrayList<Point> pontos;
  private StringBuffer listaPontos = null; 
  
  private static final double CONSTANTE = 2;
  
  public Cluster()
  {
  }
  
  public ArrayList<Point> getPontos()
  {
    return pontos;
  }

  public void adicionarPonto(Point ponto)
  {
    if (listaPontos == null) {
      listaPontos = new StringBuffer("");
    }
    if (pontos == null) {
      pontos = new ArrayList<Point>();
    }
    else {
      listaPontos.append(";");
    }
    
    listaPontos.append("(" + (int) ponto.getX() + "," + (int) ponto.getY() + ")");
     pontos.add(ponto);
  }
  
  public int compareTo(Cluster cluster)
  {
      Integer quantidadePontosCluster1 =  this.getPontos().size();  
      Double comprimentoCluster1 = this.getComprimento();
      Double alturaCluster1 = this.getAltura();
      Integer quantidadePontosCluster2 =  cluster.getPontos().size();
      Double comprimentoCluster2 = cluster.getComprimento();
      Double alturaCluster2 = cluster.getAltura();
        
      int diferencaQuantidadePontos = quantidadePontosCluster1.compareTo(quantidadePontosCluster2);
        
      if((Math.abs(alturaCluster1 - alturaCluster2) < CONSTANTE) && (Math.abs(comprimentoCluster1 - comprimentoCluster2) < CONSTANTE))
      {
        return (int) ((Math.abs(alturaCluster1.compareTo(alturaCluster2)) + 1) * 
                  (Math.abs(comprimentoCluster1.compareTo(comprimentoCluster2)) + 1) * 
                  (Math.signum(diferencaQuantidadePontos) == 0 ? 1 : Math.signum(diferencaQuantidadePontos)) - 1);
      }
      
      return (int) ((Math.abs(alturaCluster1.compareTo(alturaCluster2)) + 1) * 
                (Math.abs(comprimentoCluster1.compareTo(comprimentoCluster2)) + 1) * 
                (Math.signum(diferencaQuantidadePontos) == 0 ? 1 : Math.signum(diferencaQuantidadePontos)) + 
                diferencaQuantidadePontos - 1);
 }
  
  public double getComprimento()
  {
    double menorX = pontos.get(0).getX();
    double maiorX = menorX;
    
    for(Point ponto : pontos)
    {
      double x = ponto.getX();
      
      if(x < menorX)
      {
        menorX = x;
      }
      if(x > maiorX)
      {
        maiorX = x;
      }
    }
    
    return maiorX - menorX;
  }
  
  public double getAltura()
  {
    double menorY = pontos.get(0).getY();
    double maiorY = menorY;
    
    for(Point ponto : pontos)
    {
      double y = ponto.getY();
      
      if(y < menorY)
      {
        menorY = y;
      }
      if(y > maiorY)
      {
        maiorY = y;
      }
    }
    
    return maiorY - menorY;
  }
}