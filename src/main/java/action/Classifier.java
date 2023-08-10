package action;

import java.io.IOException;
import java.util.List;

import model.ImageSetup;
import model.NeuralNetworkWithDiscriminators;
import utils.Binarization;
import utils.ImageUtils;

public class Classifier {

    /**
     * returns 0 if the image belongs to the pattern
     *  
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException
    {
    	String pathToImg = args[0];
    	String pathToNN;
    	
    	if(args.length > 1)
    		pathToNN = args[1];
    	else
    		pathToNN = System.getProperty("user.dir") + "/twoDiscriminatorsNN.serial";
    	
    	List<Boolean> input = null;
    	NeuralNetworkWithDiscriminators nn = null;
    	
    	try 
    	{
			input = Binarization.binarizar(ImageUtils.resizeImage(pathToImg, ImageSetup.WIDTH, ImageSetup.HEIGHT));
			nn = new NeuralNetworkWithDiscriminators(pathToNN);
		} 
    	catch (Exception e) {
			System.out.println("Missing args: parent training folder or neural network.");
		}
        
        if (nn.classify(input, 0.2 ,0) == 1)
        	System.out.println("yes");
        else
        	System.out.println("no");
    }
    
}
