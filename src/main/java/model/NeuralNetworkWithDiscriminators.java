package model;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;


public class NeuralNetworkWithDiscriminators implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Retina retina;
  private Discriminator[] discriminadores;
  private int[] classificacaoDiscriminadores;
  
  public NeuralNetworkWithDiscriminators(int numeroEntradasRAMs, int tamanhoEntrada, int numeroDiscriminadores)
  {
    retina = new Retina(tamanhoEntrada);
    discriminadores = new Discriminator[numeroDiscriminadores];
    classificacaoDiscriminadores = new int[numeroDiscriminadores];
    
    for(int i = 0; i < discriminadores.length; i++)
    {
      discriminadores[i] = new Discriminator(numeroEntradasRAMs, tamanhoEntrada);
    }
  }
  
  public NeuralNetworkWithDiscriminators(String pathToParent)
  {
    ObjectInputStream in = null;
    NeuralNetworkWithDiscriminators nn = null;

    try 
    {
        FileInputStream fist = new FileInputStream(pathToParent);
        in = new ObjectInputStream(fist);
        nn = (NeuralNetworkWithDiscriminators) in.readObject();
        in.close();
    } catch (IOException ex) 
    {
        ex.printStackTrace();
    } catch (ClassNotFoundException ex) 
    {
        ex.printStackTrace();
    }
    
    retina = nn.retina;
    discriminadores = nn.discriminadores;
    classificacaoDiscriminadores = nn.classificacaoDiscriminadores;
  }
  
  public Discriminator[] getDiscriminadores()
  {
    return discriminadores;
  }

  public void train(List<Boolean> entrada, int discriminador)
  {
    List<Boolean> entradaReorganizada = retina.organize(entrada);
    discriminadores[discriminador].train(entradaReorganizada);
  }
  
  public int classify(List<Boolean> entrada, double confianca, int bleaching) throws IOException
  {
    List<Boolean> entradaReorganizada = retina.organize(entrada);
    
    for(int i = 0; i < discriminadores.length; i++)
    {
      classificacaoDiscriminadores[i] = 
    		  discriminadores[i].classify(entradaReorganizada, bleaching);
    }
    
    int classe;
    
    if(getBestResult(classificacaoDiscriminadores) == 0)
    {
      return -1;
    }
    
    if(confianca > calculateConfidence())
    {
      classe = classify(entrada, confianca, bleaching+1);
    }
    else
    {
      classe = obterClasse(classificacaoDiscriminadores, getBestResult(classificacaoDiscriminadores));
    }
    
    return classe;
  }
  
  public BufferedImage[] createMentalImage()
  {
    BufferedImage[] imagensMentais = new BufferedImage[discriminadores.length];
    
    for(int i = 0; i < discriminadores.length; i++)
    {
      List<Boolean> entrada = retina.sort(discriminadores[i].generateInput());
      imagensMentais[i] = discriminadores[i].createMentalImage(entrada);
    }
    
    return imagensMentais;
  }
  
  public static int convertToInt(List<Boolean> bloco)
  {
    int valor = 0;
    
    for (int i = 0; i < bloco.size(); i++)
    {
      if (bloco.get(i))
      {
        valor += (int) Math.pow(2, i);
      }
    }
    
    return valor;
  }
  
  public double calculateConfidence()
  {
    int melhorResultado = getBestResult(classificacaoDiscriminadores);
    int segundoMelhorResultado = getSecondBestResult(classificacaoDiscriminadores);
    
    if(melhorResultado == 0)
    {
      return 0;
    }
    return ((double)melhorResultado - (double)segundoMelhorResultado)/(double)melhorResultado;
  }
  
  public int obterClasse(int[] classificacaoDiscriminadores, int valor)
  {
    for(int i = 0; i < classificacaoDiscriminadores.length; i++)
    {
      if(classificacaoDiscriminadores[i] == valor)
      {
        return i;
      }
    }
    return classificacaoDiscriminadores.length-1;
  }
  
  public int getBestResult(int[] valores)
  {
    int melhorResultado = 0;
    
    for(int i = 0; i < valores.length; i++)
    {
      if(valores[i] > melhorResultado)
      {
        melhorResultado = valores[i];
      }
    }
    
    return melhorResultado;
  }
  
  public int getSecondBestResult(int[] valores)
  {
    int[] novosValores = new int[discriminadores.length-1];
    int melhorResultado = getBestResult(valores);
    int j = 0;
    
    for(int i = 0; i < valores.length; i++)
    {
      if(valores[i] == melhorResultado)
      {
        i++;
      }
      
      if(i < valores.length)
      {
        novosValores[j] = valores[i];
        j++;
      }
    }
    
    return getBestResult(novosValores);
  }
  
}
