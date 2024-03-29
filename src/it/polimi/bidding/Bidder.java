package it.polimi.bidding;

import it.polimi.analyzer.DocumentClassifier;
import it.polimi.analyzer.DocumentClassifierType;
import it.polimi.data.io.NotADirectoryException;
import it.polimi.data.io.PaperLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

public class Bidder {
	private DocumentClassifier classifier;
	private PaperCollection submittedPapers = new PaperCollection();
	private Collection<AuthorPapers> authorProfiles = new Vector<AuthorPapers>();
	private Collection<AuthorPapers> suggestions = new Vector<AuthorPapers>();

	public void generateBids(float topicCoverage, int numberOfPapersToPick) {
		// TODO: re-iterate adding a category if the system is not able to find
		// 20 papers
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
				int papersToPick = 0;
				if (totalSelectedPapers != 0) {
					papersToPick = numberOfPapersToPick * paperOfTheTopic
							/ totalSelectedPapers;
				}

				if (papersToPick == 0) {
					// Artificially increasing this number in order to have the
					// topic shown up in the results
					papersToPick = 3;
				}
				Collection<Paper> selectedPapers = authorPapers.pickTopPapers(
						topic, submittedPapers.getPapers(topic), papersToPick);
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
			submittedPapers.addSubmission(category, paper);
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

	public void loadAuthorTopics(String profilesDirectory,
			String reviewersAbstractsPaths) throws NotADirectoryException {
		generateAuthorProfiles(reviewersAbstractsPaths);
		// the profiles specified in the file
		File profiles = new File(profilesDirectory);
		File papers = new File(reviewersAbstractsPaths);
		if (profiles.isDirectory() && papers.isDirectory()) {
			for (File authorFile : profiles.listFiles()) {
				try {
					if (!authorFile.isFile() || authorFile.isHidden()) {
						continue;
					}
					// Loading file content
					System.out.println("Loading: " + authorFile.getName());
					FileReader authorProfileFile = new FileReader(authorFile);
					BufferedReader fileReader = new BufferedReader(
							authorProfileFile);

					String authorName = fileReader.readLine();
					authorName = authorName.substring(4,
							authorName.length() - 4);
					
					AuthorPapers authorProfile = null;
					for(AuthorPapers author : authorProfiles){
						if(author.getAuthorName().equals(authorName)){
							authorProfile = author;
							break;
						}
					}
					
					String temp;
					while ((temp = fileReader.readLine()) != null) {
						if (!temp.equals("")) {
							authorProfile.addSelectedTopic(temp);
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			throw new NotADirectoryException();
		}
	}

	public PaperCollection getGroupedSubmissions() {
		return submittedPapers;
	}

	public Collection<AuthorPapers> getUserProfiles() {
		return authorProfiles;
	}

	public Collection<AuthorPapers> getSuggestions() {
		return suggestions;
	}
}