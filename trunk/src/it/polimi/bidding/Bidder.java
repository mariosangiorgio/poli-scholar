package it.polimi.bidding;

import it.polimi.analyzer.DocumentClassifier;
import it.polimi.analyzer.DocumentClassifierType;
import it.polimi.data.io.NotADirectoryException;
import it.polimi.data.io.PaperLoader;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

public class Bidder {
	private DocumentClassifier classifier;
	private PaperCollection sumbittedPapers = new PaperCollection();
	private Collection<AuthorPapers> authorProfiles = new Vector<AuthorPapers>();
	private Collection<AuthorPapers> suggestions = new Vector<AuthorPapers>();

	public void generateBids(float topicCoverage, int numberOfPapersToPick) {
		//TODO: reiterate adding a category if the system is not able to find 20 papers
		for (AuthorPapers authorPapers : authorProfiles) {
			Collection<String> selectedTopics = authorPapers
					.getMostRelevantTopics(topicCoverage);

			int totalSelectedPapers = 0;
			for (String topic : selectedTopics) {
				totalSelectedPapers += authorPapers.getNumberOfPapers(topic);
			}

			AuthorPapers suggestion = new AuthorPapers(authorPapers
					.getAuthorName());
			for (String topic : selectedTopics) {
				int paperOfTheTopic = authorPapers.getNumberOfPapers(topic);
				int papersToPick = numberOfPapersToPick * paperOfTheTopic
						/ totalSelectedPapers;

				Collection<Paper> selectedPapers = authorPapers.pickTopPapers(
						topic, sumbittedPapers.getPapers(topic), papersToPick);
				suggestion.addSubmissions(topic, selectedPapers);
			}
			suggestions.add(suggestion);
		}
	}

	public void loadClassifier(boolean train, String classifierFile) {
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

	public void groupSubmissions(String submissionAbstractPaths)
			throws NotADirectoryException {
		File submissionsDirectory = new File(submissionAbstractPaths);
		for (Paper paper : PaperLoader.getFilesContent(submissionsDirectory)) {
			String category = classifier.classify(paper.getContent());
			sumbittedPapers.addSubmission(category, paper);
		}
	}

	public void generateAuthorProfiles(String reviewersAbstractsPaths)
			throws NotADirectoryException {
		File reviewerDirectory = new File(reviewersAbstractsPaths);
		for (File authorDirectory : PaperLoader
				.getSubDirectories(reviewerDirectory)) {
			AuthorPapers profile = new AuthorPapers(authorDirectory.getName());
			for (Paper paper : PaperLoader.getFilesContent(authorDirectory)) {
				String category = classifier.classify(paper.getContent());
				profile.addSubmission(category, paper);
			}
			authorProfiles.add(profile);
		}
	}
}
