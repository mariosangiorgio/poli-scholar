package applications.validation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultValidator {

	public static void main(String[] args) throws IOException {

		// Reading actual bids file
		FileReader reader = new FileReader("icsmbids.txt");
		BufferedReader bufferedReader = new BufferedReader(reader);

		Pattern bidderName = Pattern.compile("Bid of ([^\n]*)");
		Pattern paperNumber = Pattern
				.compile("(Yes|Maybe|Conflict)?\\s*(\\d+)\\.");

		HashMap<String, Reviewer> reviewers = new HashMap<String, Reviewer>();

		String temp;
		Reviewer tempReviewer = null;
		KindOfBid kindOfBid = null;
		while ((temp = bufferedReader.readLine()) != null) {
			Matcher bidderNameMatcher = bidderName.matcher(temp);
			if (bidderNameMatcher.find()) {
				tempReviewer = new Reviewer(bidderNameMatcher.group(1));
				reviewers.put(tempReviewer.getName(), tempReviewer);
			}
			Matcher paperMatcher = paperNumber.matcher(temp);
			if (paperMatcher.find()) {
				if (paperMatcher.group(1) != null) {
					kindOfBid = KindOfBid.valueOf(paperMatcher.group(1)
							.toLowerCase());
				}
				tempReviewer.addBid(Integer.parseInt(paperMatcher.group(2)),
						kindOfBid);
			}
		}
		bufferedReader.close();
		reader.close();

		// Reading submissions file
		reader = new FileReader("proposedBids");
		bufferedReader = new BufferedReader(reader);
		String proposedBid;
		while ((proposedBid = bufferedReader.readLine()) != null) {
			String[] papers = proposedBid.split("\t");
			String author = papers[0];
			tempReviewer = reviewers.get(author);
			if (tempReviewer == null) {
				System.err.println(author);
			} else {
				for (int i = 1; i < papers.length; i++) {
					tempReviewer
							.addSuggestedPapers(Integer.parseInt(papers[i]));
				}
			}
		}

		CSVWriter.addNewLine("Reviewer,"+
							 "Suggested Papers,"+
							 "Actually Bid Papers,"+
							 "Yes Retrieved,"+
							 "Yes Actual,"+
							 "Maybe Retrieved,"+
							 "Maybe Actual,"+
							 "Conflict Retrieved,"+
							 "Conflict Actual");
		for (Reviewer reviewer : reviewers.values()) {
			System.out.println(reviewer.getName());
			reviewer.printSummary();
			System.out.println();
		}
		CSVWriter.close();
	}

}
