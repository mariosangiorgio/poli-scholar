package it.polimi.bidding;

import applications.analyzer.DocumentClassifier;
import applications.analyzer.DocumentClassifierType;

public class BayesianBidder extends Bidder{
	
	private static final long serialVersionUID = -926572315735858206L;
	private DocumentClassifier classifier;
	
	public void train(String pathToReviewers) {
		classifier = DocumentClassifier.getFromTrainingSet(DocumentClassifierType.NaiveBayesian,pathToReviewers);
	}
	
	@Override
	public String getReviewer(String documentContent) {
		return classifier.classify(documentContent);
	}
}
