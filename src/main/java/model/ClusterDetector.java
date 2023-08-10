package model;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.List;

import utils.Binarization;
import utils.ImageUtils;

public class ClusterDetector implements Serializable
{ 
  private static final long serialVersionUID = 1L;

  private int[][] ramsOlhoDireito;
  private int[][] ramsOlhoEsquerdo;
  private int[][] ramsBoca;
  private NeuralNetwork redeOlho;
  private NeuralNetwork redeBoca;
  private boolean discretization;
  private int luminanceLevel = 4;
  private int levelA = 8;
  private int levelB = 8;
  private int xOlhoDireito;
  private int yOlhoDireito;
  private int xOlhoEsquerdo;
  private int yOlhoEsquerdo;
  private int yBoca;
  
  private final int LARGURA_JANELA_OLHO  = 25;
  private final int ALTURA_JANELA_OLHO   = 15;
  private final int LARGURA_JANELA_BOCA  = 53;
  private final int ALTURA_JANELA_BOCA   = 24;
  private final int ALTURA_MINIMA_OLHO   = 40;
  private final int ALTURA_MAXIMA_OLHO   = 80;
  private final int ALTURA_MINIMA_BOCA   = 80;
  private final int ALTURA_MAXIMA_BOCA   = 120;
  
  public ClusterDetector(int numeroEntradasRAMs, 
		  boolean discretizacao, int luminanceLevel, int levelA, int levelB)
  {
	this.discretization = discretizacao;
	this.luminanceLevel = luminanceLevel;
	this.levelA = levelA;
	this.levelB = levelB;

	if(discretizacao)
	{
		redeOlho  = new NeuralNetwork(0, numeroEntradasRAMs, 
				LARGURA_JANELA_OLHO * ALTURA_JANELA_OLHO * 
				(luminanceLevel + levelA + levelB - 3) / numeroEntradasRAMs);
	    redeBoca  = new NeuralNetwork(0, numeroEntradasRAMs, 
	    		LARGURA_JANELA_BOCA * ALTURA_JANELA_BOCA * 
	    		(luminanceLevel + levelA + levelB - 3) / numeroEntradasRAMs);

	}
	else
	{
		redeOlho  = new NeuralNetwork(0, numeroEntradasRAMs, 
				LARGURA_JANELA_OLHO  * ALTURA_JANELA_OLHO  / numeroEntradasRAMs);
		redeBoca  = new NeuralNetwork(0, numeroEntradasRAMs, 
				LARGURA_JANELA_BOCA  * ALTURA_JANELA_BOCA  / numeroEntradasRAMs);

	}
	
  }

  public BufferedImage detectFace(BufferedImage imagem)
  {
    int menorY;
    int largura;
    int altura;
    
    detectClusters(imagem);
      
    menorY = yOlhoDireito > yOlhoEsquerdo ? yOlhoEsquerdo : yOlhoDireito;
    largura = xOlhoEsquerdo - xOlhoDireito;
    altura = yBoca - menorY;
   
    BufferedImage face = ImageUtils.cropImage(imagem, largura, altura, xOlhoDireito, yOlhoDireito);
     
    return face;
  }
  
  public void detectClusters(BufferedImage imagem)
  {
    this.ramsOlhoDireito = 
    		new int[imagem.getWidth()/2 - LARGURA_JANELA_OLHO] 
    				[ALTURA_MAXIMA_OLHO - ALTURA_MINIMA_OLHO - ALTURA_JANELA_OLHO];
    this.ramsOlhoEsquerdo = 
    		new int[imagem.getWidth()/2 - LARGURA_JANELA_OLHO] 
    				[ALTURA_MAXIMA_OLHO - ALTURA_MINIMA_OLHO - ALTURA_JANELA_OLHO];
    this.ramsBoca = 
    		new int[imagem.getWidth() - LARGURA_JANELA_BOCA] 
    				[ALTURA_MAXIMA_BOCA - ALTURA_MINIMA_BOCA - ALTURA_JANELA_BOCA];
        
    detectCluster(imagem, this.redeOlho, "olhoDireito");
    
    detectCluster(imagem, this.redeOlho, "olhoEsquerdo");
    
    detectCluster(imagem, this.redeBoca, "boca");
  }
  
  public BufferedImage detectCluster(BufferedImage imagem, NeuralNetwork rede, String identificacaoCluster)
  {
    BufferedImage janela;
    List<Boolean> entrada;
    int[][] rams;
    int larguraJanela;
    int alturaJanela;
    int x0;
    int y0;
    
    if(identificacaoCluster.contains("olho"))
    {
      rams = identificacaoCluster.contains("Direito") ? ramsOlhoDireito : ramsOlhoEsquerdo;
      x0 = identificacaoCluster.contains("Esquerdo") ? imagem.getWidth() /2 : 0;
      larguraJanela = LARGURA_JANELA_OLHO;
      alturaJanela = ALTURA_JANELA_OLHO;
      y0 = ALTURA_MINIMA_OLHO;
    }
    else
    {
      rams = ramsBoca;
      larguraJanela = LARGURA_JANELA_BOCA;
	  alturaJanela = ALTURA_JANELA_BOCA;
	  x0 = 0;
	  y0 = ALTURA_MINIMA_BOCA;
    }
    //TODO, better performance
    
    for(int i = 0; i < rams.length; i = i + 2)
    {      
      for(int j = 0; j < rams[i].length; j = j + 2)
      {
        janela = ImageUtils.cropImage(imagem, larguraJanela, alturaJanela, i + x0, j + y0);
        
        if(discretization)
        {
          entrada = Binarization.discretizar(janela, luminanceLevel, levelA, levelB);
        }
        else
        {
          entrada = Binarization.binarizar(janela);
        }
       
        rams[i][j] = rede.classify(entrada);
      }
    }
    
    int[] cluster = detectBestCluster(rams);
    
    if(identificacaoCluster == "olhoDireito")
    {
      xOlhoDireito = cluster[0] + x0;
      yOlhoDireito = cluster[1] + y0;
    }
    else
    {
      if(identificacaoCluster == "olhoEsquerdo")
      {
        xOlhoEsquerdo = cluster[0] + larguraJanela + x0;
    	yOlhoEsquerdo = cluster[1] + y0;
      }
      else
      {
    	yBoca = cluster[1] + alturaJanela + y0;
      }
    }
    
    return ImageUtils.cropImage(imagem, larguraJanela, alturaJanela, cluster[0] + x0, cluster[1] + y0);
  }
  
  /**
   * Detect cluster with more active neurons.
   * 
   * @param rams
   * @return
   */
  public int[] detectBestCluster(int[][] rams)
  {
    int melhorResultado = 0;
    int x = 0;
    int y = 0;
    
    //TODO better performance
    for(int i = 0; i < rams.length; i++)
    {
      for(int j = 0; j < rams[i].length; j++)
      {
        if(rams[i][j] > melhorResultado)
        {
          melhorResultado = rams[i][j];
          x = i;
          y = j;
        }
      }
    }
    int[] cluster = {x, y};
    
    return cluster;
  }
  
  public void trainEyeAndMouthNNs(BufferedImage imagemMentalOlho, BufferedImage imagemMentalBoca)
  {
    List<Boolean> entrada;
    
    entrada = Binarization.discretizar(imagemMentalOlho, luminanceLevel, levelA, levelB);
    redeOlho.train(entrada);
    
    entrada = Binarization.discretizar(imagemMentalBoca, luminanceLevel, levelA, levelB);
    redeBoca.train(entrada);
  }
  
  public NeuralNetwork getEyeNN()
  {
    return redeOlho;
  }

  public void setEyeNN(NeuralNetwork redeOlho)
  {
    this.redeOlho = redeOlho;
  }

  public NeuralNetwork getMouthNN()
  {
    return redeBoca;
  }

  public void setMouthNN(NeuralNetwork redeBoca)
  {
    this.redeBoca = redeBoca;
  }

  public boolean isDiscretizacao()
  {
    return discretization;
  }

  public void setDiscretizacao(boolean discretizacao)
  {
    this.discretization = discretizacao;
  }
  public int getLuminanceLevel()
  {
    return luminanceLevel;
  }

  public void setLuminanceLevel(int luminanceLevel)
  {
    this.luminanceLevel = luminanceLevel;
  }

  public int getLevelA()
  {
    return levelA;
  }

  public void setLevelA(int levelA)
  {
    this.levelA = levelA;
  }

  public int getLevelB()
  {
    return levelB;
  }

  public void setLevelB(int levelB)
  {
    this.levelB = levelB;
  }


}
