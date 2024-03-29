package it.polimi.analyzer;

import it.polimi.utils.TextStripper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
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

public abstract class DocumentClassifier implements Serializable {
	private static final long serialVersionUID = 8990469542892217623L;

	private Classifier classifier;
	private Collection<String> labels;

	private String pathToTrainingSet;

	protected abstract ClassifierTrainer<?> getTrainer();

	protected DocumentClassifier(String pathToTrainingSet) {
		this.pathToTrainingSet = pathToTrainingSet;
	}

	public String classify(String input) {
		Classification classification = classifier.classify(input);
		return classification.getLabeling().getBestLabel().toString();
	}

	private void train() {
		InstanceList instances = loadTrainingInstances();
		ClassifierTrainer<?> trainer = getTrainer();

		// Splitting the instance list to have a training set and a validation
		// set
		double[] proportions = { 0.85, 0.15 };
		InstanceList[] instanceSets = instances
				.split(new Random(), proportions);

		trainer.setValidationInstances(instanceSets[1]);
		classifier = trainer.train(instanceSets[0]);

		System.out.println(" Dataset");
		System.out.println(instances.targetLabelDistribution());
		System.out.println(" Documents used for training");
		System.out.println(instanceSets[0].targetLabelDistribution());
		System.out.println(" Documents used for testing");
		System.out.println(instanceSets[1].targetLabelDistribution());

		Trial trial = new Trial(classifier, instanceSets[1]);
		System.out.println(" Test statistics:");
		System.out.println("Accuracy:\t" + trial.getAccuracy());
		System.out.println("Average rank:\t" + trial.getAverageRank());
		System.out.println();
		for (String label : labels) {
			System.out.println(label + "\tPrecision: "
					+ trial.getPrecision(label) + "\tRecall: "
					+ trial.getRecall(label) + "\tFscore: "
					+ trial.getF1(label));
		}
	}

	private String[] loadStopWords(String resourceName) {
		try {
		InputStream in = getClass().getResourceAsStream(resourceName);
		Reader resourceReader = new InputStreamReader(in);
		BufferedReader bufferedReader = new BufferedReader(resourceReader);
		String stopWord;
		Vector<String> stoplist = new Vector<String>();

			while ((stopWord = bufferedReader.readLine()) != null) {
				stoplist.add(stopWord);
			}
		bufferedReader.close();
		resourceReader.close();
		in.close();

		String words[] = new String[stoplist.size()];
		stoplist.toArray(words);
		return words;
		} catch (IOException e) {
			return new String[0];
		}
	}

	private InstanceList loadTrainingInstances() {
		TextStripper textStripper = new TextStripper();
		// Getting the stopword resource
		String[] stoplist = loadStopWords("/resources/computer science.stoplist");

		Pipe instancePipe = new SerialPipes(new Pipe[] { new Target2Label(), // Target
				// String -> class label
				new Input2CharSequence(), // Data File -> String containing
				// contents
				new CharSequence2TokenSequence(), // Data String ->
				// TokenSequence
				new TokenSequenceLowercase(), // TokenSequence words lower-cased
				(new TokenSequenceRemoveStopwords()).addStopWords(stoplist),// Remove
				// stop-words
				// from
				// sequence
				new Stemmer(), new TokenSequence2FeatureSequence(),
				// Replace each Token with a feature index
				new FeatureSequence2FeatureVector() // Collapse word order into
				// a "feature vector"
				});
		InstanceList instancelist = new InstanceList(instancePipe);

		// Reading the documents and labeling them with their directory name
		File trainingSetRoot = new File(pathToTrainingSet);
		labels = new Vector<String>();
		for (String labelName : trainingSetRoot.list()) {
			File label = new File(pathToTrainingSet + "/" + labelName);
			if (label.isDirectory() && !labelName.startsWith(".")) {
				labels.add(labelName);
				for (String documentName : label.list()) {
					File document = new File(pathToTrainingSet + "/"
							+ labelName + "/" + documentName);
					if (!documentName.startsWith(".") && document.isFile()) {
						String content = null;
						if (documentName.endsWith(".pdf")) {
							try {
								content = textStripper.getContent(document);
								content = textStripper.cleanContent(content);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						if (documentName.endsWith(".txt")) {
							try {
								FileReader fileReader = new FileReader(document);
								BufferedReader reader = new BufferedReader(
										fileReader);
								StringBuffer buffer = new StringBuffer();
								String temp;
								while ((temp = reader.readLine()) != null) {
									buffer.append(temp);
									buffer.append(" ");
								}
								content = buffer.toString();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						instancelist.addThruPipe(new Instance(content,
								labelName, documentName, null));
					}
				}
			}
		}
		return instancelist;
	}

	public static DocumentClassifier getFromTrainingSet(
			DocumentClassifierType type, String pathToTrainingSet) {
		DocumentClassifier classifier = null;
		switch (type) {
		case NaiveBayesian:
			classifier = new BayesianDocumentClassifier(pathToTrainingSet);
			break;
		}
		classifier.train();
		return classifier;
	}

	public void save(String classifierFile) {
		try {
			FileOutputStream serializedFile = new FileOutputStream(
					classifierFile);
			ObjectOutputStream outputStream;
			outputStream = new ObjectOutputStream(serializedFile);
			outputStream.writeObject(this);
			outputStream.close();
			serializedFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static DocumentClassifier load(String classifierFile) {
		DocumentClassifier classifier = null;
		try {
			FileInputStream inputStream = new FileInputStream(classifierFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(
					inputStream);
			classifier = (DocumentClassifier) objectInputStream.readObject();
			objectInputStream.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classifier;
	}
}
