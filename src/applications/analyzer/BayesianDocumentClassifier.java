package applications.analyzer;

import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.NaiveBayesTrainer;

public class BayesianDocumentClassifier extends DocumentClassifier{
	private static final long serialVersionUID = 8216489259978076346L;

	protected ClassifierTrainer<?> getTrainer() {
		return new NaiveBayesTrainer();
	}
	
}
