package utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import model.Pair;

public class ImageUtils
{
	
  public static BufferedImage loadImage(String ref) 
  {  
    BufferedImage bimg = null;  
    try
    {  
        bimg = ImageIO.read(new File(ref));  
    } 
    catch (Exception e)
    {  
        e.printStackTrace();  
    }
    return bimg;  
  }

  public static void saveToDisk(BufferedImage trans, String local)
  {  
    try
    {        
      ImageIO.write(trans, "jpg", new File(local)); 
    }
    catch (IOException e)
    {  
          e.printStackTrace();  
    } 
  }
  
  public static BufferedImage resizeImage(
		  BufferedImage imagemOriginal, 
		  int larguraDesejada, 
		  int alturaDesejada)
  {
    BufferedImage imagemRedimensionada;
    
    double px = (double) larguraDesejada / (double) imagemOriginal.getWidth();
    double py = (double) alturaDesejada / (double) imagemOriginal.getHeight();
        
    //px e py: usar apenas um valor pra manter proporcao, usar o menor 
    
    if (px > py)
    {
      py = px;
    }
    if (px < py)
    {
      px = py;
    }
    
    int larguraRedimensionada =  (int) (imagemOriginal.getWidth()*px);
    int alturaRedimensionada = (int) (imagemOriginal.getHeight()*py);

    imagemRedimensionada = new BufferedImage(
    		larguraRedimensionada, 
    		alturaRedimensionada, 
    		imagemOriginal.getType());

    for (int i = 0; i < larguraRedimensionada; i++)
    {
      for (int j = 0; j < alturaRedimensionada; j++)
      {
          int color = imagemOriginal.getRGB(i*imagemOriginal.getWidth()/larguraRedimensionada,
                                            j*imagemOriginal.getHeight()/alturaRedimensionada);
          //rever
          imagemRedimensionada.setRGB(i, j, color);
      }
    }
     
    BufferedImage imagemDesejada;
    imagemDesejada = new BufferedImage(larguraDesejada, alturaDesejada, imagemOriginal.getType());
    
    //corta altura
    if (imagemRedimensionada.getHeight() > alturaDesejada)
    {
      int corte = imagemRedimensionada.getHeight() - alturaDesejada;
      for (int i = 0; i < larguraRedimensionada; i++)
      {
        for (int j = corte/2; j < alturaRedimensionada - (corte/2) -1; j++) 
        {
          int color = imagemRedimensionada.getRGB(i, j);
          imagemDesejada.setRGB(i, j - corte/2, color);
        }
      }
    }
    
    //corta largura
    if (imagemRedimensionada.getWidth() > larguraDesejada)
    {
      int corte = imagemRedimensionada.getWidth() - larguraDesejada;
      for (int i = corte/2; i < larguraRedimensionada -1 - corte/2; i++)
      {
        for (int j = 0; j < alturaRedimensionada; j++)
        {
          int color = imagemRedimensionada.getRGB(i, j);
          imagemDesejada.setRGB(i - corte/2, j, color);
        }
      }
    }

    return imagemDesejada;
  }
  
  public static BufferedImage resizeImage(
		  String path, 
		  int larguraDesejada, 
		  int alturaDesejada)
  {
	  return resizeImage(loadImage(path), larguraDesejada, alturaDesejada);
  }
  
  public static BufferedImage cropImage(
		  BufferedImage imagemOriginal, 
		  int larguraDesejada, 
		  int alturaDesejada, 
		  int xPos, 
		  int yPos)
  {
    BufferedImage imagemCortada;
    imagemCortada = new BufferedImage(larguraDesejada, alturaDesejada, imagemOriginal.getType());

    for (int i = xPos; i < (larguraDesejada + xPos); i++)
    {
      for (int j = yPos; j < (alturaDesejada + yPos); j++)
      {
          int color = imagemOriginal.getRGB(i,j);
          
          imagemCortada.setRGB(i - xPos, j - yPos, color);
      }
    }
   
    return imagemCortada;
  }
  public static BufferedImage normalizeLuminance(BufferedImage imagem)
  {
    double luminanciaMedia = 0.0;
    
    for (int y = 0; y < imagem.getHeight(); y++)
    {
      for (int x = 0; x < imagem.getWidth(); x++)
      {
        Color c = new Color(imagem.getRGB(x, y));
        
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        
        luminanciaMedia += .299 * r + .587 * g + .114 * b;
      }
    }
    
    luminanciaMedia /= imagem.getHeight() * imagem.getWidth();
    
    BufferedImage imagemNormalizada = 
    		new BufferedImage(imagem.getWidth(), imagem.getHeight(), imagem.getType());
    
    for (int y = 0; y < imagem.getHeight(); y++)
    {
      for (int x = 0; x < imagem.getWidth(); x++)
      {
        Color c = new Color(imagem.getRGB(x, y));
        
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();        
        
        double luminancia = .299 * r + .587 * g + .114 * b;
        
        r = (int) Math.min(0xff, Math.max(0x00, r * luminanciaMedia / luminancia));
        g = (int) Math.min(0xff, Math.max(0x00, g * luminanciaMedia / luminancia));
        b = (int) Math.min(0xff, Math.max(0x00, b * luminanciaMedia / luminancia));
        
        c = new Color(r, g, b);
        imagemNormalizada.setRGB(x, y, c.getRGB());
      }
    }
    
    return imagemNormalizada;
  }
  
  public static BufferedImage discretizeColors(
		  String folder, 
		  String foto, 
		  BufferedImage imagem, 
		  double nivelLuminancia, 
		  double nivelA, 
		  double nivelB)
  {
    BufferedImage imagemDiscretizada = 
    		new BufferedImage(imagem.getWidth(), imagem.getHeight(), imagem.getType());
    
    Map<Pair, List<Double>> mapeamentoLuminancia = new HashMap<Pair, List<Double>>();
    Map<Pair, Double> mapeamentoLuminanciaMedia = new HashMap<Pair, Double>();
    
    Map<Pair, Pair> mapeamentoXYAB = new HashMap<Pair, Pair>();
    
    for (int y = 0; y < imagem.getHeight(); y++)
    {
      for (int x = 0; x < imagem.getWidth(); x++)
      {
        Color cor = new Color(imagem.getRGB(x, y));
  
        int[] niveisLab = getLABLevels(cor, nivelLuminancia, nivelA, nivelB);
        double[] lab = new double[niveisLab.length];
        
        //lab[0] = (niveisLab[0] + 0.5) / nivelLuminancia * 100.;
        lab[0] = ColorConverter.xyzParaLab(ColorConverter.rgbParaXyz(cor))[0];
        lab[1] = (niveisLab[1] + 0.5) / nivelA * 220. - 110;
        lab[2] = (niveisLab[2] + 0.5) / nivelB * 220. - 110;
        
        cor = ColorConverter.xyzParaRgb(ColorConverter.labParaXyz(lab));        
        imagemDiscretizada.setRGB(x, y, cor.getRGB());
        
        Pair parAB = new Pair(lab[1], lab[2]);
        
        if (!mapeamentoLuminancia.containsKey(parAB))
        {
          mapeamentoLuminancia.put(parAB, new ArrayList<Double>());
        }
        
        List<Double> luminancias = mapeamentoLuminancia.get(parAB);
        luminancias.add(lab[0]);
        mapeamentoLuminancia.put(parAB, luminancias);
        
        mapeamentoXYAB.put(new Pair(x, y), parAB);
      }
    }
    
    ImageUtils.saveToDisk(imagemDiscretizada, folder + "discretizacao/" + foto);
    
    for (Pair parAB : mapeamentoLuminancia.keySet())
    {
      double luminanciaMedia = 0.0;
      
      for (Double luminancia : mapeamentoLuminancia.get(parAB))
      {
        luminanciaMedia += luminancia;
      }
      
      luminanciaMedia /= mapeamentoLuminancia.get(parAB).size();
      luminanciaMedia /= 100.;
      
      int nivelLuminanciaMedia = (int) Math.max(0., Math.min(Math.floor(luminanciaMedia * nivelLuminancia), nivelLuminancia - 1));
      luminanciaMedia = (nivelLuminanciaMedia + 0.5) / nivelLuminancia * 100.;
      
      mapeamentoLuminanciaMedia.put(parAB, luminanciaMedia);
    }
    
    for (int y = 0; y < imagem.getHeight(); y++)
    {
      for (int x = 0; x < imagem.getWidth(); x++)
      {
        double a = mapeamentoXYAB.get(new Pair(x, y)).getA();
        double b = mapeamentoXYAB.get(new Pair(x, y)).getB();
        
        double[] lab = new double[]{mapeamentoLuminanciaMedia.get(new Pair(a, b)), a, b};
        
        Color cor = ColorConverter.xyzParaRgb(ColorConverter.labParaXyz(lab));        
        imagemDiscretizada.setRGB(x, y, cor.getRGB());
      }
    }
    
    ImageUtils.saveToDisk(imagemDiscretizada, folder + "discretizacaoMaior/" + foto);
    
    return imagemDiscretizada;
  }
  
  public static int[] getLABLevels(Color cor, double nivelLuminancia, double nivelA, double nivelB)
  {
    double[] lab = ColorConverter.xyzParaLab(ColorConverter.rgbParaXyz(cor)); 
    
    lab[0] /= 100.;
    lab[1] = (lab[1] + 110) / 220.;
    lab[2] = (lab[2] + 110) / 220.;
    
    int[] niveisLab = new int[lab.length];
    
    niveisLab[0] = (int) Math.max(0., Math.min(Math.floor(lab[0] * nivelLuminancia), nivelLuminancia - 1));
    niveisLab[1] = (int) Math.max(0., Math.min(Math.floor(lab[1] * nivelA), nivelA - 1));
    niveisLab[2] = (int) Math.max(0., Math.min(Math.floor(lab[2] * nivelB), nivelB - 1));
    
    return niveisLab;
  }
}
