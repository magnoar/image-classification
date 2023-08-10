ID Photo Classification
===================================

This is a pattern recognition tool that uses supervised learning, so it needs training data to work. 

The project is set to train and classify images that the aspect ratio is 3:4, 
it resizes the images to 120x160 because my training data were so.
This project is beeing used to verify if the images uploaded are suitable for use in ID (it has some additional img process functions to ID verification that are not used in the default execution because of bad performance),
but it can classify any trained pattern. 

Feel free to change to your needs.

This is an implementation of a weightless neural network model known as WiSARD, 
you can see how it works at http://www.teco.edu/~albrecht/neuro/html/node23.html

How to use
---------------

1. Create the following folder structure 
	
\parentFoder  
----\ train  
--------\ ok  
--------\ no  
    
2. Copy images that correspond to your pattern into the ok folder; (50~200 imgs)
3. Copy images that DO NOT correspond to your pattern into the fail folder (50~200 imgs)
	obs.: this is done to correct any errors (just load false images that were tested as positive here and train again)
4. As i said before, change the class ImageSetup to fit the desired aspect ratio.
5. Create a runnable jar.
6. Create and train your neural network by running:
	java -cp IDPhotoVerification.jar action.Builder "/absolut/Path/To/ParentFolder"

7. Now you can classify any image, based on previous training, by running:
	java -cp IDPhotoVerification.jar action.Classifier "/absolut/Path/To/Image.jpg"
