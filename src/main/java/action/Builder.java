package action;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import model.ImageSetup;
import model.NeuralNetwork;
import model.NeuralNetworkWithDiscriminators;


public class Builder
{
    private static int       bleaching                = 4;
    private static int       inputRAMs2Discriminators = 4;
    private static int       inputRAMs1Discriminator  = 10;
    private static int       inputRAMsPreprocessing   = 1;
    
    private static String pathToTrain = "train/";
    
    /**
     * Create NN and save to disk //TODO: path to destination
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
    	String pathToParent = args[0];
    	if(pathToParent == null || pathToParent.trim().isEmpty())
    	{
    		System.out.println("Missing arg: parent training folder.");
    		return;
    	}
    	Builder.createNNs(args[0]);
    }
    
    public static void createNNs(String pathToParent) throws IOException
    {
    	//best hit rate
    	createNNWithTwoDiscriminators(pathToParent);
    	//createNNWithOneDiscriminator(pathToParent);
    	//createNNWithPreprocessing(pathToParent);
    }
    
    public static void createNNWithPreprocessing(String pathToParent) throws IOException
    {
      NeuralNetworkWithDiscriminators nn = 
    		  new NeuralNetworkWithDiscriminators(
    				  inputRAMsPreprocessing, 
    				  ImageSetup.WIDTH_PREPROCESSING * ImageSetup.HEIGHT_PREPROCESSING, 
    				  2);
            
      NeuralNetworkTraining.trainTwoDiscriminators(nn, 
    		  pathToParent + pathToTrain);
      saveToDisk(nn, pathToParent + "preprocessing.serial");
    }
  
    public static void createNNWithTwoDiscriminators(String pathToParent) throws IOException
    {
      NeuralNetworkWithDiscriminators nn = 
    		  new NeuralNetworkWithDiscriminators(inputRAMs2Discriminators,ImageSetup.WIDTH * ImageSetup.HEIGHT, 2);
            
      NeuralNetworkTraining.trainTwoDiscriminators(nn, pathToParent + pathToTrain);
      saveToDisk(nn, "/twoDiscriminatorsNN.serial");
    }
    
    public static void createNNWithOneDiscriminator(String pathToParent) throws IOException
    {
      NeuralNetwork nn = new NeuralNetwork(bleaching, inputRAMs1Discriminator, ImageSetup.WIDTH * ImageSetup.HEIGHT);
      
      NeuralNetworkTraining.trainOneDiscriminator(nn, pathToParent + pathToTrain);
      saveToDisk(nn, "/oneDiscriminatorNN.serial");
    }

    private static void saveToDisk(Object rede, String destino) throws IOException
    {
      String path = System.getProperty("user.dir") + destino;
      FileOutputStream f_out = null;
      ObjectOutputStream oos = null;
      try
      {  
        f_out = new FileOutputStream(path);
  
        oos = new ObjectOutputStream(f_out);
        oos.writeObject(rede);
        System.out.println("Neural Network created at: " + path);
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();
      }
      finally {
		oos.close();
		f_out.close();
      }
  
    }
}
