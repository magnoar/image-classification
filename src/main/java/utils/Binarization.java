package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Binarization
{
  private static final int THRESHOLD_BINARIZACAO = 127;
  
  public static List<Boolean> binarizar(BufferedImage imagem)
  {
    List<Boolean> binarizacao = new ArrayList<Boolean>();
    
    Color cor;
    double luminanciaMedia = 0;
    
    for(int x=0; x<imagem.getWidth(); x++)
    {
      for(int y=0; y<imagem.getHeight(); y++)
      {
        cor = new Color(imagem.getRGB(x, y));
        luminanciaMedia += obterLuminancia(cor);
      }
    }
    
    luminanciaMedia /= imagem.getWidth() * imagem.getHeight();
    
    for(int x=0; x<imagem.getWidth(); x++)
    {
      for(int y=0; y<imagem.getHeight(); y++)
      {
        cor = new Color(imagem.getRGB(x, y));
        
        double luminancia = obterLuminancia(cor);
        
        binarizacao.add(luminancia >= luminanciaMedia);
      }
    }
    
    return binarizacao;
  }
  
  public static List<Boolean> discretizar(BufferedImage imagem, int nivelLuminancia, int nivelA, int nivelB)
  {
    List<Boolean> discretizacao = new ArrayList<Boolean>();
    
    BufferedImage imagemDiscretizada = ImageUtils.discretizeColors("", "", imagem, nivelLuminancia, nivelA, nivelB);
    
    for(int x=0; x<imagemDiscretizada.getWidth(); x++)
    {
      for(int y=0; y<imagemDiscretizada.getHeight(); y++)
      {
        Color cor = new Color(imagem.getRGB(x, y));
        int[] niveisLab = ImageUtils.getLABLevels(cor, nivelLuminancia, nivelA, nivelB);
        
        for (int i = 0; i < nivelLuminancia - 1; i++)
        {
          discretizacao.add(niveisLab[0] > i);
        }
        for (int i = 0; i < nivelA - 1; i++)
        {
          discretizacao.add(niveisLab[1] > i);
        }
        for (int i = 0; i < nivelB - 1; i++)
        {
          discretizacao.add(niveisLab[2] > i);
        }
      }
    }
    
    return discretizacao;
  }
  
  private static double obterLuminancia(Color cor)
  {
    return 0.2126 * cor.getRed() + 0.7152 * cor.getGreen() + 0.0722 * cor.getBlue();
  }
  
  public static List<Boolean> binarizarEscalaDeCinza(BufferedImage imagem)
  {
    List<Boolean> binarizacao = new ArrayList<Boolean>();
    
    Color cor;
    int r,g,b;
    
    for(int x=0; x<imagem.getWidth(); x++)
    {
      for(int y=0; y<imagem.getHeight(); y++)
      {
        cor = new Color(imagem.getRGB(x, y));
        r = cor.getRed();
        g = cor.getGreen();
        b = cor.getBlue();
        
        binarizacao.add((r + g + b) / 3 >= THRESHOLD_BINARIZACAO);
      }
    }
    
    return binarizacao;
  }
}
