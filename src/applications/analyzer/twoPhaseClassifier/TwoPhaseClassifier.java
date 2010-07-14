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
		TwoPhaseClassifier twoPhaseClassifier = null;
		
		String classifierFile = "twoPhaseClassifier";
		boolean train = false;
		File file = new File(classifierFile);

		if (train || !file.exists()) {
			twoPhaseClassifier = TwoPhaseClassifier.train("plaintext/model",classifierFile);
		} else {
			twoPhaseClassifier = TwoPhaseClassifier.load(classifierFile);
		}
		
		twoPhaseClassifier.classifySubmissions("plaintext/automaticBidding/submissions");
		twoPhaseClassifier.classifyReviewers("plaintext/automaticBidding/reviewers",2);		
	}
	
	
	private DocumentClassifier classifier;
	
	public static TwoPhaseClassifier train(String dataSource, String classifierFile){
		TwoPhaseClassifier twoPhaseClassifier = new TwoPhaseClassifier();
		
		twoPhaseClassifier.classifier = BayesianDocumentClassifier.getFromTrainingSet(
				DocumentClassifierType.NaiveBayesian, dataSource);
		twoPhaseClassifier.classifier.save(classifierFile);
		return twoPhaseClassifier;
	}
	
	private static TwoPhaseClassifier load(String classifierFile) {
		TwoPhaseClassifier twoPhaseClassifier = new TwoPhaseClassifier();
		twoPhaseClassifier.classifier = BayesianDocumentClassifier.load(classifierFile);
		return twoPhaseClassifier;
	}
	
	public void classifySubmissions(String path) throws NotADirectoryException, FileNotFoundException{
		Collection<File> documents = getDirectoryContent(path);
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
	}
	
	public void classifyReviewers(String path,int numberOfCategories) throws NotADirectoryException, FileNotFoundException{
		Collection<File> reviewers = getSubDirectories(path);
		for(File reviewerDirectory : reviewers){
			// Classification of each reviewer
			Reviewer reviewer = new Reviewer(reviewerDirectory.getName());
			Collection<File> papers = getDirectoryContent(reviewerDirectory);
			for(File paper : papers){
				String paperContent = getFileContent(paper);
				String category = classifier.classify(paperContent);
				reviewer.addArticleCategory(category);
			}
			System.out.println(reviewer.getName());
			for(Category category : reviewer.getTopCategories(numberOfCategories)){
				System.out.println("\t"+category.getName()+"\t"+category.getCount());
			}
		}
	}
	
	public String getFileContent(File file) throws FileNotFoundException{
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
	
	public Collection<File> getDirectoryContent(String rootDirectory) throws NotADirectoryException{
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
	
	public Collection<File> getSubDirectories(String rootDirectory) throws NotADirectoryException{
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