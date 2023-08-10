package action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import model.ImageSetup;
import model.NeuralNetworkWithDiscriminators;
import utils.Binarization;
import utils.ImageUtils;


public class ClassifierTest {

	String pathToTest = "/home/mgo/projetos/idphoto/";
	String pathToNN = pathToTest + "twoDiscriminatorsNN.serial";
	String[] okImg = {pathToTest + "1.jpg", pathToNN};
	String[] failImg = {pathToTest + "0.jpg", pathToNN};
	
	NeuralNetworkWithDiscriminators nn;
	List<Boolean> input;
	@Test
	public void testOkImage() throws IOException {
    	
		input = Binarization.binarizar(ImageUtils.resizeImage(okImg[0], ImageSetup.WIDTH, ImageSetup.HEIGHT));
		nn = new NeuralNetworkWithDiscriminators(pathToNN);
		Assert.assertEquals(nn.classify(input, 0.2 ,0), 1);

	}
	
	@Test
	public void testFailImage() throws IOException {

		input = Binarization.binarizar(ImageUtils.resizeImage(failImg[0], ImageSetup.WIDTH, ImageSetup.HEIGHT));
		nn = new NeuralNetworkWithDiscriminators(pathToNN);
		Assert.assertEquals(nn.classify(input, 0.2 ,0), 0);
	}
	
	@Test
	public void testMain() throws IOException {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		Classifier.main(okImg);
		Assert.assertEquals(outContent.toString(), "yes\n");
		System.setOut(null);
	}
}
