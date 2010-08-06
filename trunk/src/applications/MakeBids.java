package applications;

import it.polimi.bidding.Bidder;
import it.polimi.data.io.NotADirectoryException;

public class MakeBids {
	public static void main(String[] args) throws NotADirectoryException {
		// Application settings
		boolean train = false;
		String classifierFile = "classifier";
		String submissionAbstractPaths = "papers/abstracts/submissions";
		String reviewersAbstractsPaths = "papers/abstracts/reviewers";
		float topicCoverage = .75f;
		int numberOfPapersToPick = 20;

		Bidder bidder = new Bidder();

		bidder.loadClassifier(train, classifierFile);

		bidder.groupSubmissions(submissionAbstractPaths);
		// TODO: output submission groups

		bidder.generateAuthorProfiles(reviewersAbstractsPaths);
		// TODO: output reviewers' profiles

		bidder.generateBids(topicCoverage, numberOfPapersToPick);
		// TODO: suggest submissions according to the profile
	}
}
