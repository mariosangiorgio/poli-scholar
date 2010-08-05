package it.polimi.bidding;

import it.polimi.analyzer.DocumentClassifier;
import it.polimi.analyzer.DocumentClassifierType;
import it.polimi.data.io.NotADirectoryException;
import it.polimi.data.io.PaperLoader;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

public class MakeBids {
	private DocumentClassifier classifier;
	private PaperCollection sumbittedPapers = new PaperCollection();
	private Collection<AuthorProfile> authorProfiles = new Vector<AuthorProfile>();

	public static void main(String[] args) throws NotADirectoryException {
		// Application settings
		boolean train = false;
		String classifierFile = "classifier";
		String submissionAbstractPaths = "papers/abstracts/submissions";
		String reviewersAbstractsPaths = "papers/abstracts/reviewers";

		MakeBids bidder = new MakeBids();

		bidder.loadClassifier(train, classifierFile);
		
		bidder.groupSubmissions(submissionAbstractPaths);
		// TODO: output submission groups
		
		bidder.generateAuthorProfiles(reviewersAbstractsPaths);
		// TODO: output reviewers' profiles
		
		bidder.generateBids();
		// TODO: suggest submissions according to the profile
	}

	private void generateBids() {
		// TODO Auto-generated method stub
		
	}

	private void loadClassifier(boolean train, String classifierFile) {
		classifierFile = "classifier";
		File file = new File(classifierFile);

		if (train || !file.exists()) {
			classifier = DocumentClassifier.getFromTrainingSet(
					DocumentClassifierType.NaiveBayesian,
					"papers/abstracts/model");
			classifier.save(classifierFile);
		} else {
			classifier = DocumentClassifier.load(classifierFile);
		}
	}

	private void groupSubmissions(String submissionAbstractPaths)
			throws NotADirectoryException {
		File submissionsDirectory = new File(submissionAbstractPaths);
		for (Paper paper : PaperLoader.getFilesContent(submissionsDirectory)) {
			String category = classifier.classify(paper.getContent());
			sumbittedPapers.addSubmission(category, paper);
		}
	}

	private void generateAuthorProfiles(String reviewersAbstractsPaths)
			throws NotADirectoryException {
		File reviewerDirectory = new File(reviewersAbstractsPaths);
		for (File authorDirectory : PaperLoader
				.getSubDirectories(reviewerDirectory)) {
			AuthorProfile profile = new AuthorProfile(authorDirectory.getName());
			for (Paper paper : PaperLoader.getFilesContent(authorDirectory)) {
				String category = classifier.classify(paper.getContent());
				sumbittedPapers.addSubmission(category, paper);
			}
			authorProfiles.add(profile);
		}
	}
}
