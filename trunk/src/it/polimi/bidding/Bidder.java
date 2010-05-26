package it.polimi.bidding;

import it.polimi.utils.TextStripper;

import java.io.File;
import java.io.IOException;

import applications.analyzer.DocumentClassifier;
import applications.analyzer.DocumentClassifierType;

public class Bidder {
	private DocumentClassifier classifier;
	private final static String pathToReviewers   = "automaticBidding/reviewers";
	private final static String pathToSubmissions = "automaticBidding/submissions";
	
	public static void main(String[] args){
		Bidder bidder = new Bidder();
		bidder.generateBidding();
	}
	
	public Bidder(){
		//TODO: Find a way to have the papers weighted according to their age
		classifier = DocumentClassifier.getFromTrainingSet(DocumentClassifierType.NaiveBayesian,pathToReviewers);
	}
	
	public void generateBidding(){
		File submissionsFolder = new File(pathToSubmissions);
		
		System.out.println();
		System.out.println(" Biddings:");
		for(String documentName : submissionsFolder.list()){
			File document = new File(pathToSubmissions+"/"+documentName);
			
			if(!documentName.startsWith(".") && document.isFile()){
				try {
					String fullText = TextStripper.getFullText(document);
					String classification = classifier.classify(fullText);
					
					System.out.println(documentName+"\t"+classification);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}
}
