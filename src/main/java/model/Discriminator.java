package model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Discriminator implements Serializable
{
  private static final long serialVersionUID = 1L;

  private RAM[] rams;
  private int numeroEntradasRAMs;
  private int tamanhoEntrada;
  
  public Discriminator(int numeroEntradasRAMs, int tamanhoEntrada)
  {
    this.numeroEntradasRAMs = numeroEntradasRAMs;
    this.tamanhoEntrada = tamanhoEntrada;
    buildRAMs();
  }
  
  public void train(List<Boolean> entradaReorganizada)
  {
    int posicaoAtual = 0;
    
    for(int i=0; i<rams.length; i++)
    {
      List<Boolean> bloco = new ArrayList<Boolean>();
      
      for (int j = posicaoAtual; j < Math.min(posicaoAtual + numeroEntradasRAMs, entradaReorganizada.size()); j++)
      {
        bloco.add(entradaReorganizada.get(j));
      }      
      posicaoAtual += numeroEntradasRAMs;
      
      int valorEntrada = NeuralNetworkWithDiscriminators.convertToInt(bloco);
      rams[i].increment(valorEntrada);
    }
  }
  
  public int classify(List<Boolean> entradaReorganizada, int bleaching)
  {
    int saida = 0;
    
    int posicaoAtual = 0;
    for(int i = 0; i < rams.length; i++)
    {
      List<Boolean> bloco = new ArrayList<Boolean>();
      
      for (int j = posicaoAtual; j < Math.min(posicaoAtual + numeroEntradasRAMs, entradaReorganizada.size()); j++)
      {
        bloco.add(entradaReorganizada.get(j));
      }      
      posicaoAtual += numeroEntradasRAMs;
      
      int valorEntrada = NeuralNetworkWithDiscriminators.convertToInt(bloco);
  
      if (rams[i].containsKey(valorEntrada))
      {
        int saidaRAM = rams[i].get(valorEntrada);
        
        if (saidaRAM >= bleaching)
        {
          saida++;
        }
      }
    }
    
    return saida;
  }
  
  private void buildRAMs()
  {
    int numeroRAMs = (int) Math.ceil((double)tamanhoEntrada/(double)numeroEntradasRAMs);
    rams = new RAM[numeroRAMs];
    for (int i = 0; i < numeroRAMs; i++)
    {
      rams[i] = new RAM();
    }
  }
  
  public BufferedImage createMentalImage(List<Boolean> entrada)
  {
    BufferedImage imagemMental = new BufferedImage(ImageSetup.WIDTH, ImageSetup.HEIGHT, BufferedImage.TYPE_BYTE_BINARY);
        
    for(int x=0; x<imagemMental.getWidth(); x++)
    {
      for(int y=0; y<imagemMental.getHeight(); y++)
      {
        Color cor = null;
        
        if (entrada.get(x*imagemMental.getHeight() + y) == true)
        {
          cor = Color.WHITE;
        }
        else
        {
          cor = Color.BLACK;
        }
        
        imagemMental.setRGB(x, y, cor.getRGB());
      }
    }
    
    return imagemMental;
  }
  
  public List<Boolean> generateInput()
  {
    Boolean[] entrada = new Boolean[tamanhoEntrada];
    
    Random random = new Random();
    int posicaoAtual = 0;

    for(int i = 0; i < rams.length; i++)
    {
      Map<Integer, Integer> ram = rams[i].getRam();
      int numeroDeAcessos = 0;
      
      for(Integer valor: ram.values())
      {
        numeroDeAcessos += valor;
      }
      int randomNumAc = random.nextInt(numeroDeAcessos);
      
      int chaveEscolhida = -1;
      for(Integer chave : ram.keySet())
      {
        randomNumAc -= ram.get(chave);
        
        if(randomNumAc<0)
        {
          chaveEscolhida = chave;
          break;
        }
      }
      
      String listaBool = Integer.toBinaryString(chaveEscolhida);
      
      while (listaBool.length() < Math.min(numeroEntradasRAMs, entrada.length - posicaoAtual))
      {
        listaBool = "0" + listaBool;
      }
      
      for(int j = listaBool.length() - 1; j > -1; j--)
      {
        if (listaBool.charAt(j)=='1')
        {
          entrada[posicaoAtual + listaBool.length() - j - 1] = true;
        }
        else
        {
          entrada[posicaoAtual + listaBool.length() - j - 1] = false;          
        }
      }
      posicaoAtual += listaBool.length();
    }
      
    return Arrays.asList(entrada);
  }
}
