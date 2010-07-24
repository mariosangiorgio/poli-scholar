package applications.analyzer.twoPhaseClassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import applications.analyzer.BayesianDocumentClassifier;
import applications.analyzer.DocumentClassifier;
import applications.analyzer.DocumentClassifierType;

public class TwoPhaseClassifier {
	private Submissions submissions;

	public static void main(String[] args) throws Exception {
		TwoPhaseClassifier twoPhaseClassifier = null;

		String classifierFile = "classifier";
		boolean train = false;
		File file = new File(classifierFile);

		if (train || !file.exists()) {
			twoPhaseClassifier = TwoPhaseClassifier.train("plaintext/model",
					classifierFile);
		} else {
			twoPhaseClassifier = TwoPhaseClassifier.load(classifierFile);
		}

		twoPhaseClassifier
				.classifySubmissions("plaintext/automaticBidding/submissions");
		int minimumNumberOfPapers = 20;
		int maximumNumberOfPapers = (int) Math.floor(.5d * twoPhaseClassifier
				.getTotalSubmissions());
		float topicCoverage = .75f;
		twoPhaseClassifier.classifyReviewers(
				"plaintext/automaticBidding/reviewers", topicCoverage,
				minimumNumberOfPapers, maximumNumberOfPapers);
	}

	private int getTotalSubmissions() {
		return submissions.getTotalSubmissions();
	}

	private DocumentClassifier classifier;

	public static TwoPhaseClassifier train(String dataSource,
			String classifierFile) {
		TwoPhaseClassifier twoPhaseClassifier = new TwoPhaseClassifier();

		twoPhaseClassifier.classifier = BayesianDocumentClassifier
				.getFromTrainingSet(DocumentClassifierType.NaiveBayesian,
						dataSource);
		twoPhaseClassifier.classifier.save(classifierFile);
		return twoPhaseClassifier;
	}

	private static TwoPhaseClassifier load(String classifierFile) {
		TwoPhaseClassifier twoPhaseClassifier = new TwoPhaseClassifier();
		twoPhaseClassifier.classifier = BayesianDocumentClassifier
				.load(classifierFile);
		return twoPhaseClassifier;
	}

	public void classifySubmissions(String path) throws NotADirectoryException,
			FileNotFoundException {
		Collection<File> documents = getDirectoryContent(path);
		submissions = new Submissions();
		for (File document : documents) {
			String documentContent = getFileContent(document);
			String category = classifier.classify(documentContent);
			submissions.addSubmissionToClass(category, document.getName());
		}
		for (String category : submissions.getCategories()) {
			System.out.println(category);
			for (String document : submissions.getPapersOfCategory(category)) {
				System.out.println("\t" + document);
			}
		}
	}

	public void classifyReviewers(String path, float maximumCoverage,
			int minimumNumberOfSuggestion, int maximumNumberOfSuggestions)
			throws NotADirectoryException, FileNotFoundException {
		FileWriter outputFile = null;
		try {
			outputFile = new FileWriter("proposedBids");
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collection<File> reviewers = getSubDirectories(path);
		for (File reviewerDirectory : reviewers) {
			// Classification of each reviewer
			Reviewer reviewer = new Reviewer(reviewerDirectory.getName());
			Collection<File> papers = getDirectoryContent(reviewerDirectory);
			for (File paper : papers) {
				String paperContent = getFileContent(paper);
				String category = classifier.classify(paperContent);
				reviewer.addArticleCategory(category);
			}

			try {
				outputFile.write(reviewer.getName() + "\t");
				System.out.println(reviewer.getName());

				Vector<Category> selectedCategories = new Vector<Category>();
				int totalReviewerPapers = reviewer.getTotalPapers();
				int pickedPapers = 0;
				float coverage = 0;
				for (Category category : reviewer.getSortedCategories()) {
					if (pickedPapers < minimumNumberOfSuggestion
							|| (pickedPapers < maximumNumberOfSuggestions && coverage < maximumCoverage)) {
						selectedCategories.add(category);
						pickedPapers += submissions
								.getSubmissionsCount(category.getName());
						coverage += (float) category.getCount()
								/ totalReviewerPapers;
					} else {
						break;
					}
				}
				if (pickedPapers > maximumNumberOfSuggestions) {
					selectedCategories.remove(selectedCategories.size() - 1);
				}

				// Output both to reviewer file and bid file
				FileWriter bidFile = new FileWriter("bids/"
						+ reviewer.getName() + ".txt");
				bidFile.write("Bids of " + reviewer.getName() + "\n");

				for (Category category : selectedCategories) {
					System.out.println("\t" + category.getName() + "\t"
							+ category.getCount());
					for (Integer paperNumber : submissions
							.getSubmissionsOfCategory(category.getName())) {
						outputFile.write(paperNumber + "\t");
					}

					bidFile.write("Topic: " + category.getName() + "\n");
					for (String file : submissions
							.getSubmissionFilesByCategory(category.getName())) {
						bidFile.write(file + "\n");
						bidFile
								.write("Abstract:\n"
										+ loadAbstractForFile(file));
						bidFile.write("\n");
					}
					bidFile.write("\n\n");
				}
				bidFile.close();

				outputFile.write("\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String loadAbstractForFile(String file) {
		StringBuffer temp = new StringBuffer();
		try {
			FileReader reader = new FileReader(
					"plaintext/automaticBidding/submissions/" + file);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				temp.append(line + "\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp.toString();
	}

	public String getFileContent(File file) throws FileNotFoundException {
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(fileReader);
		StringBuffer buffer = new StringBuffer();
		String temp;
		try {
			while ((temp = reader.readLine()) != null) {
				buffer.append(temp);
				buffer.append(" ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer.toString();
	}

	public Collection<File> getDirectoryContent(String rootDirectory)
			throws NotADirectoryException {
		File file = new File(rootDirectory);
		return getDirectoryContent(file);
	}

	public Collection<File> getDirectoryContent(File rootDirectory)
			throws NotADirectoryException {
		Collection<File> files = new Vector<File>();
		if (!rootDirectory.isDirectory()) {
			throw new NotADirectoryException();
		}
		for (File file : rootDirectory.listFiles()) {
			if (file.isFile() && !file.isHidden()) {
				files.add(file);
			}
		}
		return files;
	}

	public Collection<File> getSubDirectories(String rootDirectory)
			throws NotADirectoryException {
		File file = new File(rootDirectory);
		return getSubDirectories(file);
	}

	public Collection<File> getSubDirectories(File rootDirectory)
			throws NotADirectoryException {
		Collection<File> files = new Vector<File>();
		if (!rootDirectory.isDirectory()) {
			throw new NotADirectoryException();
		}
		for (File file : rootDirectory.listFiles()) {
			if (file.isDirectory() && !file.isHidden()) {
				files.add(file);
			}
		}
		return files;
	}
}
