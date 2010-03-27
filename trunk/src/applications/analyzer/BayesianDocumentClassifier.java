package applications.analyzer;

import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.Trial;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.FeatureSequence2FeatureVector;
import cc.mallet.pipe.Input2CharSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.Target2Label;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceLowercase;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

public class BayesianDocumentClassifier {
	private Classifier classifier;
	
	private BayesianDocumentClassifier(){
	}
	
	public static BayesianDocumentClassifier getFromTrainingSet(){
		BayesianDocumentClassifier classifier = new BayesianDocumentClassifier();
		classifier.train();
		return classifier;
	}
	
	public static BayesianDocumentClassifier load(String classifierFile){
		BayesianDocumentClassifier classifier = new BayesianDocumentClassifier();
		try {
			FileInputStream inputStream = new FileInputStream(classifierFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
			classifier.classifier = (Classifier) objectInputStream.readObject();
			objectInputStream.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
	
	public String classify(String input){
		Classification classification = classifier.classify(input);
		return classification.getLabeling().getBestLabel().toString();
	}
	
	
	private void train(){
		Pipe instancePipe = new SerialPipes (new Pipe[] {
				new Target2Label (),				// Target String -> class label
				new Input2CharSequence (),			// Data File -> String containing contents
				new CharSequence2TokenSequence (),	// Data String -> TokenSequence
				new TokenSequenceLowercase (),		// TokenSequence words lower-cased
				new TokenSequenceRemoveStopwords (),// Remove stop-words from sequence
				new Stemmer(),
				new TokenSequence2FeatureSequence(),// Replace each Token with a feature index
				new FeatureSequence2FeatureVector() // Collapse word order into a "feature vector"
			});
		InstanceList instancelist = new InstanceList (instancePipe);
		
		// Reading the documents and labeling them with their directory name
		String basePath = "resources/trainingSet";
		File trainingSetRoot = new File(basePath);
		Collection<String> labels = new Vector<String>();
		for(String labelName:trainingSetRoot.list()){
			File label = new File(basePath+"/"+labelName);
			if(label.isDirectory() && !labelName.startsWith(".")){
				labels.add(labelName);
				for(String documentName:label.list()){
					File document = new File(basePath+"/"+labelName+"/"+documentName);
					if(!documentName.startsWith(".") && document.isFile()){
						try {
							String fullText = TextStripper.getFullText(document);
							instancelist.addThruPipe(new Instance(fullText, labelName, documentName, null));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		NaiveBayesTrainer naiveBayesTrainer = new NaiveBayesTrainer();

		// Splitting the instance list to have a training set and a validation set
		double[] proportions = {0.75,0.25};
		InstanceList[] instanceSets  = instancelist.split(new Random(), proportions);

		naiveBayesTrainer.setValidationInstances(instanceSets[1]);
		classifier = naiveBayesTrainer.train(instanceSets[0]);

		System.out.println(instancelist.targetLabelDistribution());
		System.out.println(instanceSets[0].targetLabelDistribution());
		System.out.println(instanceSets[1].targetLabelDistribution());
		
		Trial trial = new Trial(classifier,instanceSets[1]);
		System.out.println("Testing summary:");
		System.out.println("Accuracy:\t"+trial.getAccuracy());
		System.out.println("Average rank:\t"+trial.getAverageRank());
		for(String label : labels){
			System.out.println(label+"\tPrecision: "+trial.getPrecision(label)+"\tRecall: "+trial.getRecall(label)+"\tFscore: "+trial.getF1(label));
		}
	}

	public void save(String classifierFile) {
		try {
			FileOutputStream serializedFile = new FileOutputStream(classifierFile);
			ObjectOutputStream outputStream;
			outputStream = new ObjectOutputStream(serializedFile);
			outputStream.writeObject(classifier);
			outputStream.close();
			serializedFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
