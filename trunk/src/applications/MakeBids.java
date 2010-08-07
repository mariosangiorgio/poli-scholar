package applications;

import java.io.File;

import it.polimi.bidding.Bidder;
import it.polimi.bidding.OutputWriter;
import it.polimi.data.io.NotADirectoryException;

public class MakeBids {
	public static void main(String[] args) throws NotADirectoryException {
		// Application settings
		boolean train = false;
		String classifierFile = "classifier";
		String submissionAbstractPaths = "papers/abstracts/submissions";
		String reviewersAbstractsPaths = "papers/abstracts/reviewers";
		float topicCoverage = .85f;
		int numberOfPapersToPick = 20;

		//Output to file
		String outputDirectoryName			= "results";
		String submissionGroupOutputFile	= outputDirectoryName+"/papersByTopic.txt";
		String profilesOutputDirectory		= outputDirectoryName+"/profiles";
		String suggestionFolder				= outputDirectoryName+"/bids";
		OutputWriter outputWriter = new OutputWriter();
		File outputDirectory = new File(outputDirectoryName);
		if(!outputDirectory.exists()){
			outputDirectory.mkdirs();
		}

		Bidder bidder = new Bidder();

		bidder.loadClassifier(train, classifierFile);

		System.out.println("Grouping submitted papers");
		bidder.groupSubmissions(submissionAbstractPaths);
		outputWriter.writeGroupedSubmission(submissionGroupOutputFile,bidder.getGroupedSubmissions());
		System.out.println("DONE. Groups are stored in the "+submissionGroupOutputFile+" file\n");
		
		System.out.println("Generating reviewers' profiles");
		bidder.generateAuthorProfiles(reviewersAbstractsPaths);
		outputWriter.writeAuthorProfiles(profilesOutputDirectory,bidder.getUserProfiles(),topicCoverage);		
		System.out.println("DONE. Profiles are available in the the "+profilesOutputDirectory+" directory\n");

		System.out.println("Generating bid suggestions");
		bidder.generateBids(topicCoverage, numberOfPapersToPick);
		outputWriter.writeAuthorSuggestions(suggestionFolder,bidder.getSuggestions());		
		System.out.println("DONE. Suggested bids are available in the the "+suggestionFolder+" directory");
	}
}
