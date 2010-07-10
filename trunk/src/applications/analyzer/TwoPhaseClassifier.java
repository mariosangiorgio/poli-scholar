package applications.analyzer;

import java.io.File;

public class TwoPhaseClassifier {
	
	public static void main(String[] args) {
		//Getting the classifier from the clustered data-set
		DocumentClassifier classifier;
		String classifierFile = "twoPhaseClassifier";
		boolean train = false;
		File file = new File(classifierFile);

		if (train || !file.exists()) {
			classifier = BayesianDocumentClassifier.getFromTrainingSet(
					DocumentClassifierType.NaiveBayesian, "plaintext/model");
			classifier.save(classifierFile);
		} else {
			classifier = BayesianDocumentClassifier.load(classifierFile);
		}
		//Classification of each submission
		//Classification of each reviewer
	}
}
