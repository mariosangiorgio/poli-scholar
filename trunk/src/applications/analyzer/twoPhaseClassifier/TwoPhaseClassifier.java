package applications.analyzer.twoPhaseClassifier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

import applications.analyzer.BayesianDocumentClassifier;
import applications.analyzer.DocumentClassifier;
import applications.analyzer.DocumentClassifierType;

public class TwoPhaseClassifier {

	public static void main(String[] args) throws Exception {
		// Getting the classifier from the clustered data-set
		DocumentClassifier classifier;
		String classifierFile = "twoPhaseClassifier";
		boolean train = false;
		File file = new File(classifierFile);

		if (train || !file.exists()) {
			classifier = BayesianDocumentClassifier.getFromTrainingSet(
					DocumentClassifierType.NaiveBayesian, "plaintext/model");
			classifier.save(classifierFile);
		} else {
			classifier = BayesianDocumentClassifier.load(classifierFile);
		}
		// Classification of each submission
		Collection<File> documents = getDirectoryContent("plaintext/automaticBidding/submissions");
		Submissions submissions = new Submissions();
		for (File document : documents) {
			String documentContent = getFileContent(document);
			String category = classifier.classify(documentContent);
			submissions.addSubmissionToClass(category, document.getName());
		}
		for(String category : submissions.getCategories()){
			System.out.println(category);
			for(String document : submissions.getPapersOfCategory(category)){
				System.out.println("\t"+document);
			}
		}

		// Classification of each reviewer
	}

	public static String getFileContent(File file) throws FileNotFoundException{
		FileReader fileReader = new FileReader(file);
		BufferedReader reader = new BufferedReader(
				fileReader);
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
	
	public static Collection<File> getDirectoryContent(String rootDirectory) throws NotADirectoryException{
		File file = new File(rootDirectory);
		return getDirectoryContent(file);
	}
	public static Collection<File> getDirectoryContent(File rootDirectory)
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
}
