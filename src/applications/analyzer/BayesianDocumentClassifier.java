package applications.analyzer;

import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;

public class BayesianDocumentClassifier extends DocumentClassifier{
	
	protected ClassifierTrainer<?> getTrainer() {
		return new NaiveBayesTrainer();
	}
	
}
