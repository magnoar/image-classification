package model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import utils.Binarization;

public class NeuralNetworkWithPreprocessing
{
  private static final int NIVEL_LUMINANCIA = 4;
  private static final int NIVEL_A = 8;
  private static final int NIVEL_B = 8;
  private static final int BLEACHING = 0;
  
  private NeuralNetworkWithDiscriminators binarizada;
  private NeuralNetworkWithDiscriminators discretizada;
  private ClusterDetector detector;
  private double confianca;
  private double confiancaPreProcessamento;
  
  public NeuralNetworkWithPreprocessing(int entradasRAMs, double confianca, double confiancaPreProcessamento)
  {
    binarizada = new NeuralNetworkWithDiscriminators(entradasRAMs, ImageSetup.WIDTH * ImageSetup.HEIGHT, 2);
    discretizada = new NeuralNetworkWithDiscriminators(entradasRAMs, ImageSetup.WIDTH_FACE * ImageSetup.HEIGHT_FACE, 2);
    detector = new ClusterDetector(entradasRAMs, true, NIVEL_LUMINANCIA, NIVEL_A, NIVEL_B);
    this.confianca = confianca;
    this.confiancaPreProcessamento = confiancaPreProcessamento;
  }
  
  public NeuralNetworkWithPreprocessing(int entradasRAMsBinarizada, int entradasRAMsDiscretizada, double confianca, double confiancaPreProcessamento)
  {
    binarizada = new NeuralNetworkWithDiscriminators(entradasRAMsBinarizada, ImageSetup.WIDTH * ImageSetup.HEIGHT, 2);
    discretizada = new NeuralNetworkWithDiscriminators(entradasRAMsDiscretizada, ImageSetup.WIDTH_FACE * ImageSetup.HEIGHT_FACE, 2);
    detector = new ClusterDetector(entradasRAMsDiscretizada, true, NIVEL_LUMINANCIA, NIVEL_A, NIVEL_B);
    this.confianca = confianca;
    this.confiancaPreProcessamento = confiancaPreProcessamento;
  }
  
  public NeuralNetworkWithDiscriminators getBinarizada()
  {
    return binarizada;
  }

  public NeuralNetworkWithDiscriminators getDiscretizada()
  {
    return discretizada;
  }

  public void train(BufferedImage imagemMentalBinarizadaPadrao,   BufferedImage imagemMentalBinarizadaForaPadrao,
                     BufferedImage imagemMentalDiscretizadaPadrao, BufferedImage imagemMentalDiscretizadaForaPadrao,
                     BufferedImage imagemMentalOlho, BufferedImage imagemMentalBoca)
  {
    List<Boolean> entrada;
    
    entrada = Binarization.binarizar(imagemMentalBinarizadaPadrao);
    binarizada.train(entrada, 0);
   
    entrada = Binarization.binarizar(imagemMentalBinarizadaForaPadrao);
    binarizada.train(entrada, 1);
    
    detector.trainEyeAndMouthNNs(imagemMentalOlho, imagemMentalBoca);
    
    entrada = Binarization.discretizar(imagemMentalDiscretizadaPadrao, NIVEL_LUMINANCIA, NIVEL_A, NIVEL_B);
    discretizada.train(entrada, 0);
    
    entrada = Binarization.discretizar(imagemMentalDiscretizadaForaPadrao, NIVEL_LUMINANCIA, NIVEL_A, NIVEL_B);
    discretizada.train(entrada, 1);
  }
  
  public int classify(BufferedImage imagem) throws IOException
  {
    BufferedImage face;
    List<Boolean> entrada;
    int classificacao;
    
    entrada = Binarization.binarizar(imagem);
    classificacao = binarizada.classify(entrada, confiancaPreProcessamento, BLEACHING);
    
    if(classificacao == 1)
    {
      face = detector.detectFace(imagem);
      entrada = Binarization.discretizar(face, NIVEL_LUMINANCIA, NIVEL_A, NIVEL_B);
      classificacao = discretizada.classify(entrada, confianca, 0);
    }
    
    return classificacao;
  }
}
