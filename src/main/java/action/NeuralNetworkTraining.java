package action;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import model.ClusterDetector;
import model.ImageSetup;
import model.NeuralNetwork;
import model.NeuralNetworkWithDiscriminators;
import utils.Binarization;
import utils.ImageUtils;

public class NeuralNetworkTraining
{
  private static double confidence = 0.25;
  private static final int NIVEL_LUMINANCIA = 4;
  private static final int NIVEL_A = 8;
  private static final int NIVEL_B = 8;
  
  public static void trainOneDiscriminator(NeuralNetwork nn, String pathToImgsFolder)
  {
	  String folderOk = pathToImgsFolder + "ok/";
      File[] arquivos = new File(folderOk).listFiles();
      
      for(File arquivo:arquivos)
      {
          if(arquivo.getName().contains("Thumbs"))
          {
              continue;
          }
          
          List<Boolean> input = Binarization.binarizar(
        		  ImageUtils.resizeImage(
        				  ImageUtils.loadImage(folderOk + arquivo.getName()),
        				  ImageSetup.WIDTH, ImageSetup.HEIGHT));
          nn.train(input);
      }
  }
  
  public static void trainTwoDiscriminators(
		  NeuralNetworkWithDiscriminators nn, 
		  String pathToImgsFolder)
  {   
      String folderOk = pathToImgsFolder + "ok/";
      learningValues(nn, folderOk, 1);
      String folderNo = pathToImgsFolder + "no/";
      learningValues(nn, folderNo, 0);
  } 
  
  public static void learningValues(NeuralNetworkWithDiscriminators nn, String folder, int value)
  {
      File[] arquivos = new File(folder).listFiles();
      
      for(File arquivo : arquivos)
      {
          if(arquivo.getName().contains("Thumbs"))
          {
              continue;
          }

              List<Boolean> input = Binarization.binarizar(
            		  ImageUtils.resizeImage(folder+arquivo.getName(),
            				  ImageSetup.WIDTH, ImageSetup.HEIGHT));
              nn.train(input, value);
      }  
  }
  
  public static boolean classifyPreprocessing(
		  BufferedImage imagem, 
		  NeuralNetworkWithDiscriminators nn, 
		  ClusterDetector detector, 
		  String arquivo, 
		  String folder1) throws IOException
  {
      BufferedImage face = detector.detectFace(ImageUtils.resizeImage(imagem,ImageSetup.WIDTH, ImageSetup.HEIGHT));
  
      List<Boolean> input = Binarization.discretizar(
    		  (ImageUtils.resizeImage(
    				  face, ImageSetup.WIDTH_PREPROCESSING, ImageSetup.HEIGHT_PREPROCESSING)),
    		  NIVEL_LUMINANCIA,NIVEL_A,NIVEL_B);
      
      if (nn.classify(input, confidence, 0) == 0)
      {
    	System.out.println("Match!");
        return true;
      }
      else
      {
        System.out.println("Dismatch!");
        return false;
      }
  }
  
  public static int getMaxValue(List<Integer> lista)
  {
    return Collections.max(lista);
  }
  
  public static int getMinValue(List<Integer> lista)
  {
    return Collections.min(lista);
  }
}
