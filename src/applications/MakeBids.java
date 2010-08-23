package applications;

import java.io.File;

import it.polimi.bidding.Bidder;
import it.polimi.bidding.OutputWriter;
import it.polimi.data.io.NotADirectoryException;

public class MakeBids {
	// Application settings
	private boolean train = false;
	private boolean generateAuthorProfiles = false;
	private boolean loadEditedProfiles = false;
	private boolean groupSubmissions = false;
	private boolean getSuggestions = false;

	private String classifierFile = "classifier";
	private String submissionAbstractPaths = "papers/abstracts/submissions";
	private String reviewersAbstractsPaths = "papers/abstracts/reviewers";
	private float topicCoverage = .85f;
	private int numberOfPapersToPick = 20;

	// Output to file
	private String outputDirectoryName = "results";
	private String submissionGroupOutputFile = outputDirectoryName
			+ "/papersByTopic.txt";
	private String profilesDirectory = outputDirectoryName + "/profiles";
	private String suggestionFolder = outputDirectoryName + "/bids";

	public MakeBids(boolean train, boolean generateAuthorProfiles, boolean loadEditedProfiles,
			boolean groupSubmissions, boolean getSuggestions) {
		this.train = train;
		this.generateAuthorProfiles = generateAuthorProfiles;
		this.loadEditedProfiles = loadEditedProfiles;
		this.getSuggestions = getSuggestions;
		this.groupSubmissions = groupSubmissions;
	}

	public void getBids() throws NotADirectoryException {
		OutputWriter outputWriter = new OutputWriter();
		File outputDirectory = new File(outputDirectoryName);
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		Bidder bidder = new Bidder();

		bidder.loadClassifier(train, classifierFile);
		
		if(generateAuthorProfiles){
			System.out.println("Generating reviewers' profiles");
			bidder.generateAuthorProfiles(reviewersAbstractsPaths);
			outputWriter.writeAuthorProfiles(profilesDirectory, bidder
					.getUserProfiles(), topicCoverage);
			System.out.println("DONE. Profiles are available in the the "
					+ profilesDirectory + " directory\n");
		}
		if (loadEditedProfiles) {
			System.out.println("Loading profiles from files");
			bidder.loadAuthorProfiles(profilesDirectory,
					reviewersAbstractsPaths);
			System.out.println("DONE. Profiles loaded from "
					+ profilesDirectory + "\n");
		}

		if (groupSubmissions || getSuggestions) {
			System.out.println("Grouping submitted papers");
			bidder.groupSubmissions(submissionAbstractPaths);
			outputWriter.writeGroupedSubmission(submissionGroupOutputFile,
					bidder.getGroupedSubmissions());
			System.out.println("DONE. Groups are stored in the "
					+ submissionGroupOutputFile + " file\n");
		}

		if (getSuggestions) {
			System.out.println("Generating bid suggestions");
			bidder.generateBids(topicCoverage, numberOfPapersToPick);
			outputWriter.writeAuthorSuggestions(suggestionFolder, bidder
					.getSuggestions());
			System.out.println("DONE. Suggested bids are available in the the "
					+ suggestionFolder + " directory");
		}
	}
}
