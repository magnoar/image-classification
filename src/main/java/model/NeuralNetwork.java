package model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import utils.ColorConverter;

public class NeuralNetwork implements Serializable
{
  private static final long serialVersionUID = 1L;

  private int thresholdBleaching;
  private int numeroEntradasRAMs;
  private int tamanhoEntrada;
  private Retina retina;
  private RAM[] rams;
  private int thresholdInferior;
  private int thresholdSuperior;
  
  public NeuralNetwork(int thresholdBleaching, int numeroEntradasRAMs, int tamanhoEntrada)
  {	
	System.out.println(tamanhoEntrada);
	
    this.thresholdBleaching = thresholdBleaching;
    this.numeroEntradasRAMs = numeroEntradasRAMs;
    this.tamanhoEntrada = tamanhoEntrada;
    retina = new Retina(tamanhoEntrada);
    
    buildRAMs();
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
  
  public int getRAMsLength()
  {
    return rams.length;
  }
  
  public RAM[] getRAMs()
  {
    return rams;
  }
  
  public void train(List<Boolean> entrada)
  {
    List<Boolean> entradaReorganizada = retina.organize(entrada);
    int posicaoAtual = 0;
    
    for(int i=0; i<rams.length; i++)
    {
      List<Boolean> bloco = new ArrayList<Boolean>();
      
      for (int j = posicaoAtual; j < Math.min(posicaoAtual + numeroEntradasRAMs, entradaReorganizada.size()); j++)
      {
        bloco.add(entradaReorganizada.get(j));
      }      
      posicaoAtual += numeroEntradasRAMs;
      
      int valorEntrada = convertToInt(bloco);
      rams[i].increment(valorEntrada);
    }
  }
  
  public int classify(List<Boolean> entrada)
  {
    List<Boolean> entradaReorganizada = retina.organize(entrada);
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
      
      int valorEntrada = convertToInt(bloco);
      
      if (rams[i].containsKey(valorEntrada))
      {
        int saidaRAM = rams[i].get(valorEntrada);
        
        if (saidaRAM >= thresholdBleaching)
        {
          saida++;
        }
      }
    }
    
    return saida;
  }
  
  public BufferedImage createBinarizedMentalImage(int largura, int altura)
  {
    BufferedImage imagemMental = new BufferedImage(largura, altura, BufferedImage.TYPE_BYTE_BINARY);
    
    List<Boolean> entrada = retina.sort(generateInput());
    
    System.out.println("ENTRADA:"+entrada.size());
    
    for(int x=0; x<imagemMental.getWidth(); x++)
    {
      for(int y=0; y<imagemMental.getHeight(); y++)
      {
        Color cor = null;
        
        System.out.println(x+";"+y);
        
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
  
  public BufferedImage createDiscretizedMentalImage(
		  int largura, int altura, int nivelLuminancia, int nivelA, int nivelB)
  {
    BufferedImage imagemMental = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
    
    List<Boolean> entrada = retina.sort(generateInput());
    
    //System.out.println("ENTRADA:"+entrada.size());
    
    int pixels = 0;
    int[] niveisLab = new int[3];
        
    for(int x=0; x<imagemMental.getWidth(); x++)
    {
      for(int y=0; y<imagemMental.getHeight(); y++)
      {
        Color cor = null;
        
        for(int i = 0; i < niveisLab.length; i++)
        {
        	niveisLab[i] = 0;
        }
        
        for(int i = 0; i < nivelLuminancia - 1; i++)
        {
        	if(entrada.get(pixels*(nivelLuminancia + nivelA + nivelB - 3) + i))
        	{
        		niveisLab[0]++;
        	}
        }
        
        for(int i = 0; i < nivelA - 1; i++)
        {
        	if(entrada.get(nivelLuminancia - 1 + pixels*(nivelLuminancia + nivelA + nivelB - 3) + i))
        	{
        		niveisLab[1]++;
        	}
        }
        
        for(int i = 0; i < nivelB - 1; i++)
        {
        	if(entrada.get(nivelLuminancia + nivelA - 2 + pixels*(nivelLuminancia + nivelA + nivelB - 3) + i))
        	{
        		niveisLab[2]++;
        	}
        }
        
        pixels++;
        
        double[] lab = new double[3];
        
        lab[0] = ((double)niveisLab[0] / (double)nivelLuminancia) * 100;
        lab[1] = (((double)niveisLab[1] / (double)nivelA) * 220) - 110;
        lab[2] = (((double)niveisLab[2] / (double)nivelB) * 220) - 110;
        
        /*for(int i = 0; i <niveisLab.length; i++)
        {
        	System.out.println(lab[i]);
        }*/
        
        //double[] lab = {luminancia, a, b};
        
        cor = ColorConverter.xyzParaRgb(ColorConverter.labParaXyz(lab));
        imagemMental.setRGB(x, y, cor.getRGB());
        //System.out.println(x + ";" + y + ";"+ cor);
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
      
      for(int j = listaBool.length() - 1; j > -1; j--){
        if (listaBool.charAt(j)=='1'){
          entrada[posicaoAtual + listaBool.length() - j - 1] = true;
        }
        else {
          entrada[posicaoAtual + listaBool.length() - j - 1] = false;          
        }
      }
      
      posicaoAtual += listaBool.length();
    }
    
    return Arrays.asList(entrada);
  }
  
  private int convertToInt(List<Boolean> bloco)
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

  public void setThresholdInferior(int thresholdInferior)
  {
    this.thresholdInferior = thresholdInferior;
  }

  public void setThresholdSuperior(int thresholdSuperior) 
  {
    this.thresholdSuperior = thresholdSuperior;
  }

  public int getThresholdInferior()
  {
    return thresholdInferior;
  }

  public int getThresholdSuperior()
  {
    return thresholdSuperior;
  }

  public void setThresholdBleaching(int thresholdBleaching)
  {
    this.thresholdBleaching = thresholdBleaching;
  }
}